package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext.DECIMAL64
import java.math.RoundingMode

fun main() {
    while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        var input = readln()
        if (input == "/exit") return
        val (sourceBase, targetBase) = input.split(" ").map { it.toInt() }

        while (true) {
            println("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
            input = readln()
            if (input == "/back") break
            val result = Converter(sourceBase, targetBase, input)
            println("Converter result: $result\n")
        }
    }
}

class Converter(sourceBase: Int, private val targetBase: Int, source: String) {

    private var actualIntegerBase = sourceBase
    private var actualFractionalBase = sourceBase
    private var number = Number(source)
    private val minPrecision: Int = if (number.fractional.length <= 5) number.fractional.length else 5

    init {
        covertAny()
    }

    private fun covertAny() {
        number = Number(convertAnyInteger(), convertAnyFractional())
    }

    private fun convertAnyInteger(): String {
        return when {
            actualIntegerBase == 10 -> convertIntegerFromDecimal()
            targetBase == 10 -> convertIntegerToDecimal()
            else -> {
                number.integer = convertIntegerToDecimal()
                actualIntegerBase = 10
                convertAnyInteger()
            }
        }
    }

    private fun convertIntegerFromDecimal(): String {
        var temp = number.integer.toBigInteger()
        val base = targetBase.toBigInteger()
        var output = ""
        do {
            val r = (temp.rem(base)).toString(base.toInt())
            temp = temp.div(base)
            output = "$r$output"
        } while ((temp != BigInteger.ZERO))
        return output
    }

    private fun convertIntegerToDecimal(): String {
        var output = BigInteger.ZERO
        val integer = number.integer
        for (i in integer.indices) {
            output = output.plus(
                (integer[i].digitToBigInteger(actualIntegerBase) * actualIntegerBase.toBigInteger()
                    .pow(integer.lastIndex - i))
            )
        }
        return output.toString()
    }

    private fun convertAnyFractional(): String {
        return when {
            number.fractional == "" -> ""
            actualFractionalBase == 10 -> convertFractionalFromDecimal()
            targetBase == 10 -> convertFractionalToDecimal()
            else -> {
                number.fractional = convertFractionalToDecimal()
                actualFractionalBase = 10
                convertAnyFractional()
            }
        }
    }

    private fun convertFractionalFromDecimal(): String {
        var temp = number.fractional.addIntegerZero()
        val base = targetBase.toBigDecimal()
        var output = ""
        do {
            temp = temp.toBigDecimal().multiply(base).toString()
            val (int, fractional) = temp.split(".")
            temp = fractional.addIntegerZero()
            output += int.toBigInteger().toString(targetBase)
        } while (!Regex("0.0*").matches(temp) && output.length < 5)
        return output
    }


    private fun convertFractionalToDecimal(): String {
        var output = BigDecimal.ZERO
        val fractional = number.fractional
        for (i in fractional.indices) {
            output = output.plus(
                (fractional[i].digitToBigDecimal(actualFractionalBase) * actualFractionalBase.toBigDecimal()
                    .pow(-i - 1, DECIMAL64))
            )
        }
        return output.setScale(5, RoundingMode.HALF_UP).toString().removeIntegerZero()
    }

    override fun toString(): String {
        var output = number.toString()
        repeat(minPrecision - number.fractional.length) { output += "0" }
        return output
    }

}


class Number {
    var integer: String
    var fractional: String
    private val hasFractional: Boolean

    constructor(source: String) {
        hasFractional = source.contains(".")
        if (hasFractional) {
            val number = source.split(".")
            integer = number.first()
            fractional = number.last()
        } else {
            integer = source
            fractional = ""
        }
    }


    constructor(integer: String, fractional: String) {
        this.hasFractional = fractional.isNotEmpty()
        this.integer = integer
        this.fractional = if (hasFractional) {
            fractional
        } else ""
    }


    override fun toString(): String = if (hasFractional) "$integer.$fractional" else integer

}

private fun String.addIntegerZero(): String = "0.$this"
private fun String.removeIntegerZero(): String = this.replace("0.", "")

private fun Char.digitToBigInteger(base: Int): BigInteger = this.digitToInt(base).toBigInteger()
private fun Char.digitToBigDecimal(base: Int): BigDecimal = this.digitToBigInteger(base).toBigDecimal()