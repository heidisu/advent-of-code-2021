package days

import functions.readLines
import java.util.*
import kotlin.IllegalArgumentException

private const val fileName = "day10.txt"
private val input = readLines(fileName)
private val testInput = """
    [({(<(())[]>[[{[]{<()<>>
    [(()[<>])]({[<{<<[]>>(
    {([(<{}[<>[]}>{[]{[(<()>
    (((({<>}<{<{<>}{[]{[]{}
    [[<[([]))<([[{}[[()]]]
    [{[{({}]{}}([{[{{{}}([]
    {<[[]]>}<{[{[{[]{()[[[]
    [<(<(<(<{}))><([]([]()
    <{([([[(<>()){}]>(<<{{
    <{([{{}}[<[[[<>{}]]]>[]]
""".trimIndent().lines()

val parens = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')

sealed class ParseResult{
    data class Corrupt(val corruptChar: Char): ParseResult()
    data class InComplete(val inCompleted: Stack<Char>): ParseResult()
}

private fun parse(line: String): ParseResult{
    val stack = Stack<Char>()
    line.toList().forEach {
        when(it) {
            '(', '[', '{', '<' -> stack.push(it)
            ')', ']', '}', '>' -> {
                if (parens[stack.peek()]==it) {
                    stack.pop()
                } else {
                    return ParseResult.Corrupt(it)
                }
            }
        }
    }
    return ParseResult.InComplete(stack)
}

fun completeLine(inComplete: ParseResult.InComplete): List<Char>{
    val chars = mutableListOf<Char>()
    while (inComplete.inCompleted.isNotEmpty()){
        chars.add(parens[inComplete.inCompleted.pop()]!!)
    }
    return chars
}


fun invalidCharToPoints(char: Char):Long{
    return when(char){
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> throw IllegalArgumentException("invalid close paren")
    }
}

fun completedCharToPoints(char: Char): Int{
    return when(char){
        ')' -> 1
        ']' -> 2
        '}' -> 3
        '>' -> 4
        else -> throw IllegalArgumentException("Invalid close paren")
    }
}

fun score(chars: List<Char>): Long{
    return chars.fold(0L){ acc, next -> 5*acc + completedCharToPoints(next) }
}

fun main() {
    val parsedLines = input.map { parse(it) }
    val task1 =
        parsedLines
            .filterIsInstance<ParseResult.Corrupt>()
            .sumOf {  invalidCharToPoints(it.corruptChar) }
    println("Task1: $task1") // 168417

    val sortedScores =
        parsedLines
            .filterIsInstance<ParseResult.InComplete>()
            .map{ completeLine(it) }
            .map { score(it) }.sorted()
    val task2 = sortedScores[sortedScores.size/2]
    println("Task2: $task2") // 2802519786
}