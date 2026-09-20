package com.example.corecslab

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultFormatterTest {
    private fun json(schedule: String) =
        ResultFormatter.toJson(SerializabilityChecker.check(ScheduleParser.parse(schedule)))

    @Test
    fun serializableScheduleFormatsCorrectly() {
        assertEquals(
            """{"serializable":true,"edges":[{"from":1,"to":2,"item":"X"}],"cycle":null,"serialOrder":[1,2]}""",
            json("W1(X) R2(X)")
        )
    }

    @Test
    fun cyclicScheduleFormatsCorrectly() {
        assertEquals(
            """{"serializable":false,"edges":[{"from":1,"to":2,"item":"X"},{"from":2,"to":1,"item":"X"}],"cycle":[1,2,1],"serialOrder":null}""",
            json("R1(X) W2(X) W1(X)")
        )
    }
}
