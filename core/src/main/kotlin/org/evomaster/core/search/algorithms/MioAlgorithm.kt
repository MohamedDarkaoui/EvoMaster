package org.evomaster.core.search.algorithms

import org.evomaster.core.EMConfig
import org.evomaster.core.Lazy
import org.evomaster.core.search.Individual
import org.evomaster.core.search.service.SearchAlgorithm
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import org.json.JSONArray
import org.json.JSONObject

/**
 * Many Independent Objective (MIO) Algorithm
 */
class MioAlgorithm<T> : SearchAlgorithm<T>() where T : Individual {
    companion object {
        private val logger = LoggerFactory.getLogger(MioAlgorithm::class.java)
        private const val FILE_PATH = "targets.json"
    }

    override fun getType(): EMConfig.Algorithm {
        return EMConfig.Algorithm.MIO
    }

    override fun setupBeforeSearch() {
        // Nothing needs to be done before starting the search
    }

    override fun searchOnce() {
        val coveredTargetIds = archive.coveredTargets()
        val descriptiveIds = coveredTargetIds.map { archive.idMapper.getDescriptiveId(it) }

        // Save the last seen targets to a JSON file
        saveTargetsToJsonFile(descriptiveIds, archive.coveredStatisticsBySeededTests?.coveredTargets ?: emptyList())

        //logger.info("Covered targets: {}", descriptiveIds)

        val randomP = apc.getProbRandomSampling()

        if (archive.isEmpty()
            || sampler.hasSpecialInit()
            || randomness.nextBoolean(randomP)) {

            val ind = sampler.sample()

            Lazy.assert { ind.isInitialized() && ind.searchGlobalState!= null }

            ff.calculateCoverage(ind)?.run {

                archive.addIfNeeded(this)
                sampler.feedback(this)

                if (sampler.numberOfNotExecutedSeededIndividuals() > 0) {
                    println("Individuals left in seed: ${sampler.numberOfNotExecutedSeededIndividuals()}")
                }
                if (sampler.isLastSeededIndividual()) {
                    archive.archiveCoveredStatisticsBySeededTests()

                    println("Individuals left in seed: ${sampler.numberOfNotExecutedSeededIndividuals()}")
                    println(archive.coveredStatisticsBySeededTests?.coveredTargets.toString())

                    // Save the targets covered by seed to the JSON file
                    saveTargetsToJsonFile(descriptiveIds, archive.coveredStatisticsBySeededTests?.coveredTargets ?: emptyList())

                    //logger.info("Targets covered by seed: {}", descriptiveIds)
                }
            }

            return
        }

        val ei = archive.sampleIndividual()

        val nMutations = apc.getNumberOfMutations()

        getMutatator().mutateAndSave(nMutations, ei, archive)
    }

    private fun saveTargetsToJsonFile(lastSeenTargets: List<String>, seededTargets: List<Int>) {
        val seededDescriptiveIds = seededTargets.map { archive.idMapper.getDescriptiveId(it) }
        val jsonObject = JSONObject().apply {
            put("lastSeen", JSONArray(lastSeenTargets))
            put("seeded", JSONArray(seededDescriptiveIds))
        }

        try {
            PrintWriter(File(FILE_PATH)).use { out ->
                out.println(jsonObject.toString(4))
            }
            logger.info("Saved targets to JSON file: $FILE_PATH")
        } catch (e: IOException) {
            logger.error("Failed to save targets to JSON file", e)
        }
    }
}
