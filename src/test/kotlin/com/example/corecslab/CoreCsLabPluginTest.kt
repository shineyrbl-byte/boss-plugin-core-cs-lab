package com.example.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CoreCsLabPluginTest {
    @Test
    fun testPluginMetadata() {
        val plugin = CoreCsLabPlugin()
        assertNotNull(plugin)
        assertEquals("com.example.core-cs-lab", plugin.pluginId)
        assertEquals("core-cs-lab", plugin.displayName)
    }
}
