package com.github.rules

fun interface IRule<in TARGET, out PASSED, out ERROR> {
    fun eval(param: TARGET): Result<PASSED, ERROR>
}