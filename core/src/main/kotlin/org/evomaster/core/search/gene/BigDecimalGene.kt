package org.evomaster.core.search.gene

import org.evomaster.core.Lazy
import org.evomaster.core.logging.LoggingUtil
import org.evomaster.core.output.OutputFormat
import org.evomaster.core.search.gene.NumberMutatorUtils.getFormattedValue
import org.evomaster.core.search.gene.NumberMutatorUtils.handleMinMaxInConstructor
import org.evomaster.core.search.gene.NumberMutatorUtils.mutateFloatingPointNumber
import org.evomaster.core.search.gene.NumberMutatorUtils.mutateLong
import org.evomaster.core.search.gene.NumberMutatorUtils.randomizeDouble
import org.evomaster.core.search.gene.NumberMutatorUtils.randomizeLong
import org.evomaster.core.search.gene.sql.SqlPrimaryKeyGene
import org.evomaster.core.search.service.AdaptiveParameterControl
import org.evomaster.core.search.service.Randomness
import org.evomaster.core.search.service.mutator.MutationWeightControl
import org.evomaster.core.search.service.mutator.genemutation.AdditionalGeneMutationInfo
import org.evomaster.core.search.service.mutator.genemutation.SubsetGeneSelectionStrategy
import org.evomaster.core.utils.NumberCalculationUtil.getMiddle
import org.evomaster.core.utils.NumberCalculationUtil.upperBound
import org.evomaster.core.utils.NumberCalculationUtil.valueWithPrecisionAndScale
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

/**
 * gene representing BigDecimal
 *
 * note that
 * currently the mutation of bigdecimal is limited to mutations for long and double.
 * however, bigdecimal could be mutated with `unscaled value` (such as biginteger) and `scale`
 * if we further enable it, then [floatingPointMode] and [isFloatingPointMutable] are not needed
 */
