package com.example.corecslab

object ResultFormatter {
    fun toJson(r: CheckResult): String {
        val edges = r.edges.joinToString(",") {
            """{"from":${it.from},"to":${it.to},"item":"${it.item}"}"""
        }
        val cycle = r.cycle?.joinToString(",", "[", "]") ?: "null"
        val order = r.serialOrder?.joinToString(",", "[", "]") ?: "null"
        return """{"serializable":${r.serializable},"edges":[$edges],"cycle":$cycle,"serialOrder":$order}"""
    }
}
