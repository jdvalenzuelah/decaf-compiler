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


fun <E> Error<E>.tail(): Error<E> = next?.tail() ?: this

fun <T, E> Iterable<T>.zip(rule: IRule<T, *, E>): Result<*, E> {
    val results = map { rule.eval(it) }

    return if(results.all { it is Passed })
        valid()
    else {
        val errors = results.filterIsInstance<Error<E>>().toMutableList()

        val base = errors.removeFirst().tail()

        errors.fold(base) { acc, next ->
            acc.copy(next = next)
        }
    }
}