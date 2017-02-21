package org.evomaster.core.search.algorithms.onemax

import org.evomaster.core.search.EvaluatedIndividual
import org.evomaster.core.search.service.FitnessFunction
import org.evomaster.core.search.FitnessValue


class OneMaxFitness : FitnessFunction<OneMaxIndividual>() {

    override fun doCalculateCoverage(individual: OneMaxIndividual)
            : EvaluatedIndividual<OneMaxIndividual> {

        val fv = FitnessValue()

        (0 until individual.n)
                .forEach { fv.updateTarget(it, individual.getValue(it)) }

        return EvaluatedIndividual(fv, individual.copy() as OneMaxIndividual, listOf())
    }
}