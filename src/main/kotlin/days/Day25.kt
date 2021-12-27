package days

import common.Point
import common.print
import common.readLines

private const val fileName = "day25.txt"
private val input = readLines(fileName)
private val testInput = """
    v...>>.vv>
    .vv>>.vv..
    >>.>v>...v
    >>v>>.>.v.
    v>v.vv.v..
    >.>>..v...
    .vv..>.>v.
    v.v..>>v.v
    ....v..v.>
""".trimIndent().lines()

private fun parseInput(lines: List<String>): Array<Array<Char>> {
    /**val rows = lines.size
    val cols = lines.first().length
    val grid = Array(rows){ Array(cols) {'.'} }
    for (i in 0 until rows){
        for(j in 0 until cols){
            grid[i][j] = lines[i][j]
        }
    }**/
    return lines.map { it.toCharArray().toTypedArray() }.toTypedArray()
}

private fun moveHerds(grid: Array<Array<Char>>, candidates: List<Pair<Point, Point>>){
    for ((from, to) in candidates){
        grid[to.x][to.y] = grid[from.x][from.y]
        grid[from.x][from.y] = '.'
    }
}

private fun step(grid: Array<Array<Char>>, counter: Int): Int {
    val eastHerdCandidates = mutableListOf<Pair<Point, Point>>()
    val southHerdCandidates = mutableListOf<Pair<Point, Point>>()
    val rows = grid.size
    val cols = grid.first().size
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            val nextCol = if (j == cols - 1) 0 else j + 1
            if (grid[i][j] == '>' && grid[i][nextCol] == '.') {
                eastHerdCandidates.add(Pair(Point(i, j), Point(i, nextCol)))
            }
        }
    }
    moveHerds(grid, eastHerdCandidates)
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            val nextRow = if (i == rows - 1) 0 else i + 1
            if (grid[i][j] == 'v' && grid[nextRow][j] == '.') {
                southHerdCandidates.add(Pair(Point(i, j), Point(nextRow, j)))
            }
        }
    }
    moveHerds(grid, southHerdCandidates)
    if(eastHerdCandidates.isEmpty() && southHerdCandidates.isEmpty()){
        return counter + 1
    } else {
        return step(grid, counter + 1)
    }
}

fun main() {
    val grid = parseInput(input)
    val task1 = step(grid, 0)
    println("Task1: $task1") // 504
    val task2 = ""
    println("Task2: $task2")
}