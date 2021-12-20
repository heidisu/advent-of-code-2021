package days

import common.readLines

private const val fileName = "day20.txt"
private val input = readLines(fileName)
private val testInput = """
    ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

    #..#.
    #....
    ##..#
    ..#..
    ..###
""".trimIndent().lines()

private fun parseInput(input: List<String>): Pair<String, Array<Array<Char>>> {
    val imageEnhancement = input.first()
    val image = input.drop(2)
    val rows = image.size
    val cols = image.first().length
    val padding = 51
    val arr = Array(rows + 2 * padding) { Array(cols + 2 * padding) { '.' } }
    image.forEachIndexed() { i, l -> l.forEachIndexed { j, c -> arr[i + padding][j + padding] = c } }
    return imageEnhancement to arr
}

fun enhance(imageEnhancement: String, image: Array<Array<Char>>): Array<Array<Char>> {
    val rows = image.size
    val cols = image.first().size
    val enhanced = Array(rows) { Array(cols) { '.' } }
    val paddingChar = image[0][0]
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            val pos1 = image.getOrNull(i - 1)?.getOrNull(j - 1) ?: paddingChar
            val pos2 = image.getOrNull(i - 1)?.getOrNull(j) ?: paddingChar
            val pos3 = image.getOrNull(i - 1)?.getOrNull(j + 1) ?: paddingChar
            val pos4 = image.getOrNull(i)?.getOrNull(j - 1) ?: paddingChar
            val pos5 = image.getOrNull(i)?.getOrNull(j) ?: paddingChar
            val pos6 = image.getOrNull(i)?.getOrNull(j + 1) ?: paddingChar
            val pos7 = image.getOrNull(i + 1)?.getOrNull(j - 1) ?: paddingChar
            val pos8 = image.getOrNull(i + 1)?.getOrNull(j) ?: paddingChar
            val pos9 = image.getOrNull(i + 1)?.getOrNull(j + 1) ?: paddingChar
            val str = "$pos1$pos2$pos3$pos4$pos5$pos6$pos7$pos8$pos9"
            val binary = str.map { c ->
                if (c == '.') {
                    "0"
                } else {
                    "1"
                }
            }.joinToString("")
            enhanced[i][j] = imageEnhancement[Integer.parseInt(binary, 2)]
        }
    }
    return enhanced
}

fun main() {
    val (imageEnhancement, image) = parseInput(input)
    val result = enhance(imageEnhancement, enhance(imageEnhancement, image))
    val task1 = result.sumOf { l -> l.count { it == '#' } }
    println("Task1: $task1") // 5479

    var result2 = image
    repeat(50) { result2 = enhance(imageEnhancement, result2) }
    val task2 = result2.sumOf { l -> l.count { it == '#' } }
    println("Task2: $task2") // 19012
}