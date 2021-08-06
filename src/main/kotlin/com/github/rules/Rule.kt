package com.github.rules

import com.github.rules.Result.Error
import com.github.rules.Result.Passed

fun valid() = Passed<Any?>(null)
fun <T> valid(a: T) = Passed(a)

fun error() = Error<Any?>(null)
fun <T> error(e: T) = Error(e)

fun <T, A, E> rule(eval: (T) -> Result<A, E>) = IRule(eval)

inline fun <T, reified E> IRule<T, *, E>.next(next: IRule<T, *, E>): IRule<T, *, E> = rule {
    val thisRes = this.eval(it)
    val nextRes = next.eval(it)

    when {
        thisRes is Error<E> -> thisRes.copy(
            next = if(nextRes is Error<E>) nextRes else null
        )
        nextRes is Error<E> -> nextRes
        else -> valid()
    }
}