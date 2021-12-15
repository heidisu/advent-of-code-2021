package common

import java.io.File
import java.lang.Exception

object Clazz

fun readLines(fileName: String): List<String> {
    val file = Clazz::class.java.classLoader.getResource(fileName) ?: throw Exception("No such file: $fileName")
    return File(file.toURI()).readLines()
}