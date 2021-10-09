package com.github.dcc.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.dcc.cli.ui.Prettify
import com.github.dcc.compiler.Compiler


object DCC : CliktCommand() {

    private val file by argument(help = "file to compile")
        .file(mustExist = true, mustBeReadable = true)

    private val justParser by option("-E", "--parser-only", help = "Runs just the parser")
        .flag(default = false)

    private val printParseTree by option("-dt", "--dump-tree", help = "Prints parse tree in unix's tree utility style")
        .flag(default = false)

    private val printSymbolTable by option("-ds", "--dump-symbols", help = "Prints symbol table in plain text")
        .flag(default = false)

    private val printTypeTable by option("-dtt", "--dump-types", help = "Prints struct table in plain text")
        .flag(default = false)

    //TODO
    private val target by option("--target", help = "Generate code for the given target")
        .default("") //TODO: Add default target



    override fun run() {
        val compiler = Compiler(file)

        if(printParseTree) {
            compiler.reset() // make sure stream is at start
            echo(Prettify.tree(compiler.parser.program(), compiler.parser.ruleNames.toList()))
        }


        if(printSymbolTable) {
            echo(Prettify.methods(compiler.symbols.methods))
            echo(Prettify.variables(compiler.symbols.symbolTable))
        }

        if(printTypeTable) {
            echo(Prettify.types(compiler.symbols.types))
        }

        if(!justParser) {
            val result = compiler.compileSource()
            val isError = result !is Compiler.CompilationResult.Success
            echo(Prettify.compilationResult(result, file), err = isError)
        }
    }

}