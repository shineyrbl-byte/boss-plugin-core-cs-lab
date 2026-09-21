package io.github.shineyrblbyte.corecslab

object ResultFormatter {
    fun toJson(r: CheckResult): String {
        val edges = r.edges.joinToString(",") {
            """{"from":${it.from},"to":${it.to},"item":"${it.item}"}"""
        }
        val cycle = r.cycle?.joinToString(",", "[", "]") ?: "null"
        val order = r.serialOrder?.joinToString(",", "[", "]") ?: "null"
        return """{"serializable":${r.serializable},"edges":[$edges],"cycle":$cycle,"serialOrder":$order}"""
    }

    fun deadlockToJson(r: DeadlockResult): String {
        val cycle = r.cycle?.joinToString(",", "[", "]") ?: "null"
        val waits = r.waits.joinToString(",") {
            """{"txn":${it.txn},"item":"${it.item}","mode":"${it.mode}","blockedBy":${it.blockedBy.joinToString(",", "[", "]")}}"""
        }
        return """{"deadlock":${r.deadlock},"cycle":$cycle,"waits":[$waits]}"""
    }
}
