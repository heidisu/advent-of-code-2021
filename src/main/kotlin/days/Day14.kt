package days

import common.readLines

private const val fileName = "day14.txt"
private val input = readLines(fileName)
private val testInput = """
    NNCB

    CH -> B
    HH -> N
    CB -> H
    NH -> C
    HB -> C
    HC -> B
    HN -> C
    NN -> C
    BH -> H
    NC -> B
    NB -> B
    BN -> B
    BB -> N
    BC -> B
    CC -> N
    CN -> C
""".trimIndent().lines()

private fun parseRules(lines: List<String>): Pair<String, Map<String, String>> {
    val template = lines.first()
    val map = HashMap<String, String>()
    lines.drop(2)
        .map { it.split(" -> ") }
        .forEach { map[it[0].trim()] = it[1].trim() }
    return template to map
}

private fun step(template: String, rules: Map<String, String>): String {
    return template
        .windowed(2)
        .map { pair -> rules[pair]?.let { pair[0] + it + pair[1] } ?: pair }
        .fold("") { acc, str ->
            if (acc.isNotEmpty()) {
                acc + str.drop(1)
            } else {
                str
            }
        }
}

private fun fasterStep(arr: Array<String>, rules: Map<String, String>): Array<String> {
    val newArr = Array(arr.size * 2 - 1) { "" }
    for (i in 0 until arr.size - 1) {
        newArr[2 * i] = arr[i]
        newArr[2 * i + 1] = rules.getOrDefault(arr[i] + arr[i + 1], "")
    }
    newArr[newArr.size - 1] = arr[arr.size - 1]
    return newArr
}

// there must be a better way...
fun task2(template: String, rules: Map<String, String>): Long {
    // run 20 times with faster step
    var result = template.map { it.toString() }.toTypedArray()
    for (i in 1..20) {
        result = fasterStep(result, rules)
    }
    val step20 = result.joinToString("")

    // precalculate tree for each rule for 20 steps
    val preCalc = HashMap<String, Map<String, Long>>()
    for (key in rules.keys) {
        var res = key.map { it.toString() }.toTypedArray()
        for (i in 1..20) {
            res = fasterStep(res, rules)
        }
        preCalc[key] = res.groupingBy { it }.eachCount().mapValues { e -> e.value.toLong() }
    }

    // total = split step20 in tuples, use precalc tree, minus all middle letters from step20
    // ABCD = [AB] [BC] [CD] => middle letters BC counted twice
    val subtract = step20.drop(1).dropLast(1).groupingBy { it.toString() }.eachCount()
    val tuples = step20.windowed(2)
    val countMap = HashMap<String, Long>()
    for (tuple in tuples) {
        val preCalculatedForTuple = preCalc[tuple]!!
        for (entry in preCalculatedForTuple.entries) {
            countMap.merge(entry.key, entry.value) { a, b -> a + b }
        }
    }
    for (entry in subtract.entries) {
        countMap[entry.key] = countMap[entry.key]!! - entry.value
    }

    val min = countMap.values.minOf { it }
    val max = countMap.values.maxOf { it }
    return max - min
}

fun main() {
    val (template, rules) = parseRules(input)
    val result = (1..10).fold(template) { acc, _ -> step(acc, rules) }.groupingBy { it }.eachCount()
    val task1 = result.maxOf { it.value } - result.minOf { it.value }
    println("Task1: $task1") // 2891

    val task2 = task2(template, rules)
    println("Task2: $task2") // 4607749009683
}