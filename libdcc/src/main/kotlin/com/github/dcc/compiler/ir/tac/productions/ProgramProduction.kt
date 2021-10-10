package com.github.dcc.compiler.ir.tac.productions

import com.github.dcc.compiler.ir.decaf.DecafElementsIR
import com.github.dcc.compiler.ir.decaf.DecafMethod
import com.github.dcc.compiler.ir.decaf.DecafProgram
import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.compiler.symbols.variables.SymbolTable
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.TypeStore
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

class ProgramProduction(private val symbols: SymbolTable, private val methods: MethodStore, private val structs: TypeStore): DecafBaseVisitor<DecafElementsIR>() {

    private val typeResolver = StaticTypeResolver()

    override fun visitProgram(ctx: DecafParser.ProgramContext): DecafProgram {
        return DecafProgram(
            symbols, structs,
            methods = ctx.method_decl().map(::visitMethod_decl)

        )
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext): DecafMethod {
        val methodName = ctx.method_sign().ID().text
        val methodScope = symbols.getNextChildScope(methodName)
        return DecafMethod(
            signature = Declaration.Method.Signature(
                name = methodName,
                parametersType = ctx.method_sign().parameter().map { typeResolver.visitParameter(it) }
            ),
            block = StatementProduction(methodScope,  methods, structs)
                .visitBlock(ctx.block()),
            symbols = methodScope
        )
    }

}