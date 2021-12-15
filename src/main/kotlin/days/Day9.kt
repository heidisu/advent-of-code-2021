package days

import common.Point
import common.readLines

private const val fileName = "day9.txt"
private val input = readLines(fileName)
private val testInput = """
    2199943210
    3987894921
    9856789892
    8767896789
    9899965678
""".trimIndent().lines()

fun toArray(testInput: List<String>): Array<Array<Int>> {
    return testInput.map { l -> l.toList().map { it.toString().toInt() }.toTypedArray() }.toTypedArray()
}

private fun getNeighbours(point: Point, width: Int, height: Int): List<Point> {
    return listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
        .map { (x, y) -> Point(x + point.x, y + point.y) }
        .filter { (x, y) -> x in 0 until width && y in 0 until height }
}

fun isMinimum(point: Point, width: Int, height: Int, arr: Array<Array<Int>>): Boolean {
    val neighbourValues =
        getNeighbours(point, width, height)
            .map {  arr[it.x][it.y]}
    return neighbourValues.all { it > arr[point.x][point.y] }
}

fun findMinima(arr: Array<Array<Int>>): List<Pair<Point, Int>> {
    val width = arr.size
    val height = arr[0].size
    val minima = mutableListOf<Pair<Point, Int>>()
    for (i in 0 until width) {
        for (j in 0 until height) {
            val point = Point(i, j)
            if (isMinimum(point, width, height, arr)) {
                minima.add(point to arr[i][j])
            }
        }
    }
    return minima
}

fun findBasin(point: Point, arr: Array<Array<Int>>, width: Int, height: Int, acc: Set<Point>): Set<Point> {
    val neighbours =
        getNeighbours(point, width, height)
            .filter { (x, y) -> arr[x][y] > arr[point.x][point.y] && arr[x][y] != 9 }
    return neighbours.flatMap{ findBasin(it, arr, width, height, acc) }.toSet() + setOf(point)
}

fun main() {
    val arr = toArray(input)
    val minima = findMinima(arr)
    val task1 = minima.sumOf { it.second + 1 }
    println("Task1: $task1") // 631

    val width = arr.size
    val height = arr[0].size
    val task2 =
        minima
            .map {  findBasin(it.first, arr, width, height, emptySet()).count()}
            .sortedDescending()
            .take(3)
            .reduce { acc, i -> acc * i }
    println("Task2: $task2") // 821560
}

