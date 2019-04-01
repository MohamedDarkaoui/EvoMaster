package org.evomaster.core.problem.rest

import org.evomaster.core.Lazy
import org.evomaster.core.database.DbAction
import org.evomaster.core.database.DbActionUtils
import org.evomaster.core.search.Action
import org.evomaster.core.search.Individual
import org.evomaster.core.search.gene.Gene
import org.evomaster.core.search.gene.GeneUtils
import org.evomaster.core.search.service.Randomness
import org.evomaster.core.search.service.tracer.TraceableElement


class RestIndividual : Individual {

class RestIndividual(val actions: MutableList<RestAction>,
                     val sampleType: SampleType,
                     val dbInitialization: MutableList<DbAction> = mutableListOf(),
                     val usedObjects: UsedObjects = UsedObjects()
) : Individual() {

    val actions: MutableList<RestAction>
    val sampleType: SampleType
    val dbInitialization: MutableList<DbAction>

    constructor(actions: MutableList<RestAction>, sampleType: SampleType, dbInitialization: MutableList<DbAction>, description: String, traces : MutableList<out RestIndividual>) : super(description, traces){
        this.actions = actions
        this.sampleType = sampleType
        this.dbInitialization = dbInitialization
    }

    constructor(actions: MutableList<RestAction>, sampleType: SampleType, description: String) :   this(actions, sampleType, mutableListOf(), description, mutableListOf())
    constructor(actions: MutableList<RestAction>, sampleType: SampleType, dbInitialization: MutableList<DbAction> = mutableListOf()) : this(actions, sampleType, dbInitialization, sampleType.toString(), mutableListOf())
    constructor(actions: MutableList<RestAction>, sampleType: SampleType, description: String, traces : MutableList<out RestIndividual>) : this(actions, sampleType, mutableListOf<DbAction>(), description, traces)


    override fun copy(): Individual {
        return RestIndividual(
                actions.map { a -> a.copy() as RestAction } as MutableList<RestAction>,
                sampleType,
                dbInitialization.map { d -> d.copy() as DbAction } as MutableList<DbAction>,
                usedObjects.copy()
        )
    }

    override fun canMutateStructure(): Boolean {
        return sampleType == SampleType.RANDOM ||
                sampleType == SampleType.SMART_GET_COLLECTION
    }


    override fun seeGenes(filter: GeneFilter): List<out Gene> {

        return when (filter) {
            GeneFilter.ALL -> dbInitialization.flatMap(DbAction::seeGenes).plus(actions.flatMap(RestAction::seeGenes))
            GeneFilter.NO_SQL -> actions.flatMap(RestAction::seeGenes)
            GeneFilter.ONLY_SQL -> dbInitialization.flatMap(DbAction::seeGenes)

        }
    }

    /*
        TODO Tricky... should dbInitialization somehow be part of the size?
        But they are merged in a single operation in a single call...
        need to think about it
     */

    override fun size() = actions.size

    override fun seeActions(): List<out Action> {
        return actions
    }

    override fun seeInitializingActions(): List<Action> {
        return dbInitialization
    }

    override fun verifyInitializationActions(): Boolean {
        return DbActionUtils.verifyActions(dbInitialization.filterIsInstance<DbAction>())
    }


    override fun repairInitializationActions(randomness: Randomness) {

        /**
         * First repair SQL Genes (i.e. SQL Timestamps)
         */
        GeneUtils.repairGenes(this.seeGenes(Individual.GeneFilter.ONLY_SQL).flatMap { it.flatView() })

        /**
         * Now repair database constraints (primary keys, foreign keys, unique fields, etc.)
         */
        if (!verifyInitializationActions()) {
            DbActionUtils.repairBrokenDbActionsList(dbInitialization, randomness)
            Lazy.assert{verifyInitializationActions()}
        }
    }

    override fun next(description : String) : TraceableElement?{
        if(isCapableOfTracking()){
            val copyTraces = mutableListOf<RestIndividual>()
            if(!isRoot()){
                val size = getTrack()?.size?:0
                (0 until if(maxlength != -1 && size > maxlength - 1) maxlength-1  else size).forEach {
                    copyTraces.add(0, (getTrack()!![size-1-it] as RestIndividual).copy() as RestIndividual)
                }
            }
            copyTraces.add(this)
            return RestIndividual(
                    actions.map { a -> a.copy() as RestAction } as MutableList<RestAction>,
                    sampleType,
                    dbInitialization.map { d -> d.copy() as DbAction } as MutableList<DbAction>,
                    description,
                    copyTraces)
        }
        return copy()
    }

    override fun copy(withTrack: Boolean): RestIndividual {
        when(withTrack){
            false-> return copy() as RestIndividual
            else ->{
                getTrack()?:return copy() as RestIndividual
                val copyTraces = mutableListOf<RestIndividual>()
                getTrack()?.forEach {
                    copyTraces.add((it as RestIndividual).copy() as RestIndividual)
                }
                return RestIndividual(
                        actions.map { a -> a.copy() as RestAction } as MutableList<RestAction>,
                        sampleType,
                        dbInitialization.map { d -> d.copy() as DbAction } as MutableList<DbAction>,
                        getDescription(),
                        copyTraces
                )
            }
        }
    }

    /**
     * During mutation, the values used for parameters are changed, but the values attached to the respective used objects are not.
     * This function copies the new (mutated) values of the parameters into the respective used objects, to ensure that the objects and parameters are coherent.
     * The return value is true if everything went well, and false if some values could not be copied. It is there for debugging only.
     */
    fun enforceCoherence(): Boolean {

        //BMR: not sure I can use flatMap here. I am using a reference to the action object to get the relevant gene.
        actions.forEach { action ->
            action.seeGenes().forEach { gene ->
                try {
                    val innerGene = when (gene::class) {
                        OptionalGene::class -> (gene as OptionalGene).gene
                        DisruptiveGene::class -> (gene as DisruptiveGene<*>).gene
                        else -> gene
                    }
                    val relevantGene = usedObjects.getRelevantGene((action as RestCallAction), innerGene)
                    when (action::class) {
                        RestCallAction::class -> {
                            when (relevantGene::class) {
                                OptionalGene::class -> (relevantGene as OptionalGene).gene.copyValueFrom(innerGene)
                                DisruptiveGene::class -> (relevantGene as DisruptiveGene<*>).gene.copyValueFrom(innerGene)
                                ObjectGene::class -> relevantGene.copyValueFrom(innerGene)
                                else -> relevantGene.copyValueFrom(innerGene)
                            }
                        }
                    }
                }
                catch (e: Exception){
                    // TODO BMR: EnumGene is not handled well and ends up here.
                     return false
                }
            }
        }
        return true
    }
}