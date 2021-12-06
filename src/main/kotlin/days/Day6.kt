package days

import functions.readLines

private const val fileName = "day6.txt"
private val input = readLines(fileName)
private val testInput = """
    3,4,3,1,2
""".trimIndent().lines()

private class LanternFish(private var timer: Int) {
    private val children = mutableListOf<LanternFish>()
    fun step() {
        children.forEach { it.step() }
        if (timer == 0) {
            timer = 6
            children.add(LanternFish(8))
        } else {
            timer--
        }
    }

    fun count(): Int {
        return children.sumOf { it.count() } + 1
    }

    override fun toString(): String {
        return "LanternFish($timer)"
    }
}

private class Ocean(numbers: List<Int>) {
    private val lanternFish = numbers.map { LanternFish(it) }

    fun step() {
        lanternFish.forEach { it.step() }
    }

    fun count(): Int {
        return lanternFish.sumOf { it.count() }
    }
}

private class EfficientFish(numbers: List<Int>) {
    private val fishArray = Array<Long>(9) { 0 }

    init {
        val counts = numbers.groupingBy { it }.eachCount()
        for (entry in counts.entries) {
            fishArray[entry.key] = entry.value.toLong()
        }
    }

    fun step() {
        val newFish = fishArray[0]
        for (i in 1..8) {
            fishArray[i - 1] = fishArray[i]
        }
        fishArray[6] = fishArray[6] + newFish
        fishArray[8] = newFish
    }

    fun count(): Long {
        return fishArray.sum()
    }
}

fun parseInput(line: String): List<Int> {
    return line.split(",").map { it.toInt() }
}

fun main() {
    val ocean = Ocean(parseInput(input.first()))
    repeat(80) { ocean.step() }
    val task1 = ocean.count()
    println("Task1: $task1") // 388739

    val efficientFish = EfficientFish(parseInput(input.first()))
    repeat(256) { efficientFish.step() }
    val task2 = efficientFish.count()
    println("Task2: $task2") // 1741362314973
}