package days

import common.Point
import functions.readLines

private const val fileName = "day15.txt"
private val input = readLines(fileName)
private val testInput = """
    1163751742
    1381373672
    2136511328
    3694931569
    7463417111
    1319128137
    1359912421
    3125421639
    1293138521
    2311944581
""".trimIndent().lines()

fun linesToArray(lines: List<String>): Array<Array<Int>> {
    return lines.map { l -> l.toList().map { it.toString().toInt() }.toTypedArray() }.toTypedArray()
}

fun min(dist: Array<Array<Int>>, unvisited: Set<Point>): Point {
    return unvisited.map { it to dist[it.x][it.y] }.minByOrNull { it.second }!!.first
}

fun shortestPath(arr: Array<Array<Int>>): Int {
    val rows = arr.size
    val cols = arr[0].size
    val target = Point(rows - 1, cols - 1)
    val unvisited = (0 until rows).flatMap { i -> (0 until cols).map { j -> Point(i, j) } }.toMutableSet()
    val dist = Array(rows) { Array(cols) { Int.MAX_VALUE } }
    dist[0][0] = 0

    while (unvisited.isNotEmpty()) {
        val current = min(dist, unvisited)
        unvisited.remove(current)

        if (current == target) {
            break
        }

        val neighbours = getNeighbours(current, unvisited)
        for (n in neighbours) {
            val alt = dist[current.x][current.y] + arr[n.x][n.y]
            if (alt < dist[n.x][n.y]) {
                dist[n.x][n.y] = alt
            }
        }
    }
    return dist[rows - 1][cols - 1]
}

fun getNeighbours(current: Point, unvisited: MutableSet<Point>): List<Point> {
    val x = current.x
    val y = current.y
    val neighbours = listOf(Point(x, y + 1), Point(x, y - 1), Point(x + 1, y), Point(x - 1, y))
    return unvisited.filter { neighbours.contains(it) }
}

fun printArr(arr: Array<Array<Int>>) {
    val rows = arr.size
    val cols = arr[0].size
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            print("${arr[i][j]} ")
        }
        println()
    }
}

fun bigger(arr: Array<Array<Int>>): Array<Array<Int>> {
    val rows = arr.size
    val cols = arr[0].size
    val factor = 5
    val bigger = Array(rows * factor) { Array(cols * factor ) { -1 } }
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            bigger[i][j] = arr[i][j]
        }
    }

    for (i in rows until factor * rows) {
        for (j in 0 until cols) {
            bigger[i][j] = if (bigger[i - rows][j] == 9) {
                1
            } else {
                bigger[i - rows][j] + 1
            }
        }
    }

    for (i in 0 until rows * factor) {
        for (j in cols until factor * cols) {
            bigger[i][j] = if (bigger[i][j - cols] == 9) {
                1
            } else {
                bigger[i][j - cols] + 1
            }
        }
    }

    return bigger
}

fun main() {
    val arr = linesToArray(input)
    val task1 = shortestPath(arr)
    println("Task1: $task1") // 386
    val bigger = bigger(arr)
    val task2 = shortestPath(bigger)
    println("Task2: $task2") // 2806
}