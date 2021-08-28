package com.github.fredgeorge.detektmethodmcc

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MethodMccTest {

  @Test
  fun `reports inner classes`() {
    val code = """
      class A {
        inner class B
      }
      """
    MethodMccRule(Config.empty).compileAndLint(code).also { findings ->
        assertEquals(1, findings.size)
    }
  }

  @Test
  fun `doesn't report inner classes`() {
    val code = """
      class A {
        class B
      }
      """
    MethodMccRule(Config.empty).compileAndLint(code).also { findings ->
        assertTrue(findings.isEmpty())
    }
  }
}
