package days

import common.Point
import common.readLines

private const val fileName = "day11.txt"
private val input = readLines(fileName)
private val testInput = """
    5483143223
    2745854711
    5264556173
    6141336146
    6357385478
    4167524645
    2176841721
    6882881134
    4846848554
    5283751526
""".trimIndent().lines()

fun parseInput(lines: List<String>): Array<Array<Int>> {
    return lines.map { l -> l.toList().map { it.toString().toInt() }.toTypedArray() }.toTypedArray()
}

fun printArray(arr: Array<Array<Int>>) {
    val width = arr.size
    val height = arr[0].size
    for (i in 0 until width) {
        for (j in 0 until height) {
            print(arr[i][j])
        }
        println()
    }
}

private fun getNeighbours(point: Point, width: Int, height: Int): List<Point> {
    return listOf(
        Point(0, 1),
        Point(0, -1),
        Point(1, 0),
        Point(-1, 0),
        Point(-1, -1),
        Point(1, -1),
        Point(-1, 1),
        Point(1, 1)
    )
        .map { Point(it.x + point.x, it.y + point.y) }
        .filter { it.x in 0 until width && it.y in 0 until height }
}

fun flash(arr: Array<Array<Int>>, width: Int, height: Int, flashed: Set<Point>): Set<Point> {
    val toBeFlashed = mutableSetOf<Point>()
    for (i in 0 until width) {
        for (j in 0 until height) {
            if (arr[i][j] > 9 && !flashed.contains(Point(i, j))) {
                toBeFlashed.add(Point(i, j))
            }
        }
    }
    return if (toBeFlashed.isEmpty()) {
        flashed
    } else {
        toBeFlashed.forEach { p ->
            getNeighbours(p, width, height).forEach { n -> arr[n.x][n.y]++ }
        }
        flash(arr, width, height, flashed.union(toBeFlashed))
    }
}

fun step(arr: Array<Array<Int>>): Int {
    val width = arr.size
    val height = arr[0].size
    for (i in 0 until width) {
        for (j in 0 until height) {
            arr[i][j]++
        }
    }
    val flashed = flash(arr, width, height, emptySet())
    flashed.forEach { arr[it.x][it.y] = 0 }
    return flashed.size
}

fun allFlash(arr: Array<Array<Int>>, count: Int): Int {
    val total = arr.size * arr[0].size
    return if (step(arr) == total) {
        count + 1
    } else {
        allFlash(arr, count + 1)
    }
}

fun main() {
    val arr = parseInput(input)
    val task1 = (0 until 100).fold(0) { acc, _ -> acc + step(arr) }
    println("Task1: $task1") // 1625

    val task2 = allFlash(parseInput(input), 0)
    println("Task2: $task2") // 244
}