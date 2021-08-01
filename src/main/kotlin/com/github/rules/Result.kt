package com.github.rules

sealed class Result {
    object Passed : Result()

    data class Error(
        val message: String,
        val next: Error? = null
    ) : Result()

}
