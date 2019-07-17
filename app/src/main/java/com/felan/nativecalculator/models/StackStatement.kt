package com.felan.nativecalculator.models

import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList

class StackStatement(val parent: StackStatement? = null) : CalcObject(), Calcable {
    companion object {
        init {
            System.loadLibrary("native-calc")
        }
    }

    private val items: ArrayList<CalcObject> = ArrayList()

    override fun calc(): Double {
        return nativeCalc(items.toArray(arrayOf()), items.size)
    }

    private external fun nativeCalc(objects: Array<CalcObject>, size: Int): Double

    fun pushItem(item: CalcObject) {
        if (items.isNotEmpty() &&
            (items[items.size - 1] is Operator && item is Operator ||
                    items[items.size - 1] !is Operator && item !is Operator)
        )
            throw IllegalStateException("Cannot have same items after another")

        items.add(item)
    }

    fun peek() = if (items.isEmpty()) null else items[items.size - 1]

    fun pop(): CalcObject {
        val temp = peek()!!
        items.removeAt(items.size - 1)
        return temp
    }

    override fun toString(): String =
        "(" + items.joinToString(" ") + ")"
}