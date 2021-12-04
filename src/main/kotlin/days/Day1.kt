package days

import functions.readLines

private const val fileName = "day1.txt"
private val lines = readLines(fileName)

fun main() {
    val input = lines.map { it.toInt() }
    val oppg1 = input.windowed(2, 1).count { l -> l[1] - l[0] > 0 }
    println(oppg1) // 1195
    val oppg2 = input.windowed(3, 1).map { it.sum() }.windowed(2, 1).count { l -> l[1] - l[0] > 0 }
    println(oppg2)
}