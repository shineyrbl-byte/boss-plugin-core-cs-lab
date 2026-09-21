package io.github.shineyrblbyte.corecslab

data class ReadsFrom(val reader: Int, val writer: Int, val item: String)

data class Violation(val type: String, val txn: Int, val dependsOn: Int, val item: String)

data class RecoveryResult(
    val recoverable: Boolean,
    val cascadeless: Boolean,
    val strict: Boolean,
    val readsFrom: List<ReadsFrom>,
    val violations: List<Violation>
) {
    val level: String
        get() = when {
            strict -> "strict"
            cascadeless -> "cascadeless"
            recoverable -> "recoverable"
            else -> "not recoverable"
        }
}

object RecoveryChecker {
    fun check(events: List<Event>): RecoveryResult {
        val lastWriter = mutableMapOf<String, Int>()
        val committed = mutableSetOf<Int>()
        val readsFrom = mutableListOf<ReadsFrom>()
        val violations = mutableListOf<Violation>()

        fun addViolation(v: Violation) {
            if (v !in violations) violations.add(v)
        }

        for (e in events) {
            when (e.type) {
                EventType.READ -> {
                    val item = e.item!!
                    val w = lastWriter[item]
                    if (w != null && w != e.txn) {
                        val rf = ReadsFrom(e.txn, w, item)
                        if (rf !in readsFrom) readsFrom.add(rf)
                        if (w !in committed) addViolation(Violation("dirty_read", e.txn, w, item))
                    }
                }
                EventType.WRITE -> {
                    val item = e.item!!
                    val w = lastWriter[item]
                    if (w != null && w != e.txn && w !in committed) {
                        addViolation(Violation("dirty_overwrite", e.txn, w, item))
                    }
                    lastWriter[item] = e.txn
                }
                EventType.COMMIT -> {
                    for (rf in readsFrom) {
                        if (rf.reader == e.txn && rf.writer !in committed) {
                            addViolation(Violation("commit_before_source", e.txn, rf.writer, rf.item))
                        }
                    }
                    committed.add(e.txn)
                }
            }
        }

        val recoverable = violations.none { it.type == "commit_before_source" }
        val cascadeless = recoverable && violations.none { it.type == "dirty_read" }
        val strict = cascadeless && violations.none { it.type == "dirty_overwrite" }
        return RecoveryResult(recoverable, cascadeless, strict, readsFrom, violations)
    }
}
