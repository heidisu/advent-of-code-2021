package tools

import java.io.File
import java.time.LocalDate

private val day = LocalDate.now().dayOfMonth

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

private val readmeLine = "\n| [Day $day](src/main/kotlin/days/Day$day.kt) |"

fun main() {
    val resourceFile = File(dataResourcePath)
    if (!resourceFile.exists()) {
        resourceFile.createNewFile()
    }

    val kotlinFile = File(codePath)
    if (!kotlinFile.exists()) {
        kotlinFile.createNewFile()
        kotlinFile.writeText(codeContent)
    }

    val readmeFile = File(readmePath)
    if(!readmeFile.readText().contains(readmeLine)) {
        readmeFile.appendText(readmeLine)
    }

}