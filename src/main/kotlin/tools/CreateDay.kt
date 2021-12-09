package tools

import java.io.File
import java.time.LocalDate

private val day = LocalDate.now().dayOfMonth + 1

val dataResourcePath = "src/main/resources/day$day.txt"
val codePath = "src/main/kotlin/days/Day$day.kt"
val readmePath = "README.md"

val codeContent = """
    package days

    import functions.readLines

    private const val fileName = "day$day.txt"
    private val input = readLines(fileName)
    private val testInput = ""${'"'}
        
    ""${'"'}.trimIndent().lines()

    fun main() {
        val task1 = ""
        println("Task1: ${'$'}task1")
        val task2 = ""
        println("Task2: ${'$'}task2")
    }
""".trimIndent()

private val readmeLine = "| [Day $day](src/main/kotlin/days/Day$day.kt) |"

fun main() {
    val resourceFile = File(dataResourcePath)
    resourceFile.createNewFile()
    val kotlinFile = File(codePath)
    kotlinFile.createNewFile()
    kotlinFile.writeText(codeContent)
    val readmeFile = File(readmePath)
    readmeFile.appendText(readmeLine)

}