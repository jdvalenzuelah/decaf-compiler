package com.github.dcc.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.dcc.cli.utils.Prettify
import com.github.dcc.parser.DecafLexer
import com.github.dcc.parser.DecafParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

object DCC : CliktCommand() {

    private val file by argument(help = "file to compile")
        .file(mustExist = true, mustBeReadable = true)

    private val justParser by option("-E", help = "Only run the parser")
        .flag(default = false) //TODO: implement once compiler is implemented

    private val printParseTree by option("-d", "--dump", help = "Prints parse tree in unix's tree utility style")
        .flag(default = false)

    private val target by option("--target", help = "Generate code for the given target")
        .default("") //TODO: Add default target



    override fun run() {
        //TODO: Extract to service
        val charStream = ANTLRInputStream(file.inputStream())
        val lexer = DecafLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = DecafParser(tokenStream)
        val tree = parser.program()

        if(printParseTree)
            echo(Prettify.tree(tree, parser.ruleNames.toList()))
    }

}