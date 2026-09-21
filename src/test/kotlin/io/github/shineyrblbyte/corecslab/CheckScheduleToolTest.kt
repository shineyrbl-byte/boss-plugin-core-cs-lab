package io.github.shineyrblbyte.corecslab

import ai.rever.boss.plugin.api.McpToolArgs
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckScheduleToolTest {
    private fun args(vararg pairs: Pair<String, String>) = McpToolArgs(mapOf(*pairs), "")

    @Test
    fun cyclicScheduleReturnsNotSerializable() {
        val r = CheckScheduleTool.run(args("schedule" to "R1(X) W2(X) W1(X)"))
        assertFalse(r.isError)
        assertTrue(r.text.contains("\"serializable\":false"))
        assertTrue(r.text.contains("\"cycle\":[1,2,1]"))
    }

    @Test
    fun serializableScheduleReturnsSerialOrder() {
        val r = CheckScheduleTool.run(args("schedule" to "W1(X) R2(X)"))
        assertFalse(r.isError)
        assertTrue(r.text.contains("\"serialOrder\":[1,2]"))
    }

    @Test
    fun malformedScheduleReturnsError() {
        val r = CheckScheduleTool.run(args("schedule" to "R1(X) banana"))
        assertTrue(r.isError)
        assertTrue(r.text.contains("banana"))
    }

    @Test
    fun missingArgumentReturnsError() {
        val r = CheckScheduleTool.run(args())
        assertTrue(r.isError)
    }

    @Test
    fun handlerRunsThroughTheToolDefinition() {
        val def = CheckScheduleTool.definition()
        assertEquals(CheckScheduleTool.NAME, def.name)
        val r = runBlocking { def.handler.call(args("schedule" to "W1(X) R2(X)")) }
        assertFalse(r.isError)
    }
}
