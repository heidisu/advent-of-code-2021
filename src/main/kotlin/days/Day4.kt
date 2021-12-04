package days

import functions.readLines

private const val fileName = "day4.txt"
private val input = readLines(fileName)
private val testInput = """
7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
""".trimIndent().lines()

class Board(private val board: Array<Array<Int?>>, private val size: Int) {
    private fun calculateState(i: Int, j: Int, number: Int): State {
        val horizontal = board[i].filterNotNull().isEmpty()
        val vertical = (0 until size).mapNotNull { board[it][j] }.isEmpty()
        return if (horizontal || vertical) {
            val sum = board.sumOf { l -> l.filterNotNull().sum() }
            State.Bingo(sum * number)
        } else State.Playing
    }

    fun mark(number: Int): State {
        for (i in 0 until size)
            for (j in 0 until size) {
                if (board[i][j] == number) {
                    board[i][j] = null
                    return calculateState(i, j, number)
                }
            }
        return State.Playing
    }
}

sealed class State {
    data class Bingo(val score: Int) : State()
    object Playing : State()
}

private fun parseBoards(lines: List<String>): List<Board> {
    val boardSize = 5
    return lines
        .drop(2)
        .windowed(6, 6, partialWindows = true)
        .map { l ->
            l.take(boardSize)
                .map { r -> r.split(" ").filter { it.isNotBlank() }.map { it.trim().toInt() }.toTypedArray<Int?>() }
                .toTypedArray()
        }
        .map { Board(it, boardSize) }
}

fun main() {
    val numbers = input.first().split(",").map { it.toInt() }
    val boards = parseBoards(input)
    for (num in numbers) {
        val result = boards.map { it.mark(num) }.firstOrNull { it is State.Bingo } ?: State.Playing
        if (result is State.Bingo) {
            println("Task1: ${result.score} $num")
            break;
        }
    }

    var activeBoards = parseBoards(input)
    var singleBoard: Board? = null
    for (num in numbers) {
        if (singleBoard == null) {
            val winningboards =
                activeBoards.map { it to it.mark(num) }.filter { it.second is State.Bingo }.map { it.first }
            activeBoards = activeBoards.minus(winningboards)

            if (activeBoards.size == 1) {
                singleBoard = activeBoards.first()
            }
        } else {
            val result = singleBoard.mark(num)
            if (result is State.Bingo) {
                println("Task2: ${result.score} $num")
                break;
            }
        }
    }
}