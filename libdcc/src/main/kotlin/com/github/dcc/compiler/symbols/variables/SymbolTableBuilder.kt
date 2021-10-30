package com.github.dcc.compiler.symbols.variables

import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.enviroment.lineageAsString
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.SymbolStore
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import org.tinylog.kotlin.Logger

internal class DeclarationResolver(private val scope: Scope = Scope.Global) : DecafBaseVisitor<Declaration>() {

    private val typeResolver = StaticTypeResolver()

    override fun visitVar_decl(ctx: DecafParser.Var_declContext): Declaration.Variable {
        return ctx.array_decl()?.let(::visitArray_decl)
            ?: ctx.prop_decl()!!.let(::visitProp_decl)
    }

    override fun visitProp_decl(ctx: DecafParser.Prop_declContext): Declaration.Variable {
        return Declaration.Variable(
            name = ctx.ID().text,
            type = typeResolver.visitProp_decl(ctx) ?: Type.Nothing,
            scope = scope,
            context = ctx
        )
    }

    override fun visitArray_decl(ctx: DecafParser.Array_declContext): Declaration.Variable {
        return Declaration.Variable(
            name = ctx.ID().text,
            type = typeResolver.visitArray_decl(ctx) ?: Type.Nothing,
            scope = scope,
            context = ctx
        )
    }

    override fun visitParameter(ctx: DecafParser.ParameterContext): Declaration.Variable {
        return ctx.simple_param()?.let(::visitSimple_param)
            ?: ctx.array_param()!!.let(::visitArray_param)
    }

    override fun visitSimple_param(ctx: DecafParser.Simple_paramContext): Declaration.Variable {
        return Declaration.Variable(
            name = ctx.ID().text,
            type = typeResolver.visitSimple_param(ctx),
            scope = scope,
            context = ctx
        )
    }

    override fun visitArray_param(ctx: DecafParser.Array_paramContext): Declaration.Variable {
        return Declaration.Variable(
            name = ctx.ID().text,
            type = typeResolver.visitArray_param(ctx),
            scope = scope,
            context = ctx
        )
    }
}

private class GlobalScopeSymbolsBuilder private constructor(): DecafBaseVisitor<Unit>() {
    private val symbols = mutableListOf<Declaration.Variable>()

    private val declarationResolver = DeclarationResolver(Scope.Global)

    companion object {
        operator fun invoke(ctx: DecafParser.ProgramContext): SymbolStore {
            val symbols = GlobalScopeSymbolsBuilder()
                .apply { visitProgram(ctx) }
                .symbols

            Logger.info("Resolved global variables ${symbols.size}")
            return symbols
        }
    }

    override fun visitProgram(ctx: DecafParser.ProgramContext) {
        Logger.info("Visiting program: Resolving global declarations")
        ctx.var_decl()?.forEach(::visitVar_decl)
    }

    override fun visitVar_decl(ctx: DecafParser.Var_declContext) {
        Logger.info("Visiting global var decl: Resolving global declarations")
        symbols.add(declarationResolver.visitVar_decl(ctx))
    }

}

private class BlockSymbolBuilder private constructor(parent: SymbolTable, scopeLabel: String): DecafBaseVisitor<Unit>() {

    private val nextScope = parent.childScope(scopeLabel)
    private val declarationResolver = DeclarationResolver(nextScope)

    private val scopeString = nextScope.lineageAsString()

    private val symbols = mutableListOf<Declaration.Variable>()


    companion object {
        operator fun invoke(
            parent: SymbolTable,
            label: String,
            ctx: DecafParser.BlockContext,
            parameters: Collection<DecafParser.ParameterContext> = emptyList(),
        ): SymbolTable {
            val symbols = BlockSymbolBuilder(parent, label)
                .apply {
                    parameters.forEach(::visitParameter)
                    visitBlock(ctx)
                }
                .symbols
            Logger.info("Resolved ${symbols.size} variables at scope $label")
            return parent.addNewScope(label, symbols)
        }
    }

    override fun visitParameter(ctx: DecafParser.ParameterContext) {
        Logger.info("Visiting parameters: Resolving local parameters declarations at scope $scopeString")
        val decl = declarationResolver.visitParameter(ctx)
        Logger.info("Visiting var: Resolved variable declaration $decl")
        symbols.add(decl)
    }

    override fun visitBlock(ctx: DecafParser.BlockContext) {
        Logger.info("Visiting block: Resolving local block declarations at scope $scopeString")
        ctx.block_el().forEach { it.var_decl()?.let(::visitVar_decl) }
    }

    override fun visitVar_decl(ctx: DecafParser.Var_declContext) {
        val decl = declarationResolver.visitVar_decl(ctx)
        Logger.info("Visiting var: Resolved variable declaration $decl")
        symbols.add(decl)
    }

}


private class BlockScopeSymbolBuilder private constructor(val parent: SymbolTable, val scopeLabel: String, block: DecafParser.BlockContext, parameters: Collection< DecafParser.ParameterContext>): DecafBaseVisitor<Unit>() {
    private val thisSymbol = BlockSymbolBuilder(parent, scopeLabel, block, parameters)

    companion object {
        operator fun invoke(
            parent: SymbolTable,
            scopeLabel: String,
            block: DecafParser.BlockContext,
            parameters: Collection<DecafParser.ParameterContext>
        ): SymbolTable {
            return BlockScopeSymbolBuilder(parent, scopeLabel, block, parameters)
                .apply { visitBlock(block) }
                .thisSymbol
        }

        operator fun invoke(parent: SymbolTable, ctx: DecafParser.Method_declContext): SymbolTable {
            return invoke(parent, ctx.method_sign().ID().text, ctx.block(), ctx.method_sign().parameter())
        }
    }

    override fun visitBlock(ctx: DecafParser.BlockContext) {
        Logger.info("Resolving block variables label=$scopeLabel")
        ctx.block_el().forEach { it.statement()?.let(::visitStatement) }
    }

    override fun visitStatement(ctx: DecafParser.StatementContext) {
        Logger.info("Resolving block statement variables label=$scopeLabel")
        when {
            ctx.if_expr() != null -> visitIf_expr(ctx.if_expr())
            ctx.while_expr() != null -> visitWhile_expr(ctx.while_expr())
            ctx.block() != null -> visitBlock(ctx.block())
        }
    }

    override fun visitIf_expr(ctx: DecafParser.If_exprContext) {
        Logger.info("Resolving if block statement variables label=$scopeLabel")
        invoke(thisSymbol, "if", ctx.if_block().block()!!, emptyList())
        ctx.else_block()?.let {
            Logger.info("Resolving else block statement variables label=$scopeLabel")
            invoke(thisSymbol, "else", it.block()!!, emptyList())
        }
    }

    override fun visitWhile_expr(ctx: DecafParser.While_exprContext) {
        Logger.info("Resolving while block statement variables label=$scopeLabel")
        invoke(thisSymbol, "while", ctx.block(), emptyList())
    }

}

class SymbolTableBuilder private constructor(program: DecafParser.ProgramContext): DecafBaseVisitor<Unit>() {

    private val globalSymbols: SymbolTable = SymbolTable(
        symbols = GlobalScopeSymbolsBuilder(program),
        scope = Scope.Global,
        parent = null,
        child = mutableListOf(),
    )

    companion object {
        operator fun invoke(program: DecafParser.ProgramContext): SymbolTable {
            return SymbolTableBuilder(program)
                .apply { visitProgram(program) }
                .globalSymbols
        }
    }

    override fun visitProgram(ctx: DecafParser.ProgramContext) {
        ctx.method_decl().forEach(::visitMethod_decl)
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext) {
        BlockScopeSymbolBuilder(globalSymbols, ctx)
    }

}