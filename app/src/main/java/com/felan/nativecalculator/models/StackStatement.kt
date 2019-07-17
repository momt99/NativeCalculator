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
        return nativeCalc(items.toArray(arrayOf()))
    }

    private fun calcNonnative() : Double {
        var result = 0.0
        val stack = ArrayDeque(items)
        while (!stack.isEmpty()) {
            result = if (stack.element() is Operator)
                (stack.pop() as Operator).operate(result, (stack.pop() as Calcable).calc())
            else
                (stack.pop() as Calcable).calc()
        }

        return result
    }

    private external fun nativeCalc(objects: Array<CalcObject>): Double

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