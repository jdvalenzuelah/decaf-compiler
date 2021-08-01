package com.github.rules

import org.junit.jupiter.api.Test
import org.junit.Assert.assertTrue

class RuleEngineTest {

    private val stringAllUpperCase = rule<String> {
        if(it.toUpperCase() == it) Result.Passed else Result.Error("Not all are upper")
    }

    private val containsA = rule<String> {
        if(it.contains("A".toRegex(RegexOption.IGNORE_CASE))) Result.Passed else Result.Error("Should contain at leas one A")
    }

    private val containsAAndAllUpperCase = stringAllUpperCase.next(containsA)

    @Test
    fun `should return error if did not passed rule`() {
        val test = "aaa"
        val res = stringAllUpperCase.eval(test)

        assertTrue(res is Result.Error)
    }

    @Test
    fun `should return passed if rule is successful`() {
        val test = "BBBB"
        val res = stringAllUpperCase.eval(test)

        assertTrue(res is Result.Passed)
    }

    @Test
    fun `should return nested error for nested rules`() {
        val test = "cccc"
        val res = containsAAndAllUpperCase.eval(test)

        assertTrue(res is Result.Error)
        val error = res as Result.Error
        assertTrue(error.next != null)
    }

    @Test
    fun `should return passed for nested rules if all are successful`() {
        val test = "AAABBB"
        val res = containsAAndAllUpperCase.eval(test)

        assertTrue(res is Result.Passed)
    }

}