package com.example.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ScheduleParserTest {
    @Test
    fun parsesSimpleSchedule() {
        val ops = ScheduleParser.parse("R1(X) W2(X)")
        assertEquals(
            listOf(Operation(1, OpType.READ, "X"), Operation(2, OpType.WRITE, "X")),
            ops
        )
    }

    @Test
    fun rejectsBadToken() {
        assertFailsWith<IllegalArgumentException> { ScheduleParser.parse("R1(X) banana") }
    }

    @Test
    fun rejectsEmptyInput() {
        assertFailsWith<IllegalArgumentException> { ScheduleParser.parse("   ") }
    }
}
