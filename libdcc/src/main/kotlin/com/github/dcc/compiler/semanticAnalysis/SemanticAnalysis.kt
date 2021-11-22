package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.Error
import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.compiler.symbols.ProgramSymbols
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import com.github.validation.Validated
import com.github.validation.then
import com.github.validation.zip
import org.tinylog.kotlin.Logger

class SemanticAnalysis internal constructor(
    private val programSymbols: ProgramSymbols
): DecafBaseVisitor<Validated<Error>>() {

    companion object {
        internal operator fun invoke(symbols: ProgramSymbols, program: DecafParser.ProgramContext): Validated<Error> {
            return SemanticAnalysis(symbols)
                .analyze(program)
        }
    }


    fun analyze(program: DecafParser.ProgramContext): Validated<Error> {
        return SemanticRules.symbolsRules
            .invoke(programSymbols)
            .then(visitProgram(program))
    }

    private val typeResolver = StaticTypeResolver()

    override fun visitProgram(ctx: DecafParser.ProgramContext): Validated<Error> {
        Logger.info("Starting semantic analysis")
        return ctx.method_decl().map(::visitMethod_decl).zip()
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext): Validated<Error> {
        return SemanticRules.MethodBlocksRules(
            programSymbols.symbolTable.getNextChildScope(ctx.method_sign().ID().text),
            programSymbols.methods,
            programSymbols.types,
            typeResolver.visitMethod_decl(ctx) ?: Type.Void,
        ).visitBlock(ctx.block())
    }

}