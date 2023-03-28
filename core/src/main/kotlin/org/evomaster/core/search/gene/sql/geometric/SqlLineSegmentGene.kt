package org.evomaster.core.search.gene.sql.geometric

import org.evomaster.core.Lazy
import org.evomaster.core.search.gene.*
import org.evomaster.core.logging.LoggingUtil
import org.evomaster.core.search.service.Randomness
import org.evomaster.core.search.service.mutator.genemutation.AdditionalGeneMutationInfo
import org.evomaster.core.search.service.mutator.genemutation.SubsetGeneMutationSelectionStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqlLineSegmentGene(
    name: String,
    p: SqlPointGene = SqlPointGene("p"),
    q: SqlPointGene = SqlPointGene("q")
) : SqlAbstractGeometricGene(name, p, q) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(SqlLineSegmentGene::class.java)
    }

    override fun isLocallyValid() : Boolean{
        return getViewOfChildren().all { it.isLocallyValid() }
    }

    override fun copyContent(): Gene = SqlLineSegmentGene(
        name,
        p.copy() as SqlPointGene,
        q.copy() as SqlPointGene
    )

    override fun copyValueFrom(other: Gene): Boolean {
        if (other !is SqlLineSegmentGene) {
            throw IllegalArgumentException("Invalid gene type ${other.javaClass}")
        }
        val current = copy()
        val ok = this.p.copyValueFrom(other.p)
                && this.q.copyValueFrom(other.q)

        if (!ok || !isLocallyValid()){
            Lazy.assert { copyValueFrom(current) }
            return false
        }
        return true
    }

    override fun containsSameValueAs(other: Gene): Boolean {
        if (other !is SqlLineSegmentGene) {
            throw IllegalArgumentException("Invalid gene type ${other.javaClass}")
        }
        return this.p.containsSameValueAs(other.p)
                && this.q.containsSameValueAs(other.q)
    }

    override fun bindValueBasedOn(gene: Gene): Boolean {
        return when {
            gene is SqlLineSegmentGene -> {
                p.bindValueBasedOn(gene.p) &&
                        q.bindValueBasedOn(gene.q)
            }
            else -> {
                LoggingUtil.uniqueWarn(log, "cannot bind PointGene with ${gene::class.java.simpleName}")
                false
            }
        }
    }

    override fun customShouldApplyShallowMutation(
        randomness: Randomness,
        selectionStrategy: SubsetGeneMutationSelectionStrategy,
        enableAdaptiveGeneMutation: Boolean,
        additionalGeneMutationInfo: AdditionalGeneMutationInfo?
    ): Boolean {
        return false
    }

}