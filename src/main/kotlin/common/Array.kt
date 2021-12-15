package common

fun <T> Array<Array<T>>.print() {
    val rows = this.size
    val cols = this[0].size
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            print("${this[i][j]} ")
        }
        println()
    }
}