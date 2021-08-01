package com.github.rules

fun interface IRule<in TARGET> {
    fun eval(param: TARGET): Result
}