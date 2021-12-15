package days

import common.readLines

private const val fileName = "day8.txt"
private val input = readLines(fileName)
private val testInput = """
    be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
    edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
    fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
    fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
    aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
    fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
    dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
    bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
    egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
    gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
""".trimIndent().lines()

private val testInput2 = """
    acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf
""".trimIndent().lines()

data class Digit(private val digitString: String) {
    val size = digitString.length
    private val digitSet = digitString.toSet()

    fun digit(): Int? {
        return when (size) {
            2 -> 1
            3 -> 7
            4 -> 4
            7 -> 8
            else -> null
        }
    }

    fun intersect(other: Digit): Set<Char> {
        return digitSet.intersect(other.digitSet)
    }

    fun containsAll(other: Digit): Boolean {
        return digitSet.containsAll(other.digitSet)
    }

    fun containsAll(chars: Set<Char>): Boolean {
        return digitSet.containsAll(chars)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Digit
        if (digitSet != other.digitSet) return false
        return true
    }

    override fun hashCode(): Int {
        return digitSet.hashCode()
    }
}

fun solveDigit(digit: Digit, digits: Array<Digit>): Int {
    return digits.indexOfFirst { it == digit }
}

//    000
//   1   2
//   1   2
//    333
//   4   5
//   4   5
//    666
fun solveSignals(numbers: List<Digit>): Array<Digit> {
    val digits = Array(10){ Digit("")}
    digits[1] = numbers.first { it.digit() == 1 }
    digits[4] = numbers.first{ it.digit() == 4}
    digits[7] = numbers.first { it.digit() == 7 }
    digits[8] = numbers.first { it.digit() == 8}

    val sizeSix = numbers.filter { it.size == 6 } // 0, 6, 9
    digits[6] = sizeSix.first{ !it.containsAll(digits[7])} // 0 og 9 contains 7

    val sizeFive = numbers.filter { it.size == 5 } // 2, 3, 5
    digits[5] = sizeFive.first { digits[6].containsAll(it) } // 6 contains 5, but not 2 and 3
    digits[2] = sizeFive.first { it.intersect(digits[5]).size == 3} // 5 intersect 2 = 3, 5 intersect 3 = 4
    digits[3] = sizeFive.first { it != digits[5] && it != digits[2]}
    val horizontalSegments = digits[5].intersect(digits[2])
    digits[9] = sizeSix.first { it != digits[6] && it.containsAll(horizontalSegments) }
    digits[0] = sizeSix.first { it != digits[9] && it != digits[6]}
    return digits
}


fun parseDigits(line: String): Pair<List<Digit>, List<Digit>> {
    val signalPatterns = line.split("|")[0].split(" ").filter { it.isNotBlank() }.map { Digit(it) }
    val digitOutput = line.split("|")[1].split(" ").filter { it.isNotBlank() }.map { Digit(it) }
    return signalPatterns to digitOutput
}

fun digitsToNumber(digits: List<Int>): Int{
    return digits.joinToString(separator = "") { it.toString() }.toInt(10)
}

fun main() {
    val task1 = input.map { parseDigits(it) }.sumOf { it.second.mapNotNull { d-> d.digit() }.count() }
    println("Task1: $task1") // 493
    val task2 =
        input.map { parseDigits(it) }
            .map {
                val solved = solveSignals(it.first)
                it.second.map { d -> solveDigit(d, solved) }
            }.sumOf { digitsToNumber(it) }
    println("Task2: $task2") // 1010460
}
