package com.example.corecslab

data class CheckResult(
    val serializable: Boolean,
    val edges: List<Edge>,
    val cycle: List<Int>?,
    val serialOrder: List<Int>?
)

object SerializabilityChecker {
    fun check(ops: List<Operation>): CheckResult {
        val edges = PrecedenceGraph.conflictEdges(ops)
        val nodes = ops.map { it.txn }.distinct()
        val next: Map<Int, List<Int>> = nodes.associateWith { n ->
            edges.filter { it.from == n }.map { it.to }.distinct()
        }

        // state: missing = not visited, 1 = on current path, 2 = finished
        val state = mutableMapOf<Int, Int>()
        val path = mutableListOf<Int>()
        val finished = mutableListOf<Int>()
        var cycle: List<Int>? = null

        fun visit(n: Int) {
            state[n] = 1
            path.add(n)
            for (m in next.getValue(n)) {
                if (cycle != null) break
                when (state[m]) {
                    1 -> cycle = path.subList(path.indexOf(m), path.size) + m
                    null -> visit(m)
                }
            }
            path.removeAt(path.size - 1)
            state[n] = 2
            finished.add(n)
        }

        for (n in nodes) {
            if (cycle == null && state[n] == null) visit(n)
        }

        val found = cycle
        return if (found != null) {
            CheckResult(false, edges, found, null)
        } else {
            CheckResult(true, edges, null, finished.reversed())
        }
    }
}
