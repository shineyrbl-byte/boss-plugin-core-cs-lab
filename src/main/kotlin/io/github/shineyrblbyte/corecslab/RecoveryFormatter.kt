package io.github.shineyrblbyte.corecslab

object RecoveryFormatter {
    fun toJson(r: RecoveryResult): String {
        val reads = r.readsFrom.joinToString(",") {
            """{"reader":${it.reader},"writer":${it.writer},"item":"${it.item}"}"""
        }
        val viol = r.violations.joinToString(",") {
            """{"type":"${it.type}","txn":${it.txn},"dependsOn":${it.dependsOn},"item":"${it.item}"}"""
        }
        return """{"level":"${r.level}","recoverable":${r.recoverable},"cascadeless":${r.cascadeless},"strict":${r.strict},"readsFrom":[$reads],"violations":[$viol]}"""
    }
}
