package com.github.dcc.compiler.resolvers

import com.github.dcc.decaf.enviroment.Scope
import com.github.dcc.decaf.symbols.Symbol
import com.github.dcc.decaf.types.TypeTable
import com.github.dcc.decaf.types.emptyTypeTable
import com.github.dcc.decaf.types.Type
import com.github.dcc.decaf.types.typeTableOf
import com.github.dcc.parser.DecafBaseVisitor
import com.github.dcc.parser.DecafParser

/*
 Resolver for customs types (aka structs) from a Decaf.Parser
*/
class TypeTableResolver private constructor(
    private val typeResolver: StaticTypeResolver
) : DecafBaseVisitor<TypeTable>() {

    companion object {
        operator fun invoke(parser: DecafParser): TypeTable {
            return TypeTableResolver(StaticTypeResolver()).visitProgram(parser.program())
        }
    }

    override fun visitProgram(ctx: DecafParser.ProgramContext?): TypeTable {
        return ctx?.decl()?.fold(emptyTypeTable()) { table, decl ->
            table.apply { putAll(visitDecl(decl)) }
        } ?: emptyTypeTable()
    }

    override fun visitDecl(ctx: DecafParser.DeclContext?): TypeTable {
        return if(ctx?.struct_decl() != null) {
            visitStruct_decl(ctx.struct_decl())
        } else
            emptyTypeTable()
    }

    override fun visitStruct_decl(ctx: DecafParser.Struct_declContext?): TypeTable {
        val name = ctx!!.ID().text
        val s = Type.StructDecl(
            name = name,
            args = ctx.var_decl().map {
                Symbol.Variable(
                    name = it?.array_decl()?.ID()?.text ?: it.prop_decl().ID()!!.text,
                    scope = Scope.Global.child(name),
                    type = typeResolver.visitVar_decl(it)!!
                )
            }
        )
        return typeTableOf(s.id to s)
    }


}