package days

import functions.readLines
import kotlin.math.absoluteValue

private const val fileName = "day5.txt"
private val input = readLines(fileName)
private val testInput = """
    0,9 -> 5,9
    8,0 -> 0,8
    9,4 -> 3,4
    2,2 -> 2,1
    7,0 -> 7,4
    6,4 -> 2,0
    0,9 -> 2,9
    3,4 -> 1,4
    0,0 -> 8,8
    5,5 -> 8,2
""".trimIndent().lines()

data class Point(val x: Int, val y: Int)
data class LineSegment(val startPoint: Point, val endPoint: Point) {
    fun isHorizontalOrVertical(): Boolean {
        return startPoint.x == endPoint.x || startPoint.y == endPoint.y
    }

    fun pointsOnSegment(): List<Point> {
        return if (isHorizontalOrVertical()) {
            pointsOnHorizontalOrVertical()
        } else {
            val diff = (endPoint.x - startPoint.x) to (endPoint.y - startPoint.y)
            val factor = diff.first.absoluteValue
            val step = diff.first / factor to diff.second / factor
            (0..factor)
                .map { f -> Point(startPoint.x + step.first * f, startPoint.y + step.second * f) }
        }
    }

    private fun pointsOnHorizontalOrVertical(): List<Point> {
        val (lowest, highest) = if (startPoint.x >= endPoint.x && startPoint.y >= endPoint.y) {
            endPoint to startPoint
        } else {
            startPoint to endPoint
        }
        val xRange = 0..(highest.x - lowest.x)
        val yRange = 0..(highest.y - lowest.y)
        return xRange.flatMap { x -> yRange.map { y -> Point(x, y) } }
            .map { (x, y) -> Point(lowest.x + x, lowest.y + y) }
    }
}

private fun parseLine(line: String): LineSegment {
    val numbers = line.split(",", "->").filter { it.isNotBlank() }
    return LineSegment(
        Point(numbers[0].trim().toInt(), numbers[1].trim().toInt()),
        Point(numbers[2].trim().toInt(), numbers[3].trim().toInt())
    )
}

fun main() {
    val task1 =
        input
            .map { parseLine(it) }
            .filter { it.isHorizontalOrVertical() }
            .flatMap { it.pointsOnSegment() }
            .groupingBy { it }.eachCount()
            .entries.count { it.value > 1 }
    println("Task1: $task1") // 8060

    val task2 =
        input
            .map { parseLine(it) }
            .flatMap { it.pointsOnSegment() }
            .groupingBy { it }.eachCount()
            .entries.count { it.value > 1 }
    println("Task2: $task2") // 21577
}