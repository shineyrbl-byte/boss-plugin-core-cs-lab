package com.example.corecslab

enum class LockMode { SHARED, EXCLUSIVE }

data class LockRequest(val txn: Int, val mode: LockMode, val item: String)

object LockParser {
    private val pattern = Regex("""L([SX])(\d+)\((\w+)\)""")

    fun parse(text: String): List<LockRequest> {
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        require(tokens.isNotEmpty()) { "Lock schedule is empty" }

        return tokens.map { token ->
            val match = pattern.matchEntire(token)
                ?: throw IllegalArgumentException(
                    "Bad lock request '$token'. Expected something like LS1(A) or LX2(B)"
                )
            LockRequest(
                txn = match.groupValues[2].toInt(),
                mode = if (match.groupValues[1] == "S") LockMode.SHARED else LockMode.EXCLUSIVE,
                item = match.groupValues[3]
            )
        }
    }
}
