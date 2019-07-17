package com.felan.nativecalculator.models

class RawValue(val value: Double) : CalcObject(), Calcable {
    override fun calc(): Double = value

    override fun toString(): String = value.toString()
}