package com.example.corecslab

import ai.rever.boss.plugin.api.McpToolArgs
import ai.rever.boss.plugin.api.McpToolDefinition
import ai.rever.boss.plugin.api.McpToolHandler
import ai.rever.boss.plugin.api.McpToolResult

object CheckRecoverabilityTool {
    const val NAME = "mcp__com_example_core_cs_lab__check_recoverability"

    const val DESCRIPTION =
        "Classifies a transaction schedule with commits as strict, cascadeless, recoverable or not recoverable. " +
            "Input is a schedule like 'W1(X) R2(X) C1 C2' (R=read, W=write, C=commit, number=transaction, letter=data item). " +
            "Returns JSON with the level, who read from whom, and the specific violations found."

    const val INPUT_SCHEMA =
        """{"type":"object","properties":{"schedule":{"type":"string","description":"Operations and commits separated by spaces, e.g. W1(X) R2(X) C1 C2"}},"required":["schedule"]}"""

    fun run(args: McpToolArgs): McpToolResult {
        val text = args.string("schedule")
        if (text.isNullOrBlank()) {
            return McpToolResult("Missing required argument 'schedule'. Example: W1(X) R2(X) C1 C2", true)
        }
        return try {
            val result = RecoveryChecker.check(RecoveryParser.parse(text))
            McpToolResult(RecoveryFormatter.toJson(result), false)
        } catch (e: IllegalArgumentException) {
            McpToolResult(e.message ?: "Invalid schedule", true)
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
