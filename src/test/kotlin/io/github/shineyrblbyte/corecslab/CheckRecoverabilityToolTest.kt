package io.github.shineyrblbyte.corecslab

import ai.rever.boss.plugin.api.McpToolArgs
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckRecoverabilityToolTest {
    private fun args(vararg pairs: Pair<String, String>) = McpToolArgs(mapOf(*pairs), "")

    @Test
    fun reportsLevelAndViolations() {
        val r = CheckRecoverabilityTool.run(args("schedule" to "W1(X) R2(X) C2 C1"))
        assertFalse(r.isError)
        assertTrue(r.text.contains("\"level\":\"not recoverable\""))
        assertTrue(r.text.contains("\"type\":\"commit_before_source\""))
    }

    @Test
    fun strictScheduleHasNoViolations() {
        val r = CheckRecoverabilityTool.run(args("schedule" to "W1(X) C1 R2(X) C2"))
        assertFalse(r.isError)
        assertTrue(r.text.contains("\"level\":\"strict\""))
        assertTrue(r.text.contains("\"violations\":[]"))
    }

    @Test
    fun malformedInputReturnsError() {
        val r = CheckRecoverabilityTool.run(args("schedule" to "W1(X) banana"))
        assertTrue(r.isError)
        assertTrue(r.text.contains("banana"))
    }

    @Test
    fun missingArgumentReturnsError() {
        val r = CheckRecoverabilityTool.run(McpToolArgs(emptyMap(), ""))
        assertTrue(r.isError)
    }
}
