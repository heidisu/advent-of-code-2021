package demo

import java.io.File

class DaySetup {
}

fun main() {
    val file = DaySetup::class.java.classLoader.getResource("demo.txt").readText()
    print(file)
}