package com.github.rules

import com.github.rules.Result.Error
import com.github.rules.Result.Passed

fun <T> rule(eval: (T) -> Result) = IRule(eval)

fun <T> IRule<T>.next(next: IRule<T>) = rule<T> {
    val thisRes = this.eval(it)
    val nextRes = next.eval(it)

    when {
        thisRes is Error -> thisRes.copy(
            next = if(nextRes is Error) nextRes else null
        )
        nextRes is Error -> nextRes
        else -> Passed
    }
}

