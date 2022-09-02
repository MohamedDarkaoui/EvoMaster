package org.evomaster.core.search.gene.sql

import org.evomaster.core.logging.LoggingUtil
import org.evomaster.core.output.OutputFormat
import org.evomaster.core.search.gene.*
import org.evomaster.core.search.gene.collection.ArrayGene
import org.evomaster.core.search.gene.root.CompositeFixedGene
import org.evomaster.core.search.gene.utils.GeneUtils
import org.evomaster.core.search.gene.utils.GeneUtils.SINGLE_APOSTROPHE_PLACEHOLDER
import org.evomaster.core.search.service.AdaptiveParameterControl
import org.evomaster.core.search.service.Randomness
import org.evomaster.core.search.service.mutator.MutationWeightControl
import org.evomaster.core.search.service.mutator.genemutation.AdditionalGeneMutationInfo
import org.evomaster.core.search.service.mutator.genemutation.SubsetGeneSelectionStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Bit strings are strings of 1's and 0's.
 */
class SqlBitStringGene(
    /**
         * The name of this gene
         */
        name: String,

    val minSize: Int = 0,

    val maxSize: Int = ArrayGene.MAX_SIZE,

    private val booleanArrayGene: ArrayGene<BooleanGene> = ArrayGene(name, template = BooleanGene(name), minSize = minSize, maxSize = maxSize)

) :  CompositeFixedGene(name, mutableListOf( booleanArrayGene)) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(SqlBitStringGene::class.java)

        const val TRUE_VALUE = "1"

        const val FALSE_VALUE = "0"

        const val EMPTY_STR = ""
    }

    override fun isLocallyValid() : Boolean{
        return getViewOfChildren().all { it.isLocallyValid() }
    }

    override fun randomize(randomness: Randomness, tryToForceNewValue: Boolean) {
        booleanArrayGene.randomize(randomness, tryToForceNewValue)
    }

    override fun candidatesInternalGenes(randomness: Randomness, apc: AdaptiveParameterControl,  selectionStrategy: SubsetGeneSelectionStrategy, enableAdaptiveGeneMutation: Boolean, additionalGeneMutationInfo: AdditionalGeneMutationInfo?): List<Gene> {
       return listOf(booleanArrayGene)
    }


    override fun getValueAsPrintableString(previousGenes: List<Gene>, mode: GeneUtils.EscapeMode?, targetFormat: OutputFormat?, extraCheck: Boolean): String {
        return buildString {
            append("B$SINGLE_APOSTROPHE_PLACEHOLDER")
            append(booleanArrayGene.getViewOfChildren().map { g ->
                if ((g as BooleanGene).value) TRUE_VALUE else FALSE_VALUE
            }.joinToString(EMPTY_STR))
            append(SINGLE_APOSTROPHE_PLACEHOLDER)
        }
    }

    override fun copyValueFrom(other: Gene) {
        if (other !is SqlBitStringGene) {
            throw IllegalArgumentException("Invalid gene type ${other.javaClass}")
        }
        booleanArrayGene.copyValueFrom(other.booleanArrayGene)
    }

    override fun containsSameValueAs(other: Gene): Boolean {
        if (other !is SqlBitStringGene) {
            throw IllegalArgumentException("Invalid gene type ${other.javaClass}")
        }
        return booleanArrayGene.containsSameValueAs(other.booleanArrayGene)
    }

    override fun innerGene(): List<Gene> {
        return listOf(booleanArrayGene)
    }

    override fun bindValueBasedOn(gene: Gene): Boolean {
        if (gene is SqlBitStringGene) {
            return booleanArrayGene.bindValueBasedOn(gene.booleanArrayGene)
        }
        LoggingUtil.uniqueWarn(log, "cannot bind SqlBitstringGene with ${gene::class.java.simpleName}")
        return false
    }



    override fun copyContent() = SqlBitStringGene(name, minSize = minSize, maxSize = maxSize, booleanArrayGene.copy() as ArrayGene<BooleanGene>)

    override fun shallowMutate(
            randomness: Randomness,
            apc: AdaptiveParameterControl,
            mwc: MutationWeightControl,
            selectionStrategy: SubsetGeneSelectionStrategy,
            enableAdaptiveGeneMutation: Boolean,
            additionalGeneMutationInfo: AdditionalGeneMutationInfo?
    ): Boolean {
        this.randomize(randomness, true)
        return true
    }


}