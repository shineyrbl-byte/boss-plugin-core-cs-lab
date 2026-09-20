package com.example.corecslab

data class Wait(val txn: Int, val item: String, val mode: LockMode, val blockedBy: List<Int>)

data class DeadlockResult(
    val deadlock: Boolean,
    val cycle: List<Int>?,
    val waits: List<Wait>
)

object DeadlockDetector {
    private fun blockersOf(holders: Map<Int, LockMode>, r: LockRequest): List<Int> =
        holders.filter { (t, m) ->
            t != r.txn && (r.mode == LockMode.EXCLUSIVE || m == LockMode.EXCLUSIVE)
        }.keys.sorted()

    fun detect(requests: List<LockRequest>): DeadlockResult {
        val held = mutableMapOf<String, MutableMap<Int, LockMode>>()
        val waiting = linkedMapOf<Int, LockRequest>()

        for (r in requests) {
            // a transaction that is already waiting cannot issue more requests
            if (r.txn in waiting) continue

            val holders = held.getOrPut(r.item) { mutableMapOf() }
            if (blockersOf(holders, r).isEmpty()) {
                // grant it, keeping the stronger lock if the transaction already holds one
                if (holders[r.txn] != LockMode.EXCLUSIVE) holders[r.txn] = r.mode
            } else {
                waiting[r.txn] = r
            }
        }

        val waits = waiting.values.map { r ->
            Wait(r.txn, r.item, r.mode, blockersOf(held.getValue(r.item), r))
        }
        val edges = waits.flatMap { w -> w.blockedBy.map { w.txn to it } }
        val nodes = requests.map { it.txn }.distinct()
        val cycle = CycleFinder.findCycle(nodes, edges)

        return DeadlockResult(cycle != null, cycle, waits)
    }
}
