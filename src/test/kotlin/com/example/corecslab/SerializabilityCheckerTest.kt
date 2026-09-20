package com.example.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SerializabilityCheckerTest {
    private fun check(schedule: String) =
        SerializabilityChecker.check(ScheduleParser.parse(schedule))

    @Test
    fun twoTransactionCycleIsNotSerializable() {
        val r = check("R1(X) W2(X) W1(X)")
        assertFalse(r.serializable)
        assertEquals(listOf(1, 2, 1), r.cycle)
    }

    @Test
    fun simpleOrderIsSerializable() {
        val r = check("W1(X) R2(X)")
        assertTrue(r.serializable)
        assertEquals(listOf(1, 2), r.serialOrder)
    }

    @Test
    fun threeTransactionChainGivesSerialOrder() {
        val r = check("R1(X) W1(X) R2(X) W2(Y) R3(Y)")
        assertTrue(r.serializable)
        assertEquals(listOf(1, 2, 3), r.serialOrder)
    }

    @Test
    fun threeTransactionCycleIsFound() {
        val r = check("W1(X) W2(Y) W3(Z) R1(Y) R2(Z) R3(X)")
        assertFalse(r.serializable)
        assertEquals(listOf(1, 3, 2, 1), r.cycle)
    }

    @Test
    fun singleTransactionIsAlwaysSerializable() {
        val r = check("R1(X) W1(X)")
        assertTrue(r.serializable)
        assertEquals(listOf(1), r.serialOrder)
    }
}
