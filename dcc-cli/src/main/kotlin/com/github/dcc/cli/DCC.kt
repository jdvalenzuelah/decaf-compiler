package com.github.dcc.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.dcc.cli.ui.Prettify
import com.github.dcc.compiler.CompilerContext
import com.github.dcc.compiler.context.symbols
import com.github.dcc.compiler.context.types
import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.validation.Validated


object DCC : CliktCommand() {

    private val file by argument(help = "file to compile")
        .file(mustExist = true, mustBeReadable = true)

    private val justParser by option("-E", "--parser-only", help = "Runs just the parser")
        .flag(default = false)

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
        val compilerContext = CompilerContext(file)

        if(printParseTree) {
            compilerContext.reset() // make sure stream is at start
            echo(Prettify.tree(compilerContext.parser.program(), compilerContext.parser.ruleNames.toList()))
        }

        val programContext = compilerContext.programContext

        if(printSymbolTable) {
            echo(Prettify.symbols(programContext.symbols))
        }

        if(printTypeTable) {
            echo(Prettify.types(programContext.types))
        }

        if(!justParser) {
            val result = compilerContext.compileSource()
            val isError = result is Validated.Invalid
            echo(Prettify.compilationResult(result, file), err = isError)
        }

    }

}