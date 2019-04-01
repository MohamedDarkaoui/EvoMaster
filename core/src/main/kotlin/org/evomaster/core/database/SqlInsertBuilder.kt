package org.evomaster.core.database

import org.evomaster.client.java.controller.api.dto.database.operations.DatabaseCommandDto
import org.evomaster.client.java.controller.api.dto.database.operations.QueryResultDto
import org.evomaster.client.java.controller.api.dto.database.schema.DatabaseType
import org.evomaster.client.java.controller.api.dto.database.schema.DbSchemaDto
import org.evomaster.core.database.schema.Column
import org.evomaster.core.database.schema.ColumnDataType
import org.evomaster.core.database.schema.ForeignKey
import org.evomaster.core.database.schema.Table
import org.evomaster.core.remote.service.RemoteController
import org.evomaster.core.search.gene.Gene
import org.evomaster.core.search.gene.ImmutableDataHolderGene
import org.evomaster.core.search.gene.SqlPrimaryKeyGene
import java.lang.IllegalStateException


class SqlInsertBuilder(
        schemaDto: DbSchemaDto,
        private val dbExecutor: DatabaseExecutor? = null
) {

    /**
     * The counter used to give unique IDs to the DbActions.
     * Should not be negative
     */
    private var counter: Long = 0

    /*
        All the objects here are immutable
     */
    private val tables = mutableMapOf<String, Table>()

    private val databaseType: DatabaseType

    private val name: String


    init {
        /*
            Here, we need to transform (and validate) the input DTO
            into immutable domain objects
         */
        if(counter < 0){
            throw IllegalArgumentException("Invalid negative counter: $counter")
        }

        if (schemaDto.databaseType == null) {
            throw IllegalArgumentException("Undefined database type")
        }
        if (schemaDto.name == null) {
            throw IllegalArgumentException("Undefined schema name")
        }

        databaseType = schemaDto.databaseType
        name = schemaDto.name

        val tableToColumns = mutableMapOf<String, MutableSet<Column>>()
        val tableToForeignKeys = mutableMapOf<String, MutableSet<ForeignKey>>()

        for (t in schemaDto.tables) {

            val columns = mutableSetOf<Column>()

            for (c in t.columns) {

                if (!c.table.equals(t.name, ignoreCase = true)) {
                    throw IllegalArgumentException("Column in different table: ${c.table}!=${t.name}")
                }

                val column = Column(
                        name = c.name,
                        size = c.size,
                        type = ColumnDataType.valueOf(c.type),
                        primaryKey = c.primaryKey,
                        nullable = c.nullable,
                        unique = c.unique,
                        autoIncrement = c.autoIncrement,
                        foreignKeyToAutoIncrement =  c.foreignKeyToAutoIncrement,
                        lowerBound = c.lowerBound,
                        upperBound = c.upperBound
                )

                columns.add(column)
            }

            tableToColumns[t.name] = columns
        }

        for (t in schemaDto.tables) {

            val fks = mutableSetOf<ForeignKey>()

            for (f in t.foreignKeys) {

                val targetTable = tableToColumns[f.targetTable]
                        ?: throw IllegalArgumentException("Foreign key for non-existent table ${f.targetTable}")

                val sourceColumns = mutableSetOf<Column>()


                for (cname in f.sourceColumns) {
                    //TODO wrong check, as should be based on targetColumns, when we ll introduce them
                    //val c = targetTable.find { it.name.equals(cname, ignoreCase = true) }
                    //        ?: throw IllegalArgumentException("Issue in foreign key: table ${f.targetTable} does not have a column called $cname")

                    val c = tableToColumns[t.name]!!.find { it.name.equals(cname, ignoreCase = true) }
                            ?: throw IllegalArgumentException("Issue in foreign key: table ${t.name} does not have a column called $cname")
                    sourceColumns.add(c)
                }

                fks.add(ForeignKey(sourceColumns, f.targetTable))
            }

            tableToForeignKeys[t.name] = fks
        }

        for (t in schemaDto.tables) {
            val table = Table(t.name, tableToColumns[t.name]!!, tableToForeignKeys[t.name]!!)
            tables[t.name] = table
        }
    }

    private fun getTable(tableName: String): Table {
        return tables[tableName]
                ?: tables[tableName.toUpperCase()]
                ?: throw IllegalArgumentException("No table called $tableName")
    }

    /**
     * Create a SQL insertion operation into the table called [tableName].
     * Use columns only from [columnNames], to avoid wasting resources in setting
     * non-used data.
     * Note: due to constraints (eg non-null), we might create data also for non-specified columns.
     *
     * If the table has non-null foreign keys to other tables, then create an insertion for those
     * as well. This means that more than one action can be returned here.
     *
     * Note: the created actions have default values. You might want to randomize its data before
     * using it.
     *
     * Note: names are case-sensitive, although some DBs are case-insensitive. To make
     * things even harder, there could be a mismatch in casing when inserting and then
     * reading back table/column names from schema :(
     * This is (should not) be a problem when running EM, but can be trickier when writing
     * test cases manually for EM
     */
    fun createSqlInsertionAction(tableName: String, columnNames: Set<String>): List<DbAction> {

        val table = getTable(tableName)

        val takeAll = columnNames.contains("*")

        if (takeAll && columnNames.size > 1) {
            throw IllegalArgumentException("Invalid column description: more than one entry when using '*'")
        }

        for (cn in columnNames) {
            if (cn != "*" && !table.columns.any { it.name == cn }) {
                throw IllegalArgumentException("No column called $cn in table $tableName")
            }
        }

        val selectedColumns = mutableSetOf<Column>()

        for (c in table.columns) {
            /*
                we need to take primaryKey even if autoIncrement.
                Point is, even if value will be set by DB, and so could skip it,
                we would still need a non-modifiable, non-printable Gene to
                store it, as we can have other Foreign Key genes pointing to it
             */

            if (takeAll || columnNames.contains(c.name) || !c.nullable || c.primaryKey) {
                //TODO are there also other constraints to consider?
                selectedColumns.add(c)
            }
        }

        val insertion = DbAction(table, selectedColumns, counter++)
        val actions = mutableListOf(insertion)

        for (fk in table.foreignKeys) {
            /*
                Assumption: in a valid Schema, this should never end up
                in a infinite loop?
             */
            val pre = createSqlInsertionAction(fk.targetTable, setOf())
            actions.addAll(0, pre)
        }

        return actions
    }


    /**
     * Check current state of database.
     * For each row, create a DbAction containing only Primary Keys
     * and immutable data
     */
    fun extractExistingPKs(): List<DbAction>{

        if(dbExecutor == null){
            throw IllegalStateException("No Database Executor registered for this object")
        }

        val list = mutableListOf<DbAction>()

        for(table in tables.values){

            val pks = table.columns.filter { it.primaryKey }

            if(pks.isEmpty()){
                /*
                    In some very special cases, it might happen that a table has no defined
                    primary key. It's rare, but it is technically legal.
                    We can just skip those tables.
                 */
                continue
            }

            val sql = "SELECT ${pks.map { it.name }.joinToString(",")} FROM ${table.name}"

            val dto = DatabaseCommandDto()
            dto.command = sql

            val result : QueryResultDto = dbExecutor.executeDatabaseCommandAndGetResults(dto)
                    ?: continue

            result.rows.forEach { r ->

                val id = counter++

                val genes = mutableListOf<Gene>()

                for(i in 0 until pks.size){
                    val pkName = pks[i].name
                    val inQuotes = pks[i].type.shouldBePrintedInQuotes()
                    val data = ImmutableDataHolderGene(pkName, r.columnData[i], inQuotes)
                    val pk = SqlPrimaryKeyGene(pkName, table.name, data, id)
                    genes.add(pk)
                }

                val action = DbAction(table, pks.toSet(), id, genes, true)
                list.add(action)
            }
        }

        return list
    }
}