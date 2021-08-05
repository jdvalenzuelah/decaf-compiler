package com.github.dcc.compiler.resolvers

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.symbols.SymbolTable
import com.github.dcc.decaf.symbols.emptySymbolTable
import com.github.dcc.decaf.symbols.symbolTableOf
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import com.github.dcc.parser.genSignature
import kotlin.random.Random

/*
    Resolve Symbol (declared vars and methods) Table from a DecafParser.ProgramContext
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
            signature = ctx.genSignature(currentScope) //TODO: improve signature definition
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
        val statements = ctx?.statement()?.fold(emptySymbolTable()) { table, stm ->
            table.apply { putAll(visitStatement(stm)) }
        } ?: emptySymbolTable()

        val decls = ctx?.var_decl()?.fold(emptySymbolTable()) { table, decl ->
            table.apply { putAll(visitVar_decl(decl)) }
        } ?: emptySymbolTable()

        return emptySymbolTable().apply {
            putAll(statements)
            putAll(decls)
        }
    }

    override fun visitStatement(ctx: DecafParser.StatementContext?): SymbolTable {
        return ctx?.if_expr()?.let(::visitIf_expr)
            ?: ctx?.while_expr()?.let(::visitWhile_expr)
            ?: ctx?.block()?.let(::visitBlock)
            ?: emptySymbolTable()
    }

    override fun visitIf_expr(ctx: DecafParser.If_exprContext?): SymbolTable {
        val ifBlock = withChildScope("if") {
            visitBlock(ctx?.if_block()?.block())
        }

        val elseBlock = withChildScope("else") {
            visitBlock(ctx?.else_block()?.block())
        }

        return emptySymbolTable().apply {
            putAll(ifBlock)
            putAll(elseBlock)
        }
    }

    override fun visitWhile_expr(ctx: DecafParser.While_exprContext?): SymbolTable {
        return withChildScope("while") { visitBlock(ctx?.block()) }
    }

}

private  fun Symbol.genId(): String = this.id