package io.github.shineyrblbyte.corecslab

object CycleFinder {
    /** Returns a cycle like [1, 2, 1] if the directed graph has one, otherwise null. */
    fun findCycle(nodes: List<Int>, edges: List<Pair<Int, Int>>): List<Int>? {
        val next: Map<Int, List<Int>> = nodes.associateWith { n ->
            edges.filter { it.first == n }.map { it.second }.distinct()
        }

        // state: missing = not visited, 1 = on current path, 2 = finished
        val state = mutableMapOf<Int, Int>()
        val path = mutableListOf<Int>()
        var cycle: List<Int>? = null

        fun visit(n: Int) {
            state[n] = 1
            path.add(n)
            for (m in next[n].orEmpty()) {
                if (cycle != null) break
                when (state[m]) {
                    1 -> cycle = path.subList(path.indexOf(m), path.size) + m
                    null -> visit(m)
                }
            }
            path.removeAt(path.size - 1)
            state[n] = 2
        }

        for (n in nodes) {
            if (cycle == null && state[n] == null) visit(n)
        }
        return cycle
    }
}
