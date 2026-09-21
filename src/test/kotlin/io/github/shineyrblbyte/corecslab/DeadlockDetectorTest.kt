package io.github.shineyrblbyte.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeadlockDetectorTest {
    private fun detect(text: String) = DeadlockDetector.detect(LockParser.parse(text))

    @Test
    fun classicTwoTransactionDeadlock() {
        val r = detect("LX1(A) LX2(B) LX1(B) LX2(A)")
        assertTrue(r.deadlock)
        assertEquals(listOf(1, 2, 1), r.cycle)
    }

    @Test
    fun threeTransactionDeadlock() {
        val r = detect("LX1(A) LX2(B) LX3(C) LX1(B) LX2(C) LX3(A)")
        assertTrue(r.deadlock)
        assertEquals(listOf(1, 2, 3, 1), r.cycle)
    }

    @Test
    fun waitingWithoutDeadlock() {
        val r = detect("LX1(A) LS2(A)")
        assertFalse(r.deadlock)
        assertEquals(1, r.waits.size)
        assertEquals(listOf(1), r.waits[0].blockedBy)
    }

    @Test
    fun sharedLocksAreCompatible() {
        val r = detect("LS1(A) LS2(A)")
        assertFalse(r.deadlock)
        assertTrue(r.waits.isEmpty())
    }

    @Test
    fun upgradeDeadlock() {
        val r = detect("LS1(A) LS2(A) LX1(A) LX2(A)")
        assertTrue(r.deadlock)
        assertEquals(listOf(1, 2, 1), r.cycle)
    }

    @Test
    fun blockedTransactionRequestsAreIgnored() {
        val r = detect("LX1(A) LX2(A) LX2(B) LX3(B)")
        assertFalse(r.deadlock)
        assertEquals(1, r.waits.size)
    }

    @Test
    fun badTokenIsRejected() {
        assertFailsWith<IllegalArgumentException> { LockParser.parse("LZ1(A)") }
    }
}
