package com.example.module7.Model

import java.util.*

class Calculations {
    private fun conversionToRPN(infixString: String): MutableList<String> {
        val infixArr = infixString.split(" ").toMutableList()
        val stack = Stack<String>()
        val outputArr = mutableListOf<String>()
        val highPriorityOperators = arrayOf("*", "/", "%")
        val lowPriorityOperators = arrayOf("+", "-")
        while (infixArr.isNotEmpty()) {
            val symbol = infixArr[0]
            if (isNumeric(symbol)) {
                outputArr.add(symbol)
            }
            if (symbol in highPriorityOperators || symbol in lowPriorityOperators) {
                if (stack.isEmpty()) {
                    stack.push(symbol)
                } else {
                    var topSymbol = stack.peek()
                    while (stack.isNotEmpty() && ((topSymbol in highPriorityOperators && symbol in highPriorityOperators) ||
                                (topSymbol in highPriorityOperators && symbol in lowPriorityOperators) ||
                                (topSymbol in lowPriorityOperators && symbol in lowPriorityOperators)
                                )
                    ) {
                        outputArr.add(stack.pop())
                        if (stack.isNotEmpty()) {
                            topSymbol = stack.peek()
                        }


                    }
                    stack.push(symbol)
                }
            }
            if (symbol == "(") {
                stack.push(symbol)
            }
            if (symbol == ")") {
                var topSymbol = stack.pop()
                while (topSymbol != "(") {
                    outputArr.add(topSymbol)
                    topSymbol = stack.pop()
                }
            }
            infixArr.removeAt(0)

            if (infixArr.isEmpty() && stack.isNotEmpty()) {
                while (stack.isNotEmpty()) {
                    val topSymbol = stack.peek()
                    outputArr.add(topSymbol)
                    if (stack.isNotEmpty()) {
                        stack.pop()

                    }
                }
            }
        }
        return outputArr
    }

    fun calculation(infixStr: String): Int {
        val expressionInRPN = conversionToRPN(infixStr)
        val stack = Stack<String>()
        val highPriorityOperators = arrayOf("*", "/", "%")
        val lowPriorityOperators = arrayOf("+", "-")
        while (expressionInRPN.isNotEmpty()) {
            val symbol = expressionInRPN[0]
            if (isNumeric(symbol)) {
                stack.push(symbol)
            }
            if (symbol in highPriorityOperators || symbol in lowPriorityOperators) {
                val newOperand: String
                val firstOperand = stack.pop().toInt()
                val secondOperand = stack.pop().toInt()
                when (symbol) {
                    "*" -> {
                        newOperand = (firstOperand * secondOperand).toString()
                        stack.push(newOperand)
                    }
                    "/" -> {
                        newOperand = (secondOperand / firstOperand).toString()
                        stack.push(newOperand)
                    }
                    "%" -> {
                        newOperand = (secondOperand % firstOperand).toString()
                        stack.push(newOperand)
                    }
                    "+" -> {
                        newOperand = (firstOperand + secondOperand).toString()
                        stack.push(newOperand)
                    }
                    "-" -> {
                        newOperand = (secondOperand - firstOperand).toString()
                        stack.push(newOperand)
                    }
                }
            }
            expressionInRPN.removeAt(0)
        }
        return stack.pop().toInt()
    }

    private fun isNumeric(elem: String): Boolean {
        for (i in elem) {
            if (i !in "0123456789") {
                return false
            }
        }
        return true
    }
}