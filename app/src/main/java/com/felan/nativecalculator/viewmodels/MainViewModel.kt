package com.felan.nativecalculator.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.felan.nativecalculator.models.*
import java.lang.Exception
import java.lang.StringBuilder

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val currentValue: MutableLiveData<String> = MutableLiveData()

    val fullStatement: MutableLiveData<String> = MutableLiveData()

    val currentOperator: MutableLiveData<String> = MutableLiveData()

    val lastCalced: MutableLiveData<Double> = MutableLiveData()

    private var grandParent = StackStatement()
    private var currentStatement: StackStatement = grandParent

    private var currentInput: StringBuilder = StringBuilder()

    fun nextInput(input: Char) {
        try {
            if (input.isDigit() || input == '.') {
                currentInput.append(input)
                currentValue.value = currentInput.toString()
            } else
                when (input) {
                    '\b' -> {
                        if (currentInput.isNotEmpty()) {
                            currentInput.deleteCharAt(currentInput.length - 1)
                            currentValue.value = currentInput.toString()
                        } else {
                            when {
                                currentStatement.peek() is Operator -> currentStatement.pop()
                                currentStatement.peek() is StackStatement -> {
                                    currentStatement = currentStatement.peek() as StackStatement
                                    nextInput('\b')
                                }
                                currentStatement.peek() is RawValue -> {
                                    currentInput.append((currentStatement.pop() as RawValue).value)
                                    nextInput('\b')
                                }
                            }
                        }
                    }
                    '(' -> {
                        if (currentStatement.peek() != null && currentStatement.peek() is RawValue)
                            throw Exception("Cannot insert parenthesis.")
                        val item = StackStatement(currentStatement)
                        currentStatement.pushItem(item)
                        currentStatement = item
                    }
                    ')' -> {
                        if (currentStatement.parent == null)
                            throw Exception("Cannot insert parenthesis.")
                        pushCurrentInput()
                        currentStatement = currentStatement.parent!!
                    }
                    '=' -> {
                        if (currentStatement != grandParent)
                            throw Exception("Open parenthesis")
                        if (currentInput.isNotEmpty())
                            pushCurrentInput()
                        calcAndCollapse()
                        lastCalced.value = currentStatement.calc()
                        currentOperator.value = ""
                    }
                    '+', '-', '*' -> {
                        if (currentStatement.peek() == null || currentStatement.peek() is Operator)
                            pushCurrentInput()
                        lastCalced.value = currentStatement.calc()
                        currentOperator.value = input.toString()
                        currentStatement.pushItem(Operator(input))
                    }
                }
            fullStatement.value = grandParent.toString()
        } catch (ex: Exception) {
            Toast.makeText(getApplication(), ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun pushCurrentInput() {
        currentStatement.pushItem(RawValue(currentInput.toString().toDouble()))
        currentInput.clear()
        currentValue.value = ""
    }

    private fun calcAndCollapse() {
        currentInput.clear().append(currentStatement.calc())
        currentValue.value = currentInput.toString()
        grandParent = StackStatement()
        currentStatement = grandParent
    }
}