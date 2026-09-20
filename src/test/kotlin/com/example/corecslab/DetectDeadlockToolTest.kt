package com.example.corecslab

import ai.rever.boss.plugin.api.McpToolArgs
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DetectDeadlockToolTest {
    private fun args(vararg pairs: Pair<String, String>) = McpToolArgs(mapOf(*pairs), "")

    @Test
    fun classicDeadlockIsReported() {
        val r = DetectDeadlockTool.run(args("locks" to "LX1(A) LX2(B) LX1(B) LX2(A)"))
        assertFalse(r.isError)
        assertTrue(r.text.contains("\"deadlock\":true"))
        assertTrue(r.text.contains("\"cycle\":[1,2,1]"))
    }

    @Test
    fun noDeadlockIsReported() {
        val r = DetectDeadlockTool.run(args("locks" to "LX1(A) LS2(A)"))
        assertFalse(r.isError)
        assertTrue(r.text.contains("\"deadlock\":false"))
        assertTrue(r.text.contains("\"cycle\":null"))
    }

    @Test
    fun malformedInputReturnsError() {
        val r = DetectDeadlockTool.run(args("locks" to "LZ1(A)"))
        assertTrue(r.isError)
        assertTrue(r.text.contains("LZ1(A)"))
    }

    @Test
    fun missingArgumentReturnsError() {
        val r = DetectDeadlockTool.run(McpToolArgs(emptyMap(), ""))
        assertTrue(r.isError)
    }

    @Test
    fun formatterMatchesExpectedShape() {
        val result = DeadlockDetector.detect(LockParser.parse("LX1(A) LX2(B) LX1(B) LX2(A)"))
        assertTrue(
            ResultFormatter.deadlockToJson(result) ==
                """{"deadlock":true,"cycle":[1,2,1],"waits":[{"txn":1,"item":"B","mode":"EXCLUSIVE","blockedBy":[2]},{"txn":2,"item":"A","mode":"EXCLUSIVE","blockedBy":[1]}]}"""
        )
    }
}
