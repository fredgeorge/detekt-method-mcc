package com.github.fredgeorge.detektmethodmcc

import com.github.fredgeorge.detektmethodmcc.MethodMcc.Result.Companion.mean
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodMcc(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Method MCC Rule",
        Debt.FIVE_MINS,
    )

    private val results = mutableListOf<Result>()

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        println(this)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {

        val complexity = CyclomaticComplexity.calculate(function)
        val methodName = function.name ?: "<missing name>"
        results.add(Result(methodName, complexity))
    }

    override fun toString() =
        "Average method complexity is ${results.mean()}\n ${results.joinToString("\n") { it.toString() }}"

    internal class Result(private val methodName: String, private val complexity: Int) {
        companion object {
            internal fun List<Result>.mean() = this.sumOf { it.complexity }.toDouble() / this.size
        }

        override fun toString() = "Method '$methodName' has complexity of $complexity"
    }
}
