package com.felan.nativecalculator.models

class Operator(val symbol: Char) : CalcObject() {
    private external fun add(fist: Double, second: Double): Double
    private external fun sub(fist: Double, second: Double): Double
    private external fun mul(fist: Double, second: Double): Double

    override fun toString(): String = symbol.toString()
}