package com.example.corecslab

import ai.rever.boss.plugin.api.McpToolArgs
import ai.rever.boss.plugin.api.McpToolDefinition
import ai.rever.boss.plugin.api.McpToolHandler
import ai.rever.boss.plugin.api.McpToolResult

object CheckScheduleTool {
    const val NAME = "mcp__com_example_core_cs_lab__check_schedule"

    const val DESCRIPTION =
        "Checks whether a database transaction schedule is conflict-serializable. " +
            "Input is a schedule like 'R1(X) W2(X) W1(X)' (R=read, W=write, number=transaction, letter=data item). " +
            "Returns JSON with serializable, the conflict edges, a cycle if there is one, " +
            "and an equivalent serial order if the schedule is serializable."

    const val INPUT_SCHEMA =
        """{"type":"object","properties":{"schedule":{"type":"string","description":"Operations separated by spaces, e.g. R1(X) W2(X) W1(X)"}},"required":["schedule"]}"""

    fun run(args: McpToolArgs): McpToolResult {
        val text = args.string("schedule")
        if (text.isNullOrBlank()) {
            return McpToolResult("Missing required argument 'schedule'. Example: R1(X) W2(X) W1(X)", true)
        }
        return try {
            val result = SerializabilityChecker.check(ScheduleParser.parse(text))
            McpToolResult(ResultFormatter.toJson(result), false)
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
