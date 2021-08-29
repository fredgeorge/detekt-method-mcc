package com.github.fredgeorge.detektmethodmcc

import com.github.fredgeorge.detektmethodmcc.MethodMccProcessor.MethodMcc.MethodComplexity.Companion.mean
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.math.roundToInt

class MethodMccProcessor : FileProcessListener {
    companion object {
        private const val HUNDREDS = 100.0
        private const val AVERAGE_LABEL = "average method complexity"
    }
    private val mccVisitor = MethodMcc()

    override fun onProcess(file: KtFile, bindingContext: BindingContext) {
        file.accept(mccVisitor)
    }

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        result.add(ProjectMetric(
            type = AVERAGE_LABEL,
            value = (mccVisitor.mean() * HUNDREDS).roundToInt(),
            isDouble = true)
        )
    }

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

        internal fun mean() = methodComplexities.mean()

        override fun toString() =
            "Average method complexity is ${methodComplexities.mean()}\n  " +
                    methodComplexities.joinToString("\n  ") { it.toString() }

        internal class MethodComplexity(
            private val klassName: String,
            private val methodName: String,
            private val complexity: Int
        ) {
            companion object {
                internal fun List<MethodComplexity>.mean() = this.sumOf { it.complexity }.toDouble() / this.size
            }

            override fun toString() = "Method '$methodName' in class '$klassName' has complexity of $complexity"
        }
    }
}


