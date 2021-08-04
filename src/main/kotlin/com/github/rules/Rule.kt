package com.github.rules

import com.github.rules.Result.Error
import com.github.rules.Result.Passed

fun valid() = Passed<Any?>(null)
fun <T> valid(a: T) = Passed(a)

fun error() = Error<Any?>(null)
fun <T> error(e: T) = Error(e)

fun <T> rule(eval: (T) -> Result<*, *>) = IRule(eval)

fun <T> IRule<T>.next(next: IRule<T>) = rule<T> {
    val thisRes = this.eval(it)
    val nextRes = next.eval(it)

    when {
        thisRes is Error<*> -> thisRes.copy(
            next = if(nextRes is Error<*>) nextRes else null
        )
        nextRes is Error<*> -> nextRes
        else -> valid()
    }
}

inline fun <reified T> IRule<T>.nextCatching(next: IRule<T>) = rule<T> {
    try {
        this.next(next).eval(it)
    } catch (e: Exception) {
        error(e)
    }
}