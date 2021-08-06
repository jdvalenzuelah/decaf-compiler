package com.github.rules

sealed class Result<out A, out E> {

    data class Passed<out A>(val a: A) : Result<A, Nothing>()

    data class Error<E>(
        val e: E,
        var next: Error<E>? = null
    ) : Result<Nothing, E>()

}
