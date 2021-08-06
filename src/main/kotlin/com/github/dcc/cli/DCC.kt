package com.github.dcc.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.dcc.cli.ui.Prettify
import com.github.dcc.compiler.semanticAnalysis.SemanticAnalysis
import com.github.dcc.parser.*
import com.github.validation.Validated
import org.antlr.v4.runtime.ANTLRInputStream
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
        //TODO: Extract to service
        val charStream = ANTLRInputStream(file.inputStream())
        val lexer = DecafLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = DecafParser(tokenStream)

        if(printParseTree) {
            tokenStream.reset() // make sure stream is at start
            echo(Prettify.tree(parser.program(), parser.ruleNames.toList()))
        }

        if(!justParser) {
            when(val res = SemanticAnalysis(parser)) {
                is Validated.Valid -> echo("Passed!")
                is Validated.Invalid -> echo(Prettify.semanticErrors(file.path, res, charStream), err = true)
            }
        }

    }

}
