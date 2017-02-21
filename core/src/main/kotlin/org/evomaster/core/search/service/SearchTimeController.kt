package org.evomaster.core.search.service

import com.google.inject.Inject
import org.evomaster.core.EMConfig

/**
 * Class used to keep track of passing of time during the search.
 * This is needed for deciding when to stop the search, and for
 * other time-related properties like adaptive parameter control.
 */
class SearchTimeController {

    @Inject
    private lateinit var configuration: EMConfig


    var evaluatedIndividuals = 0
        private set

    var searchStarted = false
        private set

    var lastImprovement = -1
        private set

    fun startSearch(){
        searchStarted = true
    }

    fun newEvaluation(){
        evaluatedIndividuals++
    }

    fun newCoveredTarget(){
        lastImprovement = evaluatedIndividuals
    }

    fun shouldContinueSearch(): Boolean{

        if(configuration.stoppingCriterion.equals(
                EMConfig.StoppingCriterion.FITNESS_EVALUATIONS))    {

            return evaluatedIndividuals < configuration.maxFitnessEvaluations
        }

        return false //TODO
    }

    /**
     * Return how much percentage [0,1] of search budget has been used so far
     */
    fun percentageUsedBudget() : Double{

        if(configuration.stoppingCriterion.equals(
                EMConfig.StoppingCriterion.FITNESS_EVALUATIONS))    {

            return evaluatedIndividuals.toDouble() / configuration.maxFitnessEvaluations.toDouble()
        } else {
            return -1.0; //TODO
        }
    }
}