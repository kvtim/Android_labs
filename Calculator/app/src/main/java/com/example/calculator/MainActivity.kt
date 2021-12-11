package com.example.calculator

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import net.objecthunter.exp4j.ExpressionBuilder
import org.mariuszgromada.math.mxparser.Expression
import java.lang.Exception
import java.util.ArrayDeque


class MainActivity : AppCompatActivity() {

    private lateinit var previousCal: TextView
    private lateinit var display:EditText
    private var list: MutableList<String> = mutableListOf()
    var isPortrait = true
    var equalsWasPreesed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        previousCal = findViewById(R.id.preCalView)
        display = findViewById(R.id.displayText)
        display.showSoftInputOnFocus = false
    }
    private fun updateText(strToAdd:String) {
        val oldStr = display.text.toString()
        val cursorPs = display.selectionStart
        val leftStr = oldStr.substring(0, cursorPs)
        val rightStr = oldStr.substring(cursorPs)
        display.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr))
        display.setSelection(cursorPs + strToAdd.length)
    }


    fun showAlertDialogue(message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error!")
        builder.setMessage(message)
        builder.show()
    }

    fun isNumber(s: String?): Boolean {
        return  if (s.isNullOrEmpty()) false
        else s.all { Character.isDigit(it) }
    }

    fun getAmountOfLastDigits(): Int {
        var counter = 0

        var i = list.size - 1
        while (i != 0 && isNumber(list.get(i))) {
            ++counter
            --i
        }

        return counter
    }

    fun clearAllDisplays(){
        display.setText("")
        previousCal.text=""
    }

    fun digitsClickLogic(digit: String) {

        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }
        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if(lastElement == ")"|| lastElement=="e" || lastElement == "pi"
                || lastElement=="^(2)"){
                list.add("*")
                list.add(digit)
                updateText("*")
                updateText(digit)
            }
            else if (lastElement != "0" && lastElement != "^(2)" &&
                lastElement != "e" && lastElement != "pi") {
                if (getAmountOfLastDigits() < 11) {
                    list.add(digit)
                    updateText(digit)
                }
            } else if (lastElement == "0") {
                // here we need to check if there is a digit before lastElement
                if (list.size>1) {
                    var penultimateElement = list.get(list.size - 2)
                    if (isNumber(penultimateElement) || penultimateElement==".") {
                        if (getAmountOfLastDigits() < 11) {
                            list.add(digit)
                            updateText(digit)
                        }
                    }
                }
            }
        }
        else {
            list.add(digit)
            updateText(digit)
        }
    }

    fun PlusDivMulClickLogic(operation: String) {
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if (isNumber(lastElement) || lastElement==")" || lastElement=="0" ||
                lastElement=="e" || lastElement=="pi" || lastElement=="^(2)") {
                list.add(operation)
                updateText(operation)
            }
        }
    }

    fun TrigOperationsLogic(trigOp: String) {
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }

        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if(isNumber(lastElement) || lastElement==")"
                || lastElement=="e" || lastElement == "pi"
                || lastElement=="^(2)") {
                list.add("*")
                updateText("*")
                list.add(trigOp)
                updateText(trigOp)
            }
            else if(!(isNumber(lastElement) || lastElement=="." ||
                        lastElement=="0" || lastElement=="e" || lastElement=="pi" ||
                        lastElement=="^(2)")) {
                list.add(trigOp)
                updateText(trigOp)
            }
        }
        else {
            list.add(trigOp)
            updateText(trigOp)
        }
    }

    fun eAndPiLogic(logOperand: String) {
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if(isNumber(lastElement) || lastElement==")"
                || lastElement=="e" || lastElement == "pi"
                || lastElement=="^(2)")
            {
                list.add("*")
                updateText("*")
                list.add(logOperand)
                updateText(logOperand)
            }
            else if (!(isNumber(lastElement) || lastElement=="." ||
                        lastElement=="0" || lastElement=="^(2)" || lastElement=="e" ||
                        lastElement=="pi")) {
                list.add(logOperand)
                updateText(logOperand)
            }
        }
        else {
            list.add(logOperand)
            updateText(logOperand)
        }
    }

    fun powersLogic(powerStr: String){
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if (isNumber(lastElement) || lastElement=="0" || lastElement=="e" || lastElement=="pi"){
                list.add(powerStr)
                updateText(powerStr)
            }
        }
    }

    fun zeroBtn(v: View) {
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        if (list.isNotEmpty()) {
            // here we get last element that we added to list
            var lastElement = list.last()

            if (lastElement != null)
            {
                // that means something is in list. Now we need to check what is it
                if (lastElement != ")" && lastElement != "e" && lastElement != "pi" &&
                    lastElement != "^(2)") {
                    if (lastElement != "0" && getAmountOfLastDigits()  < 11) {
                        list.add("0")
                        updateText("0")
                    }
                    else {
                        // here we need to check if there is a digit before lastElement
                        if (list.size>1) {
                            var penultimateElement = list.get(list.size - 2)
                            if (isNumber(penultimateElement) && getAmountOfLastDigits() < 11) {
                                list.add("0")
                                updateText("0")
                            }
                        }
                    }
                }
            }
        }
        else {
                list.add("0")
                updateText("0")
        }
    }

    fun oneBtn(v: View) {
        digitsClickLogic("1")
    }

    fun twoBtn(v: View){
        digitsClickLogic("2")
    }

    fun threeBtn(v: View){
        digitsClickLogic("3")
    }

    fun fourBtn(v: View){
        digitsClickLogic("4")
    }

    fun fiveBtn(v: View){
        digitsClickLogic("5")
    }

    fun sixBtn(v: View){
        digitsClickLogic("6")
    }

    fun sevenBtn(v: View){
        digitsClickLogic("7")
    }

    fun eightBtn(v: View){
        digitsClickLogic("8")
    }

    fun nineBtn(v: View){
        digitsClickLogic("9")
    }

    fun parenthesesOpenBtn(v: View){
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if(isNumber(lastElement)|| lastElement==")" ||
                lastElement=="0" || lastElement=="e" || lastElement=="pi" ||
                lastElement=="^(2)") {
                list.add("*")
                list.add("(")
                updateText("*")
                updateText("(")
            }
            else if (lastElement=="*" || lastElement=="/" ||
                lastElement=="+" || lastElement=="-")
            {
                list.add("(")
                updateText("(")
            }
        }
        else {
            list.add("(")
            updateText("(")
        }
    }

    fun isCloseParenthesesLess() : Boolean{
        var openParenthesesAmount = 0
        var closeParenthesesAmount = 0

        for (element in list){
            when (element){
                "(", "sin(", "cos(", "tan(", "asin(", "acos(", "atan(",
                "^(", "log(", "log10(", "sqrt(", "abs(" -> openParenthesesAmount += 1

                ")" -> closeParenthesesAmount += 1
            }
        }

        return openParenthesesAmount > closeParenthesesAmount
    }

    fun parenthesesCloseBtn(v: View){
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if (isNumber(lastElement) || lastElement==")" || lastElement=="0" ||
                lastElement=="e" || lastElement=="pi" || lastElement=="^(2)") {
                if (isCloseParenthesesLess()){
                    list.add(")")
                    updateText(")")
                }
            }
        }
    }

    fun divideBtn(v: View){
        PlusDivMulClickLogic("/")
    }

    fun multiplyBtn(v: View){
        PlusDivMulClickLogic("*")
    }

    fun addBtn(v: View){
        PlusDivMulClickLogic("+")
    }

    fun subtractBtn(v: View) {
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if (!(lastElement=="." || lastElement=="*" || lastElement=="/" ||
                        lastElement=="+" || lastElement=="-")) {
                list.add("-")
                updateText("-")
            }
        }
        else {
            list.add("-")
            updateText("-")
        }
    }


    fun hasDotInLastNumberOfList() : Boolean {
        if (list.size>1) {
            var i = list.size-2
            while (i != 0 && isNumber(list[i]))
                i -= 1

            if (i == 0) // if i == 0 there can't be a dot in first place. even if it is symbol
                return false
            else if(i != 0 && list[i]!=".")
                return false
            else
                return true
        }
        else
            return false
    }


    fun decimalBtn(v: View){
        if (equalsWasPreesed){
            clearAllDisplays()
            equalsWasPreesed=false
        }


        // here we get last element that we added to list
        if (list.isNotEmpty()) {
            var lastElement = list.last()

            if (isNumber(lastElement)){
                // here we need to check if there is a '.' in previous number
                if (!hasDotInLastNumberOfList()){
                    list.add(".")
                    updateText(".")
                }
            }
        }
    }

    fun clearBtn(v: View){
        list.clear()
        clearAllDisplays()
    }

    fun equalsBtn(v: View){
        if (!isCloseParenthesesLess()) {
            try {
                var expression = display.text.toString()
                    .replace('×', '*')
                    .replace('÷', '/')
                previousCal.text = expression

                val ex = ExpressionBuilder(expression).build()
                val result = ex.evaluate()

                val longRes = result.toLong()
                if (result == longRes.toDouble())
                    display.setText(longRes.toString())
                else
                    display.setText(result.toString())
                list.clear()
                equalsWasPreesed = true
            } catch (e: Exception) {
                display.setText("Not a number!")
                Log.d("Exception", "msg: ${e.message}")
                list.clear()
                equalsWasPreesed = true
            }
        }
        else {
            showAlertDialogue("Not all brackets are closed!")
        }
    }



    fun trigsinBtn(v: View){
        TrigOperationsLogic("sin(")
    }

    fun trigcosBtn(v: View){
        TrigOperationsLogic("cos(")
    }

    fun trigtanBtn(v: View){
        TrigOperationsLogic("tan(")
    }

    fun trigarcsinBtn(v: View){
        TrigOperationsLogic("asin(")
    }

    fun trigarccosBtn(v: View){
        TrigOperationsLogic("acos(")
    }

    fun trigarctanBtn(v: View){
        TrigOperationsLogic("atan(")
    }

    fun logBtn(v: View){
        TrigOperationsLogic("log(")
    }

    fun naturalLogBtn(v: View){
        TrigOperationsLogic("log10(")
    }

    fun absBtn(v: View){
        TrigOperationsLogic("abs(")
    }

    fun piBtn(v: View){
        eAndPiLogic("pi")
    }

    fun eBtn(v: View){
        eAndPiLogic("e")
    }

    fun xSquredBtn(v: View){
        powersLogic("^(2)")
    }

    fun sqrtBtn(v: View){
        TrigOperationsLogic("sqrt(")
    }

    fun xPowerYbtn(v: View){
        powersLogic("^(")
    }

    fun backSpaceBtn(v: View) {
        if (list.isNotEmpty()) {
            // deleting last element in list
            list.removeAt(list.size - 1)

            // than we clear display
            display.setText("")

            var strToAdd: String = ""

            // and display new from list
            for (elem in list) {
                strToAdd += elem
            }

            display.setText(strToAdd)
            display.setSelection(display.length())
        }
    }
}