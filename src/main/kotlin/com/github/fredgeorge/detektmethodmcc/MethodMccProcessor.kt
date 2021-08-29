package com.github.fredgeorge.detektmethodmcc

import com.github.fredgeorge.detektmethodmcc.MethodMccProcessor.MethodMcc.MethodComplexity.Companion.max
import com.github.fredgeorge.detektmethodmcc.MethodMccProcessor.MethodMcc.MethodComplexity.Companion.mean
import com.github.fredgeorge.detektmethodmcc.MethodMccProcessor.MethodMcc.MethodComplexity.Companion.median
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.roundToInt

class MethodMccProcessor : FileProcessListener {
    companion object {
        private const val HUNDREDS = 100
        private const val AVERAGE_LABEL = "average method complexity"
        private const val MEDIAN_LABEL = "median method complexity"
        private const val MAXIMUM_LABEL = "maximum method complexity"
    }

    private val mccVisitor = MethodMcc()

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        file.accept(mccVisitor)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        result.add(doubleMetric(AVERAGE_LABEL, mccVisitor.mean))
        result.add(intMetric(MEDIAN_LABEL, mccVisitor.median))
        result.add(intMetric(MAXIMUM_LABEL, mccVisitor.max))
    }

    private fun doubleMetric(label: String, amount: Double) = ProjectMetric(
        type = label,
        value = (amount * HUNDREDS).roundToInt(),
        isDouble = true,
        conversionFactor = HUNDREDS
    )

    private fun intMetric(label: String, amount: Int) = ProjectMetric(
        type = label,
        value = amount
    )

    internal class MethodMcc : DetektVisitor() {
        private val methodComplexities = mutableListOf<MethodComplexity>()
        private lateinit var klassName: String

        override fun visitClass(klass: KtClass) {
            klassName = klass.name ?: "<missing class name>"
            super.visitClass(klass)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            methodComplexities.add(
                MethodComplexity(
                    klassName,
                    function.name ?: "<missing function name>",
                    CyclomaticComplexity.calculate(function)
                )
            )
        }

        internal val mean get() = methodComplexities.mean()

        internal val median get() = methodComplexities.median()

        internal val max get() = methodComplexities.max()

        override fun toString() =
            "Average method complexity is $mean\n" +
                    "Median method complexity is $median\n" +
                    "Maximum method complexity is $max\n  " +
                    methodComplexities.joinToString("\n  ") { it.toString() }

        internal class MethodComplexity(
            private val klassName: String,
            private val methodName: String,
            private val complexity: Int
        ) {
            companion object {
                internal fun List<MethodComplexity>.mean() = this.sumOf { it.complexity }.toDouble() / this.size
                internal fun List<MethodComplexity>.median() = this.sortedBy { it.complexity }[this.size / 2].complexity
                internal fun List<MethodComplexity>.max() = this.maxOf{ it.complexity }
            }

            override fun toString() = "Method '$methodName' in class '$klassName' has complexity of $complexity"
        }
    }
}


