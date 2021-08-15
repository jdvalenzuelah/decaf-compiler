package com.github.dcc.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.dcc.cli.ui.Prettify
import com.github.dcc.compiler.CompilerContext
import com.github.dcc.compiler.context.Context
import com.github.dcc.compiler.resolvers.ProgramContextResolver
import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.dcc.parser.*
import com.github.validation.Validated
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CommonTokenStream

object DCC : CliktCommand() {

    private val file by argument(help = "file to compile")
        .file(mustExist = true, mustBeReadable = true)

    private val justParser by option("-E", help = "Only run the parser")
        .flag(default = false) //TODO: implement once compiler is implemented

    private val printParseTree by option("-dt", "--dump-tree", help = "Prints parse tree in unix's tree utility style")
        .flag(default = false)

    //TODO
    private val printSymbolTable by option("-ds", "--dump-symbols", help = "Prints symbol table in plain text")
        .flag(default = false)

    //TODO
    private val printTypeTable by option("-dtt", "--dump-types", help = "Prints struct table in plain text")
        .flag(default = false)

    //TODO
    private val target by option("--target", help = "Generate code for the given target")
        .default("") //TODO: Add default target



    override fun run() {
        /*
        TODO: - apply rules to context, structure resolver/variables properly, prettify cli variable and method dump
         */
        val charStream = ANTLRInputStream(file.inputStream())
        val lexer = DecafLexer(charStream)
        val tokenStream = BufferedTokenStream(lexer)
        val compilerContext = CompilerContext(tokenStream = tokenStream)

        if(printParseTree) {
            tokenStream.reset() // make sure stream is at start
            echo(Prettify.tree(compilerContext.parser.program(), compilerContext.parser.ruleNames.toList()))
        }

        /*if(!justParser) {
            when(val res = SemanticAnalysis(compilerContext.parser)) {
                is Validated.Valid -> echo("Passed!")
                is Validated.Invalid -> echo(Prettify.semanticErrors(file.path, res, charStream), err = true)
            }
        }*/


        ProgramContextResolver.resolve(compilerContext).allVariables().forEach {
            println(it)
        }

    }

}

fun Context.ProgramContext.allVariables() = variables + methods
    .flatMap { method -> method.block?.allVariables() ?: emptyList() }

fun Context.BlockContext.allVariables(): List<Context.VariableContext> = variables + statements.flatMap {
    when(val exp = it.expression) {
        is Context.IfExpressionContext -> {
            exp.ifBlockContext.block.allVariables() + (exp.elseBlock?.block?.allVariables() ?: emptyList())
        }
        is Context.WhileContext -> exp.block.allVariables()
        is Context.BlockContext -> exp.allVariables()
        is Context.ExpressionContext,
        is Context.AssignmentContext,
        is Context.MethodCallContext,
        is Context.ReturnContext -> emptyList()
        else -> emptyList()
    }
}