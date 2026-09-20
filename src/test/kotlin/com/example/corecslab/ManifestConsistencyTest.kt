package com.example.corecslab

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
            "mcp__com_example_core_cs_lab__lab_ping",
            CheckScheduleTool.NAME,
            DetectDeadlockTool.NAME,
        )
        for (name in names) {
            assertTrue(manifest.contains("\"$name\""), "Manifest is missing $name")
        }
    }
}
