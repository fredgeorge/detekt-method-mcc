package com.github.fredgeorge.detektmethodmcc

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MethodMccTest {

    @Test
    fun `Finds complexity in the method`() {
        """
            private class A {
                fun z(flag: Boolean) : String {
                    return if (flag) "true" else "false"
                }
                
                fun y() = z().length
                
                override fun equals(other: Any?) = this === other || other is A && this.flag == other.flag
        
                override fun hashCode() = flag.hashCode()
            }
        """.also { code ->
            MethodMcc(Config.empty).compileAndLint(code).also { findings ->
                assertTrue(findings.isEmpty())
            }
        }
    }

    // The following is a valid class whose text representation is pasted above
    private class A(private val flag: Boolean) {
        fun z() : String {
            return if (flag) "true" else "false"
        }

        fun y() = z().length

        override fun equals(other: Any?) = this === other || other is A && this.flag == other.flag

        override fun hashCode() = flag.hashCode()
    }
}
