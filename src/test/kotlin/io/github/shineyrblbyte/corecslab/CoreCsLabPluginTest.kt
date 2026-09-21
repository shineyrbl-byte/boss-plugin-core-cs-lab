package io.github.shineyrblbyte.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CoreCsLabPluginTest {
    @Test
    fun testPluginMetadata() {
        val plugin = CoreCsLabPlugin()
        assertNotNull(plugin)
        assertEquals("io.github.shineyrbl-byte.core-cs-lab", plugin.pluginId)
        assertEquals("core-cs-lab", plugin.displayName)
    }
}
