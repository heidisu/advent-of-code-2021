package days

import common.readLines

private const val fileName = "day17.txt"
private val input = readLines(fileName)
private val testInput = """
    target area: x=20..30, y=-10..-5
""".trimIndent().lines()

fun calculateHit(velX: Int, velY: Int, targetPoints: List<Pair<Int, Int>>): Triple<Int, Int, Int>? {
    var currMax = 0
    var x = 0
    var y = 0
    var currVelX = velX
    var currVelY = velY
    var counter = 1
    while (true) {
        x += currVelX
        y += currVelY
        if (currVelX > 0) {
            currVelX--
        } else if (currVelX < 0) {
            currVelX++
        }
        currVelY--

        // probe not hitting target
        if (counter > 500) {
            return null
        }
        if (targetPoints.contains(x to y)) {
            return Triple(velX, velY, currMax)
        }
        counter++
        if (y > currMax) {
            currMax = y
        }
    }
}

private fun parseInput(input: String): Pair<IntRange, IntRange> {
    val regex = """target area: x=([-]*[0-9]+)..([-]*[0-9]+), y=([-]*[0-9]+)..([-]*[0-9]+)""".toRegex()
    val match = regex.matchEntire(input)
    return (Integer.parseInt(match!!.groups[1]!!.value)..Integer.parseInt(match.groups[2]!!.value)) to
            (Integer.parseInt(match.groups[3]!!.value)..Integer.parseInt(match.groups[4]!!.value))
}

// there is probably a smart way of solving this problem, this not it.
// wait 17 minutes for the result...
fun main() {
    val (targetX, targetY) = parseInput(input.first())
    val targetPoints = targetX.flatMap { x -> targetY.map { y -> x to y } }

    val xRange = 0..500
    val yRange = -400..300

    val result = xRange
        .flatMap { s -> yRange.map { t -> calculateHit(s, t, targetPoints) } }
        .filterNotNull()
    val task1 = result.maxByOrNull { (_, _, z) -> z }!!.third
    println("Task1: $task1") // 6555

    val task2 = result.count()
    println("Task2: $task2") // 4973
}