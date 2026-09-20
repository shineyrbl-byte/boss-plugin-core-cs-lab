package com.example.corecslab

import ai.rever.boss.plugin.api.McpToolArgs
import ai.rever.boss.plugin.api.McpToolDefinition
import ai.rever.boss.plugin.api.McpToolHandler
import ai.rever.boss.plugin.api.McpToolResult

object DetectDeadlockTool {
    const val NAME = "mcp__com_example_core_cs_lab__detect_deadlock"

    const val DESCRIPTION =
        "Detects deadlock in a sequence of lock requests using a wait-for graph. " +
            "Input is a sequence like 'LX1(A) LX2(B) LX1(B) LX2(A)' " +
            "(LS = shared lock, LX = exclusive lock, number = transaction, letter = data item). " +
            "Returns JSON with deadlock, the cycle of waiting transactions if there is one, " +
            "and which transactions are waiting on which."

    const val INPUT_SCHEMA =
        """{"type":"object","properties":{"locks":{"type":"string","description":"Lock requests separated by spaces, e.g. LX1(A) LX2(B) LX1(B) LX2(A)"}},"required":["locks"]}"""

    fun run(args: McpToolArgs): McpToolResult {
        val text = args.string("locks")
        if (text.isNullOrBlank()) {
            return McpToolResult("Missing required argument 'locks'. Example: LX1(A) LX2(B) LX1(B) LX2(A)", true)
        }
        return try {
            val result = DeadlockDetector.detect(LockParser.parse(text))
            McpToolResult(ResultFormatter.deadlockToJson(result), false)
        } catch (e: IllegalArgumentException) {
            McpToolResult(e.message ?: "Invalid lock sequence", true)
        }
    }

    fun definition(): McpToolDefinition =
        McpToolDefinition(
            name = NAME,
            description = DESCRIPTION,
            inputSchema = INPUT_SCHEMA,
            readOnly = true,
            handler = McpToolHandler { args -> run(args) },
        )
}
