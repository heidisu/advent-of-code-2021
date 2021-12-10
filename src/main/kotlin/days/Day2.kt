package days

import functions.readLines
import java.lang.Exception

private const val fileName = "day2.txt"
private val input = readLines(fileName)
private val testInput = """
    forward 5
    down 5
    forward 8
    up 3
    down 8
    forward 2
""".trimIndent().lines()

private fun parse(line: String): Pair<String, Int> {
     val words = line.split(' ')
    return Pair(words[0], words[1].toInt())
}

data class Position(val horizontal: Int, val depth: Int)

fun toPosition(pair: Pair<String, Int>): Position {
    return when(pair.first){
        "forward" -> Position(pair.second, 0)
        "up" -> Position(0, -pair.second)
        "down" -> Position(0, pair.second)
        else -> throw Exception("oh no")
    }
}

fun main() {
    val task1 =
        input
            .map { parse(it) }
            .map { toPosition(it) }
            .fold(Pair(0, 0)){ acc, pos -> Pair(acc.first + pos.horizontal, acc.second + pos.depth)}
            .let { it.first * it.second }
    println("Task1: $task1")


    val task2 =
        input
            .map { parse(it) }
            .fold(Triple(0, 0, 0)) { acc, pos ->
                when (pos.first) {
                    "forward" -> acc.copy(first = acc.first + pos.second, second = acc.second + (acc.third * pos.second))
                    "up" -> acc.copy(third = acc.third - pos.second)
                    "down" -> acc.copy(third = acc.third + pos.second)
                    else -> acc
                }
            }
            .let { it.first * it.second }
    println("Task2: $task2")
}