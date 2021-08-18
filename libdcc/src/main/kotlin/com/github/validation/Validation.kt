package com.github.validation

fun interface Validation<in I, out E> : (I) -> Validated<E>

infix fun <E> Validated<E>.then(next: Validated<E>): Validated<E> {
    return when(this) {
        is Validated.Valid -> when(next) {
            is Validated.Valid -> Validated.Valid
            is Validated.Invalid ->  next
        }
        is Validated.Invalid -> when(next) {
            is Validated.Valid -> this
            is Validated.Invalid -> this.append(next)
        }
    }
}

fun <I, E> Validation<I, E>.exhaustive(next: Validation<I, E>) = Validation<I, E> { this(it) then next(it) }

infix fun <I, E> Validation<I, E>.then(next: Validation<I, E>) = this.exhaustive(next)

fun <I, E> Validation<I, E>.shortCircuit(next: Validation<I, E>) = Validation<I, E> {
    val t = this(it)
    if(t is Validated.Invalid) t else next(it)
}

fun <E>  Collection<Validated<E>>.zip(): Validated<E> =
    this.fold(Validated.Valid as Validated<E>) { acc, next ->
        acc.then(next)
    }


class ValidationChainBuilder<I, E> {
    private var baseValidation = Validation<I, E> { Validated.Valid }

    operator fun Validation<I, E>.unaryPlus() { baseValidation = baseValidation.then(this@unaryPlus) }

    fun build(): Validation<I, E> = baseValidation

}

fun <I, E> validation(init: ValidationChainBuilder<I, E>.() -> Unit): Validation<I, E> = ValidationChainBuilder<I, E>()
    .apply(init)
    .build()

fun <I, E> validated(test: I, init: ValidationChainBuilder<I, E>.() -> Unit) = validation(init).invoke(test)