package com.github.fredgeorge.detektmethodmcc

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class FredMetricsRuleSetProvider : RuleSetProvider {
  override val ruleSetId: String = "FredMetrics"

  override fun instance(config: Config): RuleSet {
    return RuleSet(
      ruleSetId,
      listOf(
        MethodMccRule(config),
      ),
    )
  }
}
