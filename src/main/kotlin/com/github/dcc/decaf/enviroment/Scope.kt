package com.github.dcc.decaf.enviroment

/*
 Supported envs defined in decaf spec
*/
sealed class Scope(
    open val parent: Scope
) {
    object Global : Scope(Global) {
        override fun toString(): String = this::class.simpleName ?: super.toString()
    }

    data class Local(
        val name: String, //method name
        override val parent: Scope = Global
    ) : Scope(parent) {
        override fun toString(): String = name
    }

    val parents: List<Scope>
        get() {
            val acc = mutableListOf<Scope>()
            var next = parent
            while (next != Global) {
                acc.add(next)
                next = next.parent
            }
            return acc
        }

    fun genId(): String {

       return when(this) {
            is Global -> Global.toString()
            is Local -> {
                parents.toMutableList().apply {
                    reverse()
                    add(this@Scope)
                }.joinToString(separator = "@") { it.toString() }
            }
        }
    }

    fun child(name: String) = Local(name, this)

}
