package org.evomaster.e2etests.spring.openapi.v3.jackson.extended

import com.foo.rest.examples.spring.openapi.v3.jackson.extended.ExtendedJacksonController
import org.evomaster.ci.utils.CIUtils
import org.evomaster.core.EMConfig
import org.evomaster.core.problem.rest.HttpVerb
import org.evomaster.e2etests.spring.openapi.v3.SpringTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ExtendedJacksonEMTest : SpringTestBase() {

    companion object {
        @BeforeAll
        @JvmStatic
        fun init() {
            initClass(ExtendedJacksonController())
            CIUtils.skipIfOnGA()
        }
    }

    @Test
    fun testRunEM() {

        runTestHandlingFlakyAndCompilation(
            "GeneratedExtendedJacksonEMTest",
            "org.foo.GeneratedExtendedJacksonEMTest",
            500,
            !CIUtils.isRunningGA(),
            { args: MutableList<String> ->

                args.add("--externalServiceIPSelectionStrategy")
                args.add("USER")
                args.add("--externalServiceIP")
                args.add("127.0.0.4")
                args.add("--instrumentMR_NET")
                args.add("true")

                val solution = initAndRun(args)

                Assertions.assertTrue(solution.individuals.size >= 1)
                assertHasAtLeastOne(solution, HttpVerb.GET, 200, "/api/jackson/byte/{s}", "Working")
            }, 3
        )
    }
}