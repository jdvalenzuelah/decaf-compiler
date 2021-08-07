package com.github.validation

sealed class Validated<out E> {

    object Valid : Validated<Nothing>()

    data class Invalid<E>(
        val e: E,
        var next: Invalid<E>? = null,
    ) : Validated<E>(), Iterable<Invalid<E>> {
        override fun iterator(): Iterator<Invalid<E>> = InvalidResultIterator(this)
    }

}

class InvalidResultIterator<E>(inv: Validated.Invalid<E>) : Iterator<Validated.Invalid<E>> {
    private var current: Validated.Invalid<E>? = inv
    override fun hasNext(): Boolean = current != null

    override fun next(): Validated.Invalid<E> {
        val tc = current
        current = current?.next
        return tc ?: error("Reached end!")
    }
}

fun <E> Validated.Invalid<E>.tail(): Validated.Invalid<E> = next?.tail() ?: this

fun <E> Validated.Invalid<E>.append(new: Validated.Invalid<E>) = apply { this.tail().next = new }
