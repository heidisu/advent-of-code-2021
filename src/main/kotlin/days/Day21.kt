package days

import common.readLines

private const val fileName = "day21.txt"
private val input = readLines(fileName)
private val testInput = """
    Player 1 starting position: 4
    Player 2 starting position: 8
""".trimIndent().lines()

private fun parseInput(input: List<String>): Pair<Int, Int>{
    val startPos1 = Integer.parseInt(input[0].last().toString())
    val startPos2 = Integer.parseInt(input[1].last().toString())
    return startPos1 to startPos2
}

fun throwDice(diceVal: Int): Int {
    return if (diceVal == 100) {
        1
    } else {
        diceVal + 1
    }
}

fun playDeterministic(startPos1: Int, startPos2: Int): Pair<Int, Int>{
    val positions = Array(2){ 0 }
    val points = Array(2){ 0 }
    positions[0] = startPos1
    positions[1] = startPos2
    var diceVal = 100
    var numDiceThrows = 0
    var next = 0
    while(points[0] < 1000 && points[1] < 1000){
        val dice1 = throwDice(diceVal)
        val dice2 = throwDice(dice1)
        val dice3 = throwDice(dice2)
        diceVal = dice3
        numDiceThrows += 3
        var nextPos = (positions[next] + dice1 + dice2 + dice3) % 10
        if (nextPos == 0){ nextPos  =10 }
        points[next] += nextPos
        positions[next] = nextPos
        println("Player $next nextPos: $nextPos points: ${points[next]} dice: $diceVal")
        next = if(next == 0 ){1} else {0}

    }
    return numDiceThrows to (points.minOf { it })
}

data class Game(val pos1: Int, val pos2: Int, val points1: Int, val points2: Int, val next: Int)

fun playDirac(startPos1: Int, startPos2: Int): Pair<Int, Int>{
    val games = mutableListOf<Game>()
    val wins = Array(2){ 0L }
    games.add(Game(pos1 = startPos1, pos2 = startPos2, points1 = 0, points2 = 0, next = 0))


    return 0 to 0
}

fun main() {
    val startPos = parseInput(input)
    val deterministic = playDeterministic(startPos.first, startPos.second)
    val task1 = deterministic.first * deterministic.second
    println("Task1: $task1")
    val task2 = ""
    println("Task2: $task2")
}