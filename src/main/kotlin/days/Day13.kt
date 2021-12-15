package days

import common.Point
import common.readLines
import java.lang.IllegalArgumentException

private const val fileName = "day13.txt"
private val input = readLines(fileName)
private val testInput = """
    6,10
    0,14
    9,10
    0,3
    10,4
    4,11
    6,0
    6,12
    4,1
    0,13
    10,12
    3,4
    3,0
    8,4
    1,10
    2,14
    8,10
    9,0

    fold along y=7
    fold along x=5
""".trimIndent().lines()

private sealed class Line(open val x: Int?, open val y: Int?) {
    data class Horizontal(override val y: Int) : Line(null, y)
    data class Vertical(override val x: Int) : Line(x, null)
}

private fun parseLines(input: List<String>): Pair<Array<Array<Int>>, List<Line>> {
    val points =
        input
            .takeWhile { it.isNotEmpty() }
            .map {
                val splits = it.split(",")
                Point(splits[0].toInt(), splits[1].toInt())
            }
    val width = points.maxOf { it.x } + 1
    val height = points.maxOf { it.y } + 1
    val array: Array<Array<Int>> = Array(width) { Array(height) { 0 } }
    for (point in points) {
        array[point.x][point.y] = 1
    }
    val lines = input.drop(points.size + 1).map {
        val splits = it.split(" ")
        val line = splits.last().split("=")
        when (line[0]) {
            "x" -> Line.Vertical(x = line[1].toInt())
            "y" -> Line.Horizontal(y = line[1].toInt())
            else -> throw IllegalArgumentException("oh no")
        }
    }
    return array to lines
}

private fun print(arr: Array<Array<Int>>) {
    val height = arr[0].size
    val width = arr.size
    for (j in 0 until height) {
        for (i in 0 until width) {
            if (arr[i][j] == 0) {
                print(".")
            } else {
                print("#")
            }
        }
        println()
    }
    println()
}

private fun foldVertical(arr: Array<Array<Int>>, fold: Int): Array<Array<Int>> {
    val height = arr[0].size
    val width = arr.size
    val newArr: Array<Array<Int>> = Array(fold) { Array(height) { 0 } }
    for (j in 0 until height) {
        for (i in 0 until fold) {
            newArr[i][j] = arr[i][j]
        }
    }
    for (j in 0 until height) {
        for (i in fold + 1 until width) {
            if (arr[i][j] == 1)
                newArr[2 * fold - i][j] = 1
        }
    }
    return newArr
}

private fun foldHorizontal(arr: Array<Array<Int>>, fold: Int): Array<Array<Int>> {
    val height = arr[0].size
    val width = arr.size
    val newArr: Array<Array<Int>> = Array(width) { Array(fold) { 0 } }
    for (i in 0 until width) {
        for (j in 0 until fold) {
            newArr[i][j] = arr[i][j]
        }
    }
    for (i in 0 until width) {
        for (j in fold + 1 until height) {
            if (arr[i][j] == 1) {
                newArr[i][2 * fold - j] = 1
            }
        }
    }
    return newArr
}


private fun fold(arr: Array<Array<Int>>, line: Line): Array<Array<Int>> {
    return when (line) {
        is Line.Vertical -> foldVertical(arr, line.x)
        is Line.Horizontal -> foldHorizontal(arr, line.y)
    }
}

fun main() {
    val parsed = parseLines(input)
    val firstFold = fold(parsed.first, parsed.second.first())
    val task1 = firstFold.sumOf { a -> a.count { it == 1 } }
    println("Task1: $task1") //737
    val task2 = parsed.second.fold(parsed.first) { acc, l -> fold(acc, l) }
    print(task2) // Task 2: ZUJUAFHP
}