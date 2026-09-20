package com.example.corecslab

enum class EventType { READ, WRITE, COMMIT }

data class Event(val txn: Int, val type: EventType, val item: String?)

object RecoveryParser {
    private val opPattern = Regex("""([RW])(\d+)\((\w+)\)""")
    private val commitPattern = Regex("""C(\d+)""")

    fun parse(text: String): List<Event> {
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        require(tokens.isNotEmpty()) { "Schedule is empty" }

        val events = tokens.map { token ->
            val op = opPattern.matchEntire(token)
            val commit = commitPattern.matchEntire(token)
            when {
                op != null -> Event(
                    txn = op.groupValues[2].toInt(),
                    type = if (op.groupValues[1] == "R") EventType.READ else EventType.WRITE,
                    item = op.groupValues[3]
                )
                commit != null -> Event(commit.groupValues[1].toInt(), EventType.COMMIT, null)
                else -> throw IllegalArgumentException(
                    "Bad token '$token'. Expected R1(X), W2(Y) or C1"
                )
            }
        }

        val committed = mutableSetOf<Int>()
        for (e in events) {
            require(e.txn !in committed) { "Transaction ${e.txn} acts after its commit" }
            if (e.type == EventType.COMMIT) committed.add(e.txn)
        }
        return events
    }
}
