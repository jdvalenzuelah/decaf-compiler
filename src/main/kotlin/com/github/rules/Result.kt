package com.github.rules

sealed class Result<out A, out E> {

    data class Passed<out A>(val a: A) : Result<A, Nothing>()

    data class Error<out E>(
        val e: E,
        val next: Error<*>? = null
    ) : Result<Nothing, E>()

}
