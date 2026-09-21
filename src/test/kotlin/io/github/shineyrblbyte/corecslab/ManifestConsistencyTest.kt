package io.github.shineyrblbyte.corecslab

import kotlin.test.Test
import kotlin.test.assertTrue

class ManifestConsistencyTest {
    private val manifest: String = ManifestConsistencyTest::class.java
        .getResourceAsStream("/META-INF/boss-plugin/plugin.json")!!
        .bufferedReader()
        .readText()

    @Test
    fun everyToolInCodeIsDeclaredInTheManifest() {
        val names = listOf(
            "mcp__io_github_shineyrbl_byte_core_cs_lab__lab_ping",
            CheckScheduleTool.NAME,
            DetectDeadlockTool.NAME,
            CheckRecoverabilityTool.NAME,
        )
        for (name in names) {
            assertTrue(manifest.contains("\"$name\""), "Manifest is missing $name")
        }
    }
}
