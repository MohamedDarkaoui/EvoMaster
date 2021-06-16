package org.evomaster.core.search.structuralelement.action

import io.swagger.parser.OpenAPIParser
import org.evomaster.core.problem.rest.RestActionBuilderV3
import org.evomaster.core.problem.rest.RestCallAction
import org.evomaster.core.problem.rest.param.BodyParam
import org.evomaster.core.search.Action
import org.evomaster.core.search.gene.ObjectGene
import org.evomaster.core.search.structuralelement.StructuralElementBaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object RestActionCluster{
    private val cluster : MutableMap<String, Action> = mutableMapOf()
    init {
        RestActionBuilderV3.addActionsFromSwagger(OpenAPIParser().readLocation("swagger/artificial/resource_test.json", null, null).openAPI, cluster)
    }

    fun getRestCallAction(path: String) : RestCallAction? = cluster[path] as? RestCallAction

}


class RestPostCallStructureTest : StructuralElementBaseTest(){
    override fun getStructuralElement(): RestCallAction{
        return RestActionCluster.getRestCallAction("POST:/v3/api/rfoo")?:throw IllegalStateException("cannot get the expected the action")
    }

    override fun getExpectedChildrenSize(): Int = 1

    @Test
    fun testTraverseBackIndex(){
        val root = getStructuralElement()
        assertEquals(root, root.getRoot())

        val floatValue = ((root.parameters[0] as BodyParam).gene as ObjectGene).fields[3]

        val path = listOf(0, 0, 3)
        assertEquals(floatValue, root.targetWithIndex(path))

        val actualPath = mutableListOf<Int>()
        floatValue.traverseBackIndex(actualPath)
        assertEquals(path, actualPath)

    }
}

class RestGetCallStructureTest : StructuralElementBaseTest(){
    override fun getStructuralElement(): RestCallAction{
        return RestActionCluster.getRestCallAction("GET:/v3/api/rfoo/{id}")?:throw IllegalStateException("cannot get the expected the action")
    }

    override fun getExpectedChildrenSize(): Int = 2
}


