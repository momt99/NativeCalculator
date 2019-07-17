package com.felan.nativecalculator.models

class Operator(val symbol: Char) : CalcObject() {
    private external fun add(fist: Double, second: Double): Double
    private external fun sub(fist: Double, second: Double): Double
    private external fun mul(fist: Double, second: Double): Double

    fun operate(first: Double, second: Double): Double =
        when (symbol) {
            '+' -> add(first, second)
            '-' -> sub(first, second)
            '×' -> mul(first, second)
            else -> 0.0
        }


    override fun toString(): String = symbol.toString()
}