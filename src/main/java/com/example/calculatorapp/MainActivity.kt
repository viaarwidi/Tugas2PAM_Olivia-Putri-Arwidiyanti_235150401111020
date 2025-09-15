package com.example.calculatorapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.math.round
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var tvDisplay: TextView

    private var currentInput: String = ""    // input yang sedang dibentuk (angka ke-2)
    private var operand1: String = ""        // angka pertama
    private var operator: String = ""        // "+", "-", "×", "÷"
    private var justCalculated = false      // flag supaya input baru menggantikan hasil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        // number buttons
        val btnIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
            R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        for (id in btnIds) {
            findViewById<Button>(id).setOnClickListener { v ->
                onNumberPressed((v as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { onDotPressed() }

        // operators
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperatorPressed("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperatorPressed("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorPressed("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorPressed("÷") }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsPressed() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { backspace() }

        updateDisplay("0")
    }

    private fun onNumberPressed(num: String) {
        if (justCalculated) {
            // mulai input baru
            currentInput = ""
            operand1 = ""
            operator = ""
            justCalculated = false
        }
        // blok leading zero
        if (currentInput == "0") currentInput = num else currentInput += num
        updateDisplay(currentInput)
    }

    private fun onDotPressed() {
        if (justCalculated) {
            currentInput = "0"
            operand1 = ""
            operator = ""
            justCalculated = false
        }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0."
            else currentInput += "."
            updateDisplay(currentInput)
        }
    }

    private fun onOperatorPressed(op: String) {
        if (currentInput.isEmpty() && operand1.isNotEmpty()) {
            // ganti operator jika user menekan operator beruntun
            operator = op
            return
        }
        if (operand1.isEmpty()) {
            operand1 = if (currentInput.isEmpty()) "0" else currentInput
            currentInput = ""
            operator = op
        } else {
            // jika sudah ada operand1 dan operator, lakukan perhitungan dulu
            if (currentInput.isNotEmpty()) {
                val result = calculate(operand1, operator, currentInput)
                operand1 = result
                currentInput = ""
                operator = op
                updateDisplay(formatResult(result))
            } else {
                operator = op
            }
        }
    }

    private fun onEqualsPressed() {
        if (operand1.isEmpty() || operator.isEmpty() || currentInput.isEmpty()) return
        val result = calculate(operand1, operator, currentInput)
        updateDisplay(formatResult(result))
        // siapkan untuk input baru; biarkan hasil sebagai operand1 agar bisa operasi lanjutan
        operand1 = result
        currentInput = ""
        operator = ""
        justCalculated = true
    }

    private fun calculate(a: String, op: String, b: String): String {
        val x = a.toDoubleOrNull() ?: 0.0
        val y = b.toDoubleOrNull() ?: 0.0
        val res = when (op) {
            "+" -> x + y
            "-" -> x - y
            "×" -> x * y
            "÷" -> {
                if (y == 0.0) return "Error" // pembagian 0
                x / y
            }
            else -> 0.0
        }
        return res.toString()
    }

    private fun formatResult(value: String): String {
        if (value == "Error") return value
        val d = value.toDoubleOrNull() ?: return "0"
        // tampilkan tanpa .0 jika bilangan bulat
        return if (d % 1.0 == 0.0) {
            d.toLong().toString()
        } else {
            // batasi digit desimal (mis. maksimal 8) untuk tampilan rapi
            String.format("%.8f", d).trimEnd('0').trimEnd('.')
        }
    }

    private fun clearAll() {
        currentInput = ""
        operand1 = ""
        operator = ""
        justCalculated = false
        updateDisplay("0")
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty()) updateDisplay("0") else updateDisplay(currentInput)
        }
    }

    private fun updateDisplay(text: String) {
        tvDisplay.text = text
    }
}