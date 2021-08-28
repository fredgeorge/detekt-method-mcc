package com.github.fredgeorge.detektmethodmcc

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass

class MethodMccRule(config: Config) : Rule(config) {
  override val issue = Issue(
    javaClass.simpleName,
    Severity.CodeSmell,
    "Method MCC Rule",
    Debt.TWENTY_MINS,
  )

  override fun visitClass(klass: KtClass) {
    super.visitClass(klass)

    if (klass.isInner()) {
      report(CodeSmell(issue, Entity.atName(klass), "Custom message"))
    }
  }
}
