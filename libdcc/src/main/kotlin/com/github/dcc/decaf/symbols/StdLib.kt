package com.github.dcc.decaf.symbols

import com.github.dcc.decaf.DecafStdLib
import com.github.dcc.decaf.types.Type

object StdLib : DecafStdLib<Declaration.Method> {

    override val InputInt: Declaration.Method = Declaration.Method(
        name = "InputInt",
        type = Type.Int,
        parameters = emptyList(),
        context = null
    )


    override val OutputInt: Declaration.Method = Declaration.Method(
        name = "OutputInt",
        type = Type.Void,
        parameters = listOf(
            Declaration.Parameter("n", Type.Int, null)
        ),
        null
    )
}