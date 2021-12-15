package days

import common.readLines

private const val fileName = "day3.txt"
private val input = readLines(fileName)
private val testInput =
    """
00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
""".trimIndent().lines()

fun gammaEpsilon(input: List<String>): Pair<String, String>{
    val size = input.first().length
    val rows = input.size
    return (0 until size)
        .map { idx -> input.map { it[idx] } }
        .fold(StringBuilder() to StringBuilder()){
                acc, lst ->
            if(lst.count { it == '0' } > rows / 2){
                acc.first.append('0') to acc.second.append('1')
            }
            else {
                acc.first.append('1') to acc.second.append('0')
            }
        }
        .let { it.first.toString() to it.second.toString() }
}

fun whatNumber(input: List<String>, idx: Int, comparator: (Int, Int) -> Boolean): Char {
    val map = input
        .map { it[idx] }
        .groupingBy { it }.eachCount()
    val ones = map['1']!!
    val zeroes = map['0']!!
    return if (comparator(ones, zeroes) ) {
        '1'
    } else {
        '0'
    }
}

fun main() {
    val (gamma, epsilon) = gammaEpsilon(input)
    val task1 = Integer.parseInt(gamma, 2) * Integer.parseInt(epsilon, 2) // 2595824
    println("Task1: $task1") // 2595824

    var oxygenLines = input
    var co2Scrubber = input
    for (idx in 0 until input.first().length) {
        if (oxygenLines.size > 1) {
            oxygenLines =
                oxygenLines.filter { it[idx] == whatNumber(oxygenLines, idx) { ones, zeroes -> ones >= zeroes } }
        }
        if (co2Scrubber.size > 1) {
            co2Scrubber =
                co2Scrubber.filter { it[idx] == whatNumber(co2Scrubber, idx) { ones, zeroes -> zeroes > ones } }
        }
    }

    val task2 = Integer.parseInt(oxygenLines.first(), 2) * Integer.parseInt(co2Scrubber.first(), 2)
    println("Task2: $task2") // 2135254
}