/*
 * Copyright (C)2018 - Deny Prasetyo <jasoet87@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jdvalenzuelah

import com.github.jdvalenzuelah.parser.DecafParser
import com.github.jdvalenzuelah.parser.DecafLexer
import  org.antlr.v4.runtime.*

fun main() {
    val charStream: ANTLRInputStream = ANTLRFileStream("/Users/josuevalenzuela/Documents/per/u/A5S2/cc3032/decaf-compiler/src/main/resources/helloWorld.decaf")
    val lexer = DecafLexer(charStream)
    val tokenStream = CommonTokenStream(lexer)
    val parser = DecafParser(tokenStream)
    val tree = parser.program()
    tree.inspect(parser)
}
