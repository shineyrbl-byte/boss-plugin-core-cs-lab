package com.example.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals

class PrecedenceGraphTest {
    private fun edgesOf(schedule: String) =
        PrecedenceGraph.conflictEdges(ScheduleParser.parse(schedule))

    @Test
    fun readThenWriteThenWriteGivesTwoArrows() {
        assertEquals(
            listOf(Edge(1, 2, "X"), Edge(2, 1, "X")),
            edgesOf("R1(X) W2(X) W1(X)")
        )
    }

    @Test
    fun twoReadsNeverConflict() {
        assertEquals(emptyList(), edgesOf("R1(X) R2(X)"))
    }

    @Test
    fun differentItemsNeverConflict() {
        assertEquals(emptyList(), edgesOf("W1(X) W2(Y)"))
    }

    @Test
    fun sameTransactionNeverConflictsWithItself() {
        assertEquals(emptyList(), edgesOf("R1(X) W1(X)"))
    }

    @Test
    fun writeThenReadGivesOneArrow() {
        assertEquals(listOf(Edge(1, 2, "X")), edgesOf("W1(X) R2(X)"))
    }
}
