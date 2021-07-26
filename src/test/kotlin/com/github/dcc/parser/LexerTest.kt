package com.github.dcc.parser

import com.github.dcc.tokens
import org.antlr.v4.runtime.ANTLRInputStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class LexerTest {

    @Test
    fun `should get all tokens from decaf source`() {
        val source = """class Program { char var; int main() { var = 'A'; return 0; } }"""
        val charStream = ANTLRInputStream(source.toCharArray(), source.length)
        val lexer = DecafLexer(charStream)

        val expectedTokens = listOf(
            "CLASS",
            "PROGRAM",
            "OCURLY",
            "CHAR",
            "ID",
            "SEMICOLON",
            "INT",
            "ID",
            "OPARENTHESIS",
            "CPARENTHESIS",
            "OCURLY",
            "ID",
            "EQ",
            "CHAR_LITERAL",
            "SEMICOLON",
            "RETURN",
            "INT_LITERAL",
            "SEMICOLON",
            "CCURLY",
            "CCURLY"
        )

        val tokens  = lexer.tokens()

        assertEquals(expectedTokens, tokens)
    }

}