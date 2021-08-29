package com.github.fredgeorge.detektmethodmcc

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MethodMccTest {
    companion object {
        private const val code = """
            private class A {
                fun z(flag: Boolean) : String {
                    return if (flag) "true" else "false"
                }
                
                fun y() = z().length
                
                override fun equals(other: Any?) = this === other || other is A && this.flag == other.flag
        
                override fun hashCode() = flag.hashCode()
            }
        """
    }

    @Test
    fun `Finds complexity in the method`() {
        val ktFile = compileContentForTest(code)
        val processor = MethodMccProcessor()
        processor.onProcess(ktFile, BindingContext.EMPTY)
        processor.onFinish(listOf(ktFile), result, BindingContext.EMPTY)
        assertEquals(1, result.metrics.size)
    }

    private val result = object : Detektion {

        override val findings: Map<String, List<Finding>> = emptyMap()
        override val notifications: Collection<Notification> = emptyList()
        override val metrics: MutableList<ProjectMetric> = mutableListOf()

        private var userData = KeyFMap.EMPTY_MAP

        override fun <V> getData(key: Key<V>): V? = userData.get(key)

        override fun <V> addData(key: Key<V>, value: V) {
            userData = userData.plus(key, requireNotNull(value))
        }

        override fun add(notification: Notification) {
            throw UnsupportedOperationException("not implemented")
        }

        override fun add(projectMetric: ProjectMetric) {
            metrics.add(projectMetric)
        }
    }

    // The following is a valid class whose text representation is pasted above
    private class A(private val flag: Boolean) {
        fun z(): String {
            return if (flag) "true" else "false"
        }

        fun y() = z().length

        override fun equals(other: Any?) = this === other || other is A && this.flag == other.flag

        override fun hashCode() = flag.hashCode()
    }
}