class BigDecimalGene(
    name: String,
    value: BigDecimal? = null,
    min : BigDecimal? = null,
    max : BigDecimal? = null,
    minInclusive : Boolean = true,
    maxInclusive : Boolean = true,

    /**
     * indicate whether to employ float pointing to mutate the gene
     */
    var floatingPointMode : Boolean = true,

    /**
     * as jdk,
     * the number of decimal digits in this BigDecimal
     */
    precision : Int? = null,

    /**
     * as jdk,
     * the number of digits to the right of the decimal point
     */
    scale : Int? = null

) : FloatingPointNumberGene<BigDecimal>(name, value,
    min = handleMinMaxInConstructor(value = min, isMin = true, precision = precision, scale = scale, example = BigDecimal.ZERO),
    max = handleMinMaxInConstructor(value = max, isMin = false, precision = precision, scale = scale, example = BigDecimal.ZERO),
    minInclusive, maxInclusive, precision, scale){

    companion object{
        private val log : Logger = LoggerFactory.getLogger(BigDecimalGene::class.java)

        /**
         * a low probability to change the mode, eg, from floating-point to long
         */
        private const val PROBABILITY_CHANGE_MODE = 0.05

        private val DEFAULT_ROUNDING_MODE : RoundingMode = RoundingMode.HALF_UP

        private val MAX_IN_FLOATINGPOINT = BigDecimal.valueOf(Double.MAX_VALUE)
        private val MIN_IN_FLOATINGPOINT = BigDecimal.valueOf(-Double.MAX_VALUE)

        private val MAX_IN_LONG = BigDecimal.valueOf(Long.MAX_VALUE)
        private val MIN_IN_LONG = BigDecimal.valueOf(Long.MIN_VALUE)

    }

    /**
     * whether to allow search to change [floatingPointMode]
     *
     * if [scale] is specified, floating mode is immutable
     * */
    var isFloatingPointMutable : Boolean = scale == null
        private set

    init {
        if (precision != null){
            if (precision <= 0)
                throw IllegalArgumentException("invalid precision: a negative number or 0 for the precision is not allowed")
            if (precision > NumberMutatorUtils.MAX_PRECISION)
                throw IllegalArgumentException("invalid precision: the max is ${NumberMutatorUtils.MAX_PRECISION}, but $precision is specified")
        }

        if (scale!= null){
            if (scale < 0)
                throw IllegalArgumentException("invalid scale: a negative number for the scale is not allowed")

            floatingPointMode = scale > 0
        }

        /*
            if specified range cannot be applied with long range, ie, min > Long.MAX or max < Long.MIN,
            only floatingPointMode is applicable
         */
        if (!rangeWithinLongRange()){
            floatingPointMode = true
            forbidFloatingPointModeMutable()
        }

        // format value
        setValueWithDecimal(this.value, precision, scale)
    }

    fun forbidFloatingPointModeMutable(){
        isFloatingPointMutable = false
    }

    override fun copyContent(): BigDecimalGene{
        val copy = BigDecimalGene(name, value, min, max, minInclusive, maxInclusive, floatingPointMode, precision, scale)
        copy.isFloatingPointMutable = this.isFloatingPointMutable
        return copy
    }

    override fun compareTo(other: ComparableGene): Int {
        if (other !is BigDecimalGene) {
            throw RuntimeException("Expected BigIntegerGene. Cannot compare to ${other::javaClass} instance")
        }
        return value.compareTo(other.value)
    }

    override fun randomize(randomness: Randomness, tryToForceNewValue: Boolean, allGenes: List<Gene>) {

        if (isFloatingPointMutable && randomness.nextBoolean()){
            floatingPointMode = randomness.nextBoolean()
        }

        if (floatingPointMode){
            val dValue = randomizeDouble(getMinUsedInSearch().toDouble(), getMaxUsedInSearch().toDouble(), scale, randomness)
            setValueWithDouble(dValue)
        }else{
            val longValue = randomizeLong(
                value.toLong(),
                getMinUsedInSearch().run { if (this < MIN_IN_LONG) MIN_IN_LONG else this }.toLong(),
                getMaxUsedInSearch().run { if (this > MAX_IN_LONG) MAX_IN_LONG else this  }.toLong(),
                randomness, tryToForceNewValue)
            setValueWithLong(longValue)
        }
    }

    override fun shallowMutate(
        randomness: Randomness,
        apc: AdaptiveParameterControl,
        mwc: MutationWeightControl,
        allGenes: List<Gene>,
        selectionStrategy: SubsetGeneSelectionStrategy,
        enableAdaptiveGeneMutation: Boolean,
        additionalGeneMutationInfo: AdditionalGeneMutationInfo?
    ): Boolean {

        if (isFloatingPointMutable && randomness.nextBoolean(PROBABILITY_CHANGE_MODE)){
            floatingPointMode = !floatingPointMode
        }

        // if the current value exceeds the range of long, then mode must be floating-point
        if (isFloatingPointMutable && !floatingPointMode && !withinLongRange())
            floatingPointMode = true

        val mutated = super.shallowMutate(
            randomness,
            apc,
            mwc,
            allGenes,
            selectionStrategy,
            enableAdaptiveGeneMutation,
            additionalGeneMutationInfo
        )
        if (mutated) return true

        if (floatingPointMode){
            setValueWithDouble(mutateFloatingPointNumber(randomness, apc = apc, value = value.toDouble(), smin = getMinUsedInSearch().toDouble(), smax = getMaxUsedInSearch().toDouble(), scale=scale))
        }else{
            setValueWithLong(mutateLong(value = value.toLong(), min=getMinUsedInSearch().toLong(), max = getMaxUsedInSearch().toLong(), apc = apc, randomness = randomness))
        }
        return true
    }

    override fun getValueAsPrintableString(
        previousGenes: List<Gene>,
        mode: GeneUtils.EscapeMode?,
        targetFormat: OutputFormat?,
        extraCheck: Boolean
    ): String {
        // note that it could return scientific representation
        return value.toString()
    }

    override fun copyValueFrom(other: Gene) {
        if (other !is BigDecimalGene)
            throw IllegalArgumentException("Invalid gene type ${other.javaClass}")
        // since bigdecimal is immutable, just refer to the value of other gene
        this.value = other.value
    }

    override fun containsSameValueAs(other: Gene): Boolean {
        if (other !is BigDecimalGene)
            throw IllegalArgumentException("Invalid gene type ${other.javaClass}")
        return this.value.compareTo(other.value) == 0
    }


    override fun bindValueBasedOn(gene: Gene): Boolean {
        val bd = when(gene){
            is SeededGene<*> -> return this.bindValueBasedOn(gene.getPhenotype() as Gene)
            is NumericStringGene -> return this.bindValueBasedOn(gene.number)
            is LongGene -> BigDecimal(gene.value)
            is FloatGene -> BigDecimal(gene.value.toDouble())
            is IntegerGene -> BigDecimal(gene.value)
            is DoubleGene -> BigDecimal(gene.value)
            is StringGene -> gene.value.toBigDecimalOrNull()?: return false
            is Base64StringGene -> gene.data.value.toBigDecimalOrNull()?: return false
            is ImmutableDataHolderGene -> gene.value.toBigDecimalOrNull()?: return false
            is SqlPrimaryKeyGene -> BigDecimal(gene.uniqueId)
            is BigIntegerGene -> BigDecimal(gene.value)
            is BigDecimalGene -> gene.value
            else -> {
                log.info("Do not support to bind long gene with the type: ${gene::class.java.simpleName}")
                return false
            }
        }
        setValueWithDecimal(bd, precision, scale)
        return true
    }

    fun setValueWithDouble(doubleValue: Double){
        setValueWithDecimal(BigDecimal(doubleValue.toString()), precision, scale)
    }

    fun setValueWithLong(longValue: Long){
        setValueWithDecimal(BigDecimal(longValue.toString()), precision, scale)
    }

    fun setValueWithString(value: String){
        setValueWithDecimal(BigDecimal(value), precision, scale)
    }

    private fun getRoundingMode() = DEFAULT_ROUNDING_MODE

    private fun setValueWithDecimal(bd: BigDecimal, precision: Int?, scale: Int?){
        value = if (precision == null){
            if (scale == null) bd
            else bd.setScale(scale, getRoundingMode())
        } else{
            val context = MathContext(precision, getRoundingMode())
            bd.round(context).run {
                if (scale != null) this.setScale(scale)
                else this
            }
        }
    }

    private fun getMinUsedInSearch() : BigDecimal {
        if (min != null && min >= MAX_IN_FLOATINGPOINT)
            throw IllegalStateException("not support yet: minimum value is greater than Double.MAX")
        if (minInclusive)
            return getFormattedValue(if (min == null || min < MIN_IN_FLOATINGPOINT) MIN_IN_FLOATINGPOINT else min, scale)

        if (min == null)
            log.warn("there is no minimum value specified, but minInclusive is false for gene $name")

        val lowerBound = if (min == null || MIN_IN_FLOATINGPOINT > min ){
            MIN_IN_FLOATINGPOINT
        }else if (min == MIN_IN_FLOATINGPOINT){
            BigDecimal.valueOf(-NumberMutatorUtils.MAX_DOUBLE_EXCLUSIVE)
        } else
            (min + getMinimalDelta())

        return getFormattedValue(lowerBound, scale)
    }

    private fun getMaxUsedInSearch() : BigDecimal {

        if (max != null && max <= MIN_IN_FLOATINGPOINT)
            throw IllegalStateException("not support yet: max value is less than -Double.MAX")

        if (maxInclusive)
            return getFormattedValue(if (max == null || max > MAX_IN_FLOATINGPOINT) MAX_IN_FLOATINGPOINT else max, scale)

        if (max == null)
            log.warn("there is no maximum value specified, but maxInclusive is false for gene $name")

        val upperBound = if (max == null || MAX_IN_FLOATINGPOINT < max ){
            MAX_IN_FLOATINGPOINT
        }else if (max == MAX_IN_FLOATINGPOINT){
            BigDecimal.valueOf(NumberMutatorUtils.MAX_DOUBLE_EXCLUSIVE)
        } else
            (max - getMinimalDelta())

        return getFormattedValue(upperBound, scale)
    }


    override fun getMinimum(): BigDecimal {
        return valueWithPrecisionAndScale(getMinUsedInSearch().toString(), scale)
    }

    override fun getMaximum(): BigDecimal {
        return valueWithPrecisionAndScale(getMaxUsedInSearch().toString(), scale)
    }

    override fun isValid(): Boolean {
        if (!super.isValid()) return false
        if (max != null && value > getMaximum())
            return false
        if (min != null && value < getMinimum())
            return false
        return true
    }

    /**
     * note that for bigdecimal
     * if the value exceeds the range manipulated by search, we consider it immutable for the moment
     */
    override fun isMutable(): Boolean {
        return super.isMutable() && checkValueRangeInSearch()
    }

    private fun checkValueRangeInSearch() : Boolean{

        val r = if (!isFloatingPointMutable && !floatingPointMode)
                    withinLongRange()
                else
                    value <= BigDecimal.valueOf(Double.MAX_VALUE) && value >= BigDecimal.valueOf(-Double.MAX_VALUE)
        if (!r)
            LoggingUtil.uniqueWarn(log, "value of BigDecimal exceeds the range of mutation")
        return r
    }

    private fun withinLongRange() : Boolean = value <= MAX_IN_LONG && value >= MIN_IN_LONG

    private fun rangeWithinLongRange() : Boolean = getMaximum() > MIN_IN_LONG || getMinimum() < MAX_IN_LONG

    override fun getDefaultValue(): BigDecimal {
        val df = super.getDefaultValue()
        if (df <= getMaximum() && df >= getMinimum())
            return df
        return getMiddle(getMinimum(), getMaximum(), scale)
    }

    override fun getZero(): BigDecimal = BigDecimal.ZERO
}