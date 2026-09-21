package io.github.shineyrblbyte.corecslab

data class Edge(val from: Int, val to: Int, val item: String)

object PrecedenceGraph {
    fun conflictEdges(ops: List<Operation>): List<Edge> {
        val edges = mutableListOf<Edge>()
        for (i in ops.indices) {
            for (j in i + 1 until ops.size) {
                val a = ops[i]
                val b = ops[j]
                val conflict = a.txn != b.txn &&
                    a.item == b.item &&
                    (a.type == OpType.WRITE || b.type == OpType.WRITE)
                if (conflict) {
                    val edge = Edge(a.txn, b.txn, a.item)
                    if (edge !in edges) edges.add(edge)
                }
            }
        }
        return edges
    }
}
