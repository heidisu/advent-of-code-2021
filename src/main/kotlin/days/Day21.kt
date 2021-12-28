package days

import common.readLines
import java.lang.Long.max

private const val fileName = "day21.txt"
private val input = readLines(fileName)
private val testInput = """
    Player 1 starting position: 4
    Player 2 starting position: 8
""".trimIndent().lines()

private fun parseInput(input: List<String>): Pair<Int, Int> {
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

fun playDeterministic(game: Game, diceVal: Int, numDiceThrows: Int): Pair<Int, Game> {
    if (game.next.points < 1000 && game.prev.points < 1000) {
        val dice1 = throwDice(diceVal)
        val dice2 = throwDice(dice1)
        val dice3 = throwDice(dice2)
        var nextPos = (game.next.pos + dice1 + dice2 + dice3) % 10
        if (nextPos == 0) {
            nextPos = 10
        }
        val newPrev = game.next.copy(points = game.next.points + nextPos, pos = nextPos)
        return playDeterministic(Game(game.prev, newPrev), dice3, numDiceThrows + 3)

    }
    return numDiceThrows to game
}

data class Player(val id: Int, val pos: Int, val points: Int)

data class Game(val next: Player, val prev: Player)

fun playDirac(playingGames: Map<Game, Long>, wonGames: MutableMap<Game, Long>): Map<Game, Long> {
    if (playingGames.isEmpty()) {
        return wonGames
    }
    val newPlayingGames = HashMap<Game, Long>()
    for (gameAndCount in playingGames){
        val game = gameAndCount.key
        val count = gameAndCount.value
        val diceTriples = (1..3).flatMap { x -> (1..3).flatMap { y -> (1..3).map { z -> Triple(x, y, z) } } }
        val games =
            diceTriples
                .map { (game.next.pos + it.first + it.second + it.third) % 10 }
                .map { if (it == 0 ) 10 else it }
                .map { game.next.copy(points = game.next.points + it, pos = it)}
                .map { Game(game.prev, it)}
        games.forEach {
            if (it.next.points >= 21 || it.prev.points >= 21){
                wonGames[it] = (wonGames[it] ?: 0) + count
            }
            else {
                newPlayingGames[it] = (newPlayingGames[it] ?: 0) + count
            }
        }
    }
    return playDirac(newPlayingGames, wonGames)
}

fun main() {
    val startPos = parseInput(input)
    val (numDiceTrows, game) = playDeterministic(Game(Player(1, startPos.first, 0), Player(2, startPos.second, 0)), 100, 0)
    val loosingScore = if (game.prev.points < game.next.points) game.prev.points else game.next.points
    val task1 = numDiceTrows * loosingScore
    println("Task1: $task1") // 504972

    val map = HashMap<Game, Long>().apply {
        this[Game(Player(1, startPos.first, 0), Player(2, startPos.second, 0))] = 1L
    }

    var count1 = 0L
    var count2 = 0L
    val games = playDirac(map, mutableMapOf())
    for (gameAndCount in games){
        val count = gameAndCount.value
        val game = gameAndCount.key
        val winner = if (game.next.points >= 21) game.next else game.prev
        if (winner.id == 1) {
            count1 += count
        } else {
            count2 += count
        }
    }
    val task2 = max(count1, count2)
    println("Task2: $task2") // 446968027750017
}