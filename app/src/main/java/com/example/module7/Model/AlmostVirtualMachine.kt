package com.example.module7.Model

class AlmostVirtualMachine (private val codeText: String) {
    class Operands (val variable: String, val expr: String)
    class Instruction (val name: String, val attr: Operands)

    var output = ""
    var doLog = false
    var globalInts: MutableMap<String, Int> = mutableMapOf()
    var globalIntArrays: MutableMap<String, MutableList<Int>> = mutableMapOf()
    private var instrHandlers = mapOf(
        "var" to this::newVar,
        "=" to this::assign,
        "+=" to this::increase,
        "-=" to this::decrease,
        "++" to this::increment,
        "--" to this::decrement,
        "out" to this::out,
        "if" to this::conditionalOperator,
        "while" to this::whileLoop
    )
    private var pointer = 0
    private val calc = Calculations()
    val instructionsList = convert()

    fun globalVariablesLog(message: String) {
        if (!doLog) return
        println(message)
        for (variable in globalInts) {
            print("${variable.key} = ${variable.value}; ")
        }
        println()
    }

    private fun instructionLog(ptr: Int, instr: Instruction) {
        if (!doLog) return
        print("$ptr-> ")
        print("name: ${instr.name}, ")
        print("variable: ${instr.attr.variable}, ")
        println("expression: ${instr.attr.expr}:")
    }

    fun execute() {
        executeAt(pointer, listOf())
    }

    private fun executeAt(startIndex: Int, stopInstr: List<String>) {
        pointer = startIndex
        while (pointer < instructionsList.size) {
            val instr = instructionsList[pointer]
            instructionLog(pointer, instr)
            if (instr.name in instrHandlers) {
                instrHandlers[instr.name]?.invoke(instr.attr)
            }
            if (instr.name in stopInstr) break
            pointer++
        }
    }

    private fun skipTo(instrNames: List<String>) {
        while (pointer < instructionsList.size) {
            if (instructionsList[pointer].name in instrNames) break
            pointer++
        }
    }

    private fun newVar(ops: Operands) {
        when {
            ops.variable == "" -> println("WARNING: NO VARIABLE NAME GIVEN")
            ops.variable in globalInts -> println("WARNING: VARIABLE ${ops.variable} ALREADY EXISTS")
            ops.expr == "" -> {
                globalInts[ops.variable] = 0
                globalVariablesLog("GLOBAL VARIABLES:")
            }
            else -> {
                globalInts[ops.variable] = calc.calculation(dereference(ops.expr))
                globalVariablesLog("GLOBAL VARIABLES:")
            }
        }
    }

    private fun assign(ops: Operands) {
        when {
            ops.variable == "" -> println("WARNING: NO VARIABLE NAME GIVEN")
            ops.variable !in globalInts -> println("WARNING: VARIABLE ${ops.variable} DOES NOT EXIST")
            ops.expr == "" -> println("WARNING: NO EXPRESSION GIVEN")
            else -> {
                globalInts[ops.variable] = calc.calculation(dereference(ops.expr))
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
        output += when {
            ops.variable == "" -> "${ops.expr}\n"
            ops.expr ==  "" -> "${ops.variable} = ${globalInts[ops.variable]}\n"
            else -> "${ops.variable} = ${globalInts[ops.variable]} -- ${ops.expr}"
        }
    }

    private fun conditionalOperator(ops: Operands) {
        if (ops.variable != "") println("WARNING: UNNEEDED VARIABLE NAME")
        if (calculateLogical(ops.expr)) {
            executeAt(pointer + 1, listOf("end", "else"))
            if (pointer < instructionsList.size && instructionsList[pointer].name == "else") {
                skipTo(listOf("end"))
            }
        } else {
            skipTo(listOf("end", "else"))
            if (pointer < instructionsList.size && instructionsList[pointer].name == "else") {
                executeAt(pointer + 1, listOf("end"))
            }
        }
    }

    private fun whileLoop(ops: Operands) {
        if (ops.variable != "") println("WARNING: UNNEEDED VARIABLE NAME")
        val returnPoint = pointer
        while (calculateLogical(ops.expr)) {
            executeAt(returnPoint + 1, listOf("end"))
        }
    }

    private fun dereference(expr: String): String {
        var finalExpr = expr
        val reg = Regex("[A-Za-z_]\\w*")
        val matches = reg.findAll(finalExpr)
        for (match in matches) {
            finalExpr = finalExpr.replace(match.value, globalInts[match.value].toString())
        }
        return finalExpr
    }

    private fun calculateLogical(expr: String): Boolean {
        val reg = Regex("^(?<operand1>[^<>=!]+) (?<operator>>|<|>=|<=|=|!=) (?<operand2>[^<>=!]+)\$")
        val match = reg.find(expr) ?: throw Error ("Некорректное логическое выражение: $expr")
        val a = calc.calculation(dereference(match.groupValues[1]))
        val op = match.groupValues[2]
        val b = calc.calculation(dereference(match.groupValues[3]))
        return when (op) {
            ">" -> a > b
            "<" -> a < b
            ">=" -> a >= b
            "<=" -> a <= b
            "=" -> a == b
            "!=" -> a != b
            else -> false
        }
    }

    private fun convertLine(codeLine: String): Instruction {
        val reg = Regex("(?:^|\\n)(?<Name>[^;\\s]*)(?: (?<Variable>[A-Za-z_]\\w*))? ?(?<Expression>'.*')?;$")
        val match = reg.find(codeLine) ?: throw Error("Некорректная инструкция: $codeLine")
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
}