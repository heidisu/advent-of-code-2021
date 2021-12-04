package days

import functions.readLines

private const val fileName = "day1.txt"
private val input = readLines(fileName)

private val testInput = """
    
""".trimIndent().lines()

fun main() {
    val sequence = input.map { it.toInt() }
    val task1 = sequence.windowed(2, 1).count { l -> l[1] - l[0] > 0 }
    println("Task1: $task1") // 1195
    val task2 = sequence.windowed(3, 1).map { it.sum() }.windowed(2, 1).count { l -> l[1] - l[0] > 0 }
    println("Task2: $task2")
}