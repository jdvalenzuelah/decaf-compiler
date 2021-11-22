package com.github.dcc.compiler.symbols.methods

import com.github.dcc.compiler.resolvers.StaticTypeResolver
import com.github.dcc.compiler.symbols.types.ParameterResolver
import com.github.dcc.decaf.symbols.Declaration
import com.github.dcc.decaf.symbols.MethodStore
import com.github.dcc.decaf.symbols.StdLib
import com.github.dcc.decaf.types.Type
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser
import org.tinylog.kotlin.Logger

class MethodStoreBuilder private constructor() : DecafBaseVisitor<Unit>() {

    companion object {
        operator fun invoke(program: DecafParser.ProgramContext): MethodStore {
            return MethodStoreBuilder()
                .apply {
                    program.method_decl().forEach(::visitMethod_decl)
                }
                .methods
        }
    }

    private val typeResolver = StaticTypeResolver()
    private val parameterResolver = ParameterResolver()
    private val methods = mutableListOf<Declaration.Method>()

    init {
        injectStdLib()
    }

    private fun injectStdLib() {
        methods.add(StdLib.InputInt)
        methods.add(StdLib.OutputInt)
    }

    override fun visitMethod_decl(ctx: DecafParser.Method_declContext) {
        visitMethod_sign(ctx.method_sign())
    }

    override fun visitMethod_sign(ctx: DecafParser.Method_signContext) {
        Logger.info("Resolving method")
        val method = Declaration.Method(
            name = ctx.ID().text,
            type = typeResolver.visitMethod_type(ctx.method_type()) ?: Type.Nothing,
            parameters =ctx.parameter().map(parameterResolver::visitParameter),
            context = ctx,
        )

        if(method.name == StdLib.InputInt.name || method.name == StdLib.OutputInt.name) {
            Logger.warn("Defined stdlib method ${method.name}, ignoring.")
            return
        }

        Logger.info("Resolved method $method")
        methods.add(method)
    }

}