package days

import functions.readLines

private const val fileName = "day12.txt"
private val input = readLines(fileName)
private val testInput = """
    start-A
    start-b
    A-c
    A-b
    b-d
    A-end
    b-end
""".trimIndent().lines()

private val testInput2 = """
    dc-end
    HN-start
    start-kj
    dc-start
    dc-HN
    LN-dc
    HN-end
    kj-sa
    kj-HN
    kj-dc
""".trimIndent().lines()

private val testInput3 = """
    fs-end
    he-DX
    fs-he
    start-DX
    pj-DX
    end-zg
    zg-sl
    zg-pj
    pj-he
    RW-he
    fs-DX
    pj-RW
    zg-RW
    start-pj
    he-WI
    zg-he
    pj-fs
    start-RW
""".trimIndent().lines()

private fun parseLines(lines: List<String>): Map<String, List<String>> {
    val map = HashMap<String, List<String>>()
    lines.map { it.split("-") }.map { it[0] to it[1] }.forEach {
        map.merge(it.first, listOf(it.second)) { acc, l -> acc + l }
        map.merge(it.second, listOf(it.first)) { acc, l -> acc + l }
    }
    return map
}

private fun searchPaths(
    map: Map<String, List<String>>,
    finished: List<List<String>>,
    ongoing: List<List<String>>,
    allowedLowerCase: (String, List<String>) -> Boolean
): List<List<String>> {
    if (ongoing.isEmpty()) {
        return finished
    } else {
        val newFinished = mutableListOf<List<String>>()
        newFinished.addAll(finished)
        val newOngoing = mutableListOf<List<String>>()
        ongoing.forEach { l ->
            val nextSteps = map[l.last()]
            nextSteps?.forEach { c ->
                if (c == "end") {
                    newFinished.add(l + listOf(c))
                } else if (c != "start" && (c == c.uppercase() || allowedLowerCase(c, l))) {
                    newOngoing.add(l + listOf(c))
                }
            }
        }
        return searchPaths(map, newFinished, newOngoing, allowedLowerCase)
    }
}

private fun allowedLowerCase(c: String, path: List<String>): Boolean {
    return path.count { it == c } == 0
            || path.filter { it == it.lowercase() }.groupingBy { it }.eachCount().values.count { it > 1 } == 0
}

fun main() {
    val map = parseLines(input)
    val task1 = searchPaths(map, emptyList(), listOf(listOf("start"))) { str, lst -> !lst.contains(str) }.size
    println("Task1: $task1") // 5178

    val task2 =
        searchPaths(map, emptyList(), listOf(listOf("start"))) { str, lst -> allowedLowerCase(str, lst) }.size
    println("Task2: $task2") // 130094
}