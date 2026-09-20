package com.example.corecslab

import ai.rever.boss.plugin.api.McpToolDefinition
import ai.rever.boss.plugin.api.McpToolHandler
import ai.rever.boss.plugin.api.McpToolProvider
import ai.rever.boss.plugin.api.McpToolResult
import ai.rever.boss.plugin.api.Plugin
import ai.rever.boss.plugin.api.PluginContext
import ai.rever.boss.plugin.logging.BossLogger
import ai.rever.boss.plugin.logging.LogCategory

class CoreCsLabPlugin : Plugin {
    override val pluginId: String = "com.example.core-cs-lab"
    override val displayName: String = "core-cs-lab"

    // Note: Templates avoid the Compose compiler plugin, so no $stable field is synthesised
    // on the plugin class. If Compose is added later for UI components, keep the logger as a
    // top-level property or resolve per-call to avoid Compose stability metadata linkage hazards.
    private val logger = BossLogger.forComponent("core-cs-lab")
    private var toolProvider: McpToolProvider? = null

    override fun register(context: PluginContext) {
        logger.info(LogCategory.GENERAL, "Registering core-cs-lab (com.example.core-cs-lab)")
        val provider =
            object : McpToolProvider {
                override val providerId: String = pluginId

                override fun tools(): List<McpToolDefinition> =
                    listOf(
                        McpToolDefinition(
                            name = "mcp__com_example_core_cs_lab__lab_ping",
                            description = "Simple connectivity test for the Core CS Lab plugin",
                            handler =
                                McpToolHandler { _ ->
                                    logger.info(LogCategory.GENERAL, "Executing lab_ping for core-cs-lab")
                                    McpToolResult("Core CS Lab plugin is alive.")
                                },
                        ),
                        CheckScheduleTool.definition(),
                        DetectDeadlockTool.definition(),
                    )
            }
        toolProvider = provider
        context.registerMcpToolProvider(provider)
    }

    override fun dispose() {
        toolProvider = null
        logger.info(LogCategory.GENERAL, "Disposed core-cs-lab (com.example.core-cs-lab)")
    }
}
