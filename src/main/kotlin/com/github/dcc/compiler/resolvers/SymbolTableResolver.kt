package com.github.dcc.compiler.resolvers

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.symbols.SymbolTable
import com.github.dcc.decaf.symbols.emptySymbolTable
import com.github.dcc.decaf.symbols.symbolTableOf
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

/*
    Resolve Symbol Table from a DecafParser.ProgramContext
*/
class SymbolTableResolver private constructor(
    private val typeResolver: StaticTypeResolver
) : DecafBaseVisitor<SymbolTable>() {

    companion object {
        operator fun invoke(parser: DecafParser): SymbolTable {
            return SymbolTableResolver(StaticTypeResolver()).visitProgram(parser.program())
        }
    }

    private var currentScope: Scope = Scope.Global

    private fun <T> withChildScope(name: String, block: () -> T): T {
        currentScope = currentScope.child(name)
        val value = block.invoke()
        currentScope = currentScope.parent
        return value
    }

    override fun visitProgram(ctx: DecafParser.ProgramContext?): SymbolTable {
        return ctx?.decl()
            ?.fold(emptySymbolTable()) { table, decl ->
                table.apply { putAll(visitDecl(decl)) }
            }
            ?: error("aaa")
    }

    override fun visitDecl(ctx: DecafParser.DeclContext?): SymbolTable {
        return when {
            ctx?.var_decl() != null -> visitVar_decl(ctx.var_decl())
            ctx?.method_decl() != null -> visitMethod_decl(ctx.method_decl())
            else -> emptySymbolTable()
        }
    }

    override fun visitVar_decl(ctx: DecafParser.Var_declContext?): SymbolTable {
        return ctx?.array_decl()?.let(::visitArray_decl)
            ?: ctx?.prop_decl()!!.let(::visitProp_decl)
    }

    override fun visitProp_decl(ctx: DecafParser.Prop_declContext?): SymbolTable {
        val s = Symbol.Variable(
            name = ctx!!.ID()!!.text,
            scope = currentScope,
            type = typeResolver.visitProp_decl(ctx)!!
        )

        return symbolTableOf(s.genId() to s)
    }

    override fun visitArray_decl(ctx: DecafParser.Array_declContext?): SymbolTable {
        val s = Symbol.Variable(
            name = ctx!!.ID()!!.text,
            scope = currentScope,
            type = typeResolver.visitArray_decl(ctx)!!
        )
        return symbolTableOf(s.genId() to s)
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext?): SymbolTable {
        val s = Symbol.Method(
            name = ctx!!.ID()!!.text,
            scope = currentScope,
            type = typeResolver.visitMethod_decl(ctx)!!,
            signature = ctx.genSignature(currentScope)
        )

        val methodSymbols = withChildScope(s.name) {
            val params = ctx.parameter()?.fold(emptySymbolTable()) { table, decl ->
                table.apply { putAll(visitParameter(decl)) }
            }
            emptySymbolTable().apply {
                params?.let { putAll(it) }
                putAll(visitBlock(ctx.block()))
            }
        }

        return symbolTableOf(s.genId() to s).apply { putAll(methodSymbols) }
    }

    override fun visitParameter(ctx: DecafParser.ParameterContext?): SymbolTable {
        return ctx?.array_param()?.let(::visitArray_param)
            ?: visitSimple_param(ctx?.simple_param())
    }

    override fun visitSimple_param(ctx: DecafParser.Simple_paramContext?): SymbolTable {
        val s = Symbol.Variable(
            name = ctx!!.ID().text,
            scope = currentScope,
            type = typeResolver.visitSimple_param(ctx)
        )
        return symbolTableOf(s.genId() to s)
    }

    override fun visitArray_param(ctx: DecafParser.Array_paramContext?): SymbolTable {
        val s = Symbol.Variable(
            name = ctx!!.ID().text,
            scope = currentScope,
            type = typeResolver.visitArray_param(ctx)
        )
        return symbolTableOf(s.genId() to s)
    }

    override fun visitBlock(ctx: DecafParser.BlockContext?): SymbolTable {
        return emptySymbolTable()
    }

}

internal fun Symbol.genId(): String = when(this) {
    is Symbol.Variable -> "$scope-$name-var"
    is Symbol.Method -> "$scope-$name-fun"
}

internal fun DecafParser.Method_declContext.genSignature(scope: Scope): String {
    val name = ID()!!.text
    val args = parameter().joinToString(separator = ",") { it.text }
    return "$scope.$name($args)"
}