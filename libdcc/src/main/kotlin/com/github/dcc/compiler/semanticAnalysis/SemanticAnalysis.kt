package com.github.dcc.compiler.semanticAnalysis

import com.github.dcc.compiler.resolvers.DeclarationResolver
import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.decaf.semanticRules.GeneralDeclarationRules
import com.github.dcc.decaf.semanticRules.GlobalScopeRules
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import com.github.validation.Validated
import com.github.validation.then
import com.github.validation.zip

class SemanticAnalysis private constructor(
    private val declarationResolver: DeclarationResolver
) : DecafBaseVisitor<Validated<SemanticError>>() {

    companion object {
        operator fun invoke(parser: DecafParser): Validated<SemanticError> {
            return SemanticAnalysis(DeclarationResolver(StaticTypeResolver()))
                .visitProgram(parser.program())
        }
    }

    private val symbols = mutableListOf<Declaration>()

    override fun visitProgram(ctx: DecafParser.ProgramContext): Validated<SemanticError> {
        val walkResult = Validated.Valid //ctx.decl().map(::visitDecl).zip()

        val globalScopeResult = GlobalScopeRules.containsJustOneMainMethod
            .then(GlobalScopeRules.mainMethodHasNoParameters)
            .then(GeneralDeclarationRules.arraySizeMustBeGreaterThanZero)
            .invoke(symbols)

        return walkResult then globalScopeResult
    }

    /*override fun visitDecl(ctx: DecafParser.DeclContext?): Validated<SemanticError> {
        return if(ctx == null)
            Validated.Valid
        else
            visitMethod_decl(ctx.method_decl())
                .then(visitStruct_decl(ctx.struct_decl()))
                .then(visitVar_decl(ctx.var_decl()))
    }*/

    override fun visitStruct_decl(ctx: DecafParser.Struct_declContext?): Validated<SemanticError> {
        if(ctx == null) return Validated.Valid
        symbols.add(declarationResolver.visitStruct_decl(ctx))
        return Validated.Valid //super.visitStruct_decl(ctx)
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext?): Validated<SemanticError> {
        if(ctx == null) return Validated.Valid
        symbols.add(declarationResolver.visitMethod_decl(ctx))
        return Validated.Valid //super.visitMethod_decl(ctx)
    }

    override fun visitVar_decl(ctx: DecafParser.Var_declContext?): Validated<SemanticError> {
        if(ctx == null) return Validated.Valid
        symbols.add(declarationResolver.visitVar_decl(ctx))
        return Validated.Valid //super.visitVar_decl(ctx)
    }

}