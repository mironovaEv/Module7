package com.example.module7.Model

class AlmostVirtualMachine (private val codeText: String = "") {
    class Operands (val variable: String, val expr: String)
    class Instruction (val name: String, val ops: Operands)

    var output = ""
    var input = ""
    var doLog = false
    var infiniteLoopStop = Int.MAX_VALUE
    var globalInts: MutableMap<String, Int> = mutableMapOf()
    var globalArrays: MutableMap<String, MutableList<Int>> = mutableMapOf()
    var globalTypes: MutableMap<String, String> = mutableMapOf()
    private var instrHandlers = mapOf(
        "int" to this::newInt,
        "arr" to this::newArr,
        "=" to this::assign,
        "+=" to this::increase,
        "-=" to this::decrease,
        "++" to this::increment,
        "--" to this::decrement,
        "out" to this::out,
        "in" to this::input,
        "if" to this::conditionalOperator,
        "while" to this::whileLoop
    )
    private var pointer = 0
    fun getPointer(): Int {
        return pointer
    }
    private var halt = false
    private val calc = Calculations()
    private var instructionsList = convert()

    fun findInstruction(instrName: String): Int? {
        for (i in instructionsList.indices) {
            if (instrName == instructionsList[i].name) return i
        }
        return null
    }

    fun globalVariablesLog(message: String) {
        if (!doLog) return
        println(message)
        for (variable in globalInts) {
            print("${variable.key} = ${variable.value}; ")
        }
        for (variable in globalArrays) {
            print("${variable.key} = ${variable.value}; ")
        }
        println()
    }

    private fun instructionLog(ptr: Int, instr: Instruction) {
        if (!doLog) return
        print("$ptr-> ")
        print("instruction: ${instr.name}, ")
        print("variable: ${instr.ops.variable}, ")
        println("expression: ${instr.ops.expr}:")
    }
    
    fun execute(startIndex: Int = pointer, stopInstr: List<String> = listOf()) {
        pointer = startIndex
        while (pointer < instructionsList.size && !halt) {
            val instr = instructionsList[pointer]
            instructionLog(pointer, instr)
            if (instr.name in instrHandlers) {
                try {
                    instrHandlers[instr.name]?.invoke(instr.ops)
                } catch (e: Error) {
                    throw Error("ERROR: ${e.message} at\n-> ${instr.name} ${instr.ops.variable} ${instr.ops.expr}")
                }
            }
            if (instr.name in stopInstr) break
            pointer++
        }
    }

    private fun skipBlock(stopInstr: List<String> = listOf("end")) {
        var depth = 0
        while (pointer < instructionsList.size) {
            if (instructionsList[pointer].name in stopInstr) {
                if (depth <= 1) break
                else depth--
            }
            if (instructionsList[pointer].name in listOf("while", "if")) depth++
            pointer++
        }
    }

    private fun newInt(ops: Operands) {
        when {
            Regex("""[A-Za-z_]\w*""").matchEntire(ops.variable) == null -> throw Error("VARIABLE NAME ${ops.variable} IS INVALID")
            ops.variable in globalTypes -> throw Error("VARIABLE ${ops.variable} ALREADY EXISTS")
            else -> {
                globalTypes[ops.variable] = "int"
                globalInts[ops.variable] = 0
                globalVariablesLog("GLOBAL VARIABLES:")
            }
        }
    }

    private fun newArr(ops: Operands) {
        when {
            Regex("""[A-Za-z_]\w*""").matchEntire(ops.variable) == null -> throw Error("VARIABLE NAME ${ops.variable} IS INVALID")
            ops.variable in globalTypes -> throw Error("VARIABLE ${ops.variable} ALREADY EXISTS")
            else -> {
                globalTypes[ops.variable] = "array"
                globalArrays[ops.variable] = MutableList(calculate(dereference(ops.expr)), { 0 })
                globalVariablesLog("GLOBAL VARIABLES:")
            }
        }
    }

    private fun assign(ops: Operands) {
        when {
            ops.variable == "" -> throw Error("NO VARIABLE NAME GIVEN")
            Regex("""[A-Za-z_]\w*""").find(ops.variable)?.value !in globalTypes ->
                throw Error("VARIABLE ${ops.variable} DOES NOT EXIST")
            ops.expr == "" -> throw Error("NO EXPRESSION GIVEN")
            else -> {
                val reg = Regex("""(?<Name>[^\[\]]+)\[(?<Index>.+)\]""")
                val match = reg.matchEntire(ops.variable)
                if (match == null) {
                    globalInts[ops.variable] = calculate(dereference(ops.expr))
                } else {
                    globalArrays[match.groupValues[1]]?.set(
                        calculate(dereference(match.groupValues[2])),
                        calculate(dereference(ops.expr))
                    )
                }
                globalVariablesLog("GLOBAL VARIABLES:")
            }
        }
    }

    private fun increase(ops: Operands) {
        assign(Operands(ops.variable, "${ops.variable} + ${ops.expr}"))
    }

    private fun decrease(ops: Operands) {
        assign(Operands(ops.variable, "${ops.variable} - ${ops.expr}"))
    }

    private fun increment(ops: Operands) {
        if (ops.expr != "") println("WARNING: UNNEEDED EXPRESSION")
        increase(Operands(ops.variable, "1"))
    }

    private fun decrement(ops: Operands) {
        if (ops.expr != "") println("WARNING: UNNEEDED EXPRESSION")
        decrease(Operands(ops.variable, "1"))
    }

    private fun out(ops: Operands) {
        val newOutput: String
        if (Regex("""\".*\"""").matchEntire(ops.expr) != null) {
            val reg = Regex("""\*\{([^\{\}]+)\}""")
            newOutput = reg.replace(ops.expr.dropLast(1).drop(1)) { match ->
                if (Regex("""\[.+\]""").find(match.groupValues[1]) == null && globalTypes[match.groupValues[1]] == "array") {
                    globalArrays[match.groupValues[1]].toString()
                } else getVal(match.groupValues[1]).toString()
            }
        } else {
            if (Regex("""\[.+\]""").find(ops.expr) == null && globalTypes[ops.expr] == "array") {
                newOutput = globalArrays[ops.expr].toString()
            } else {
                newOutput = calculate(dereference(ops.expr)).toString()
            }
        }
        output += newOutput + "\n"
    }

    private fun conditionalOperator(ops: Operands) {
        if (ops.variable != "") println("WARNING: UNNEEDED VARIABLE NAME")
        if (calculateLogical(ops.expr)) {
            execute(pointer + 1, listOf("end", "else"))
            if (pointer < instructionsList.size && instructionsList[pointer].name == "else") {
                skipBlock()
            }
        } else {
            skipBlock(listOf("end", "else"))
            if (pointer < instructionsList.size && instructionsList[pointer].name == "else") {
                execute(pointer + 1, listOf("end"))
            }
        }
    }

    private fun whileLoop(ops: Operands) {
        if (ops.variable != "") println("WARNING: UNNEEDED VARIABLE NAME")
        val returnPoint = pointer
        var loop = 0
        while (calculateLogical(ops.expr)) {
            execute(returnPoint + 1, listOf("end"))
            loop++
            if (loop > infiniteLoopStop) {
                throw Error("Infinite loop")
            }
            if (halt) {
                return
            }
        }
        skipBlock()
    }

    private fun input(ops: Operands) {
        val match = Regex("""\s*\b(\d+)\b""").find(input)
        if (match != null) {
            assign(Operands(ops.variable, match.groupValues[1]))
            input = input.removePrefix(match.value)
        } else {
            halt = true
            throw Error("Incorrect input")
        }
    }

    private fun getVal(variable: String): Int? {
        val reg = Regex("""(?<Name>[^\[\]]+)\[(?<Index>.+)\]""")
        val match = reg.matchEntire(variable)
        return if (match == null) {
            globalInts[variable]
        } else {
            globalArrays[match.groupValues[1]]?.get(calculate(dereference(match.groupValues[2])))
        }
    }

    private fun dereference(expr: String): String {
        var finalExpr = expr
        val reg = Regex("""[A-Za-z_]\w*(?:\[.+\])?""")
        val matches = reg.findAll(finalExpr)
        for (match in matches) {
            finalExpr = finalExpr.replace(match.value, getVal(match.value).toString())
        }
        return finalExpr
    }

    private fun calculate(expr: String): Int {
        val result: Int
        try {
            result = calc.calculation(expr)
        } catch (e: Throwable) {
            throw Error("COULD NOT CALCULATE EXPRESSION ${expr}")
        }
        return result
    }

    private fun calculateLogical(expr: String): Boolean {
        val reg = Regex("^(?<operand1>[^<>=!]+) (?<operator>>|<|>=|<=|==|!=) (?<operand2>[^<>=!]+)\$")
        val match = reg.find(expr) ?: throw Error ("???????????????????????? ???????????????????? ??????????????????: $expr")
        val a = calculate(dereference(match.groupValues[1]))
        val op = match.groupValues[2]
        val b = calculate(dereference(match.groupValues[3]))
        return when (op) {
            ">" -> a > b
            "<" -> a < b
            ">=" -> a >= b
            "<=" -> a <= b
            "==" -> a == b
            "!=" -> a != b
            else -> false
        }
    }

    private fun formatExpression(expr: String): String {
        var exprNew = expr
        if ('"' !in expr) {
            exprNew = Regex("""[-+*\/%]|[<>!=]=|[<>]""").replace(expr) { match -> " ${match.value} " }
            exprNew = Regex("""\(""").replace(exprNew, "( ")
            exprNew = Regex("""\)""").replace(exprNew, " )")
            exprNew = Regex("""\s{2,}""").replace(exprNew, " ")
        }
        return Regex("""(?<=^|\[)\s+|\s+(?=$|\])""").replace(exprNew, "")
    }

    private fun convertLine(codeLine: String): Instruction {
        val reg = Regex("""(?:^|\n)(?<Name>[^;\s]*)(?: (?<Variable>[A-Za-z_]\w*(?:\[.+\])?))? ?(?<Expression>'.*')?;$""")
        val match = reg.find(codeLine) ?: throw Error("???????????????????????? ????????????????????: $codeLine")
        return Instruction(match.groupValues[1], Operands(match.groupValues[2], match.groupValues[3].drop(1).dropLast(1)))
    }

    private fun convert(): List<Instruction> {
        val instructionsList: MutableList<Instruction> = mutableListOf()
        val reg = Regex("(?:^|\\n).*;")
        val matches = reg.findAll(codeText)
        for (match in matches) {
            instructionsList += convertLine(match.value)
        }
        return instructionsList
    }

    fun pushInstr(name: String, variable: String = "", expr: String = "") {
        instructionsList = instructionsList + Instruction(name, Operands(formatExpression(variable), formatExpression(expr)))
    }
}