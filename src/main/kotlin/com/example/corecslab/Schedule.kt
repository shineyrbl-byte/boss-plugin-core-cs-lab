package com.example.corecslab

enum class OpType { READ, WRITE }

data class Operation(val txn: Int, val type: OpType, val item: String)

object ScheduleParser {
    private val pattern = Regex("""([RW])(\d+)\((\w+)\)""")

    fun parse(text: String): List<Operation> {
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        require(tokens.isNotEmpty()) { "Schedule is empty" }

        return tokens.map { token ->
            val match = pattern.matchEntire(token)
                ?: throw IllegalArgumentException(
                    "Bad operation '$token'. Expected something like R1(X) or W2(Y)"
                )
            Operation(
                txn = match.groupValues[2].toInt(),
                type = if (match.groupValues[1] == "R") OpType.READ else OpType.WRITE,
                item = match.groupValues[3]
            )
        }
    }
}
