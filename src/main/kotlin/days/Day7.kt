package days

import functions.readLines
import kotlin.math.absoluteValue

private const val fileName = "day7.txt"
private val input = readLines(fileName)
private val testInput = """
    16,1,2,0,4,2,7,1,2,14
""".trimIndent().lines()

fun main() {
    val numbers = input.first().split(",").map { it.toInt() }
    val min = numbers.minOf { it }
    val max = numbers.maxOf { it }
    val task1 = (min .. max).map{i -> numbers.sumOf { n -> (n - i).absoluteValue } }.minOf { it }
    println("Task1: $task1") //331067
    val task2 = (min .. max).map{ i -> numbers.sumOf { n -> (1..(n - i).absoluteValue).sum() } }.minOf { it }
    println("Task2: $task2") //92881128
}