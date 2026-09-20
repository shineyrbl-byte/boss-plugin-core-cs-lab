package com.example.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecoveryCheckerTest {
    private fun check(s: String) = RecoveryChecker.check(RecoveryParser.parse(s))

    @Test
    fun commitBeforeSourceIsNotRecoverable() {
        val r = check("W1(X) R2(X) C2 C1")
        assertFalse(r.recoverable)
        assertEquals("not recoverable", r.level)
        assertTrue(r.violations.any { it.type == "commit_before_source" && it.txn == 2 && it.dependsOn == 1 })
    }

    @Test
    fun dirtyReadWithSafeCommitOrderIsOnlyRecoverable() {
        val r = check("W1(X) R2(X) C1 C2")
        assertTrue(r.recoverable)
        assertFalse(r.cascadeless)
        assertEquals("recoverable", r.level)
    }

    @Test
    fun readingCommittedDataIsStrict() {
        val r = check("W1(X) C1 R2(X) C2")
        assertTrue(r.strict)
        assertEquals("strict", r.level)
        assertTrue(r.violations.isEmpty())
    }

    @Test
    fun dirtyOverwriteIsCascadelessButNotStrict() {
        val r = check("W1(X) W2(X) C1 C2")
        assertTrue(r.cascadeless)
        assertFalse(r.strict)
        assertEquals("cascadeless", r.level)
    }

    @Test
    fun writerThatNeverCommitsBreaksRecoverability() {
        val r = check("W1(X) R2(X) C2")
        assertEquals("not recoverable", r.level)
    }

    @Test
    fun readingYourOwnWriteIsNotADependency() {
        val r = check("W1(X) R1(X) C1")
        assertTrue(r.readsFrom.isEmpty())
        assertEquals("strict", r.level)
    }

    @Test
    fun readBeforeALaterWriteIsFine() {
        val r = check("R1(X) W2(X) C2 C1")
        assertEquals("strict", r.level)
    }

    @Test
    fun actionAfterCommitIsRejected() {
        assertFailsWith<IllegalArgumentException> { RecoveryParser.parse("W1(X) C1 W1(Y)") }
    }

    @Test
    fun badTokenIsRejected() {
        assertFailsWith<IllegalArgumentException> { RecoveryParser.parse("W1(X) banana") }
    }
}
