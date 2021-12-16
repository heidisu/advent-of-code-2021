package days

import common.readLines
import java.lang.IllegalArgumentException
import kotlin.math.pow

private const val fileName = "day16.txt"
private val input = readLines(fileName)
private val testInput = """
    9C0141080250320F1802104A08
""".trimIndent().lines()

private val hexToByte = HashMap<Char, String>().apply {
    put('0', "0000")
    put('1', "0001")
    put('2', "0010")
    put('3', "0011")
    put('4', "0100")
    put('5', "0101")
    put('6', "0110")
    put('7', "0111")
    put('8', "1000")
    put('9', "1001")
    put('A', "1010")
    put('B', "1011")
    put('C', "1100")
    put('D', "1101")
    put('E', "1110")
    put('F', "1111")
}

sealed class Packet(open val version: Int) {
    data class Literal(override val version: Int, val value: Long) : Packet(version) {
        override fun versionSum(): Int {
            return version
        }

        override fun eval(): Long {
            return value
        }
    }

    sealed class Operator(override val version: Int, open val type: Int, open val subPackets: List<Packet>) :
        Packet(version) {
        override fun versionSum(): Int {
            return version + subPackets.sumOf { it.versionSum() }
        }

        data class Sum(override val version: Int, override val type: Int, override val subPackets: List<Packet>) :
            Operator(version, type, subPackets) {
            override fun eval(): Long {
                return subPackets.sumOf { it.eval() }
            }
        }

        data class Product(override val version: Int, override val type: Int, override val subPackets: List<Packet>) :
            Operator(version, type, subPackets) {
            override fun eval(): Long {
                return subPackets.map { it.eval() }.fold(1L) { acc, n -> acc * n }
            }
        }

        data class Minimum(override val version: Int, override val type: Int, override val subPackets: List<Packet>) :
            Operator(version, type, subPackets) {
            override fun eval(): Long {
                return subPackets.map { it.eval() }.minOf { it }
            }
        }

        data class Maximum(override val version: Int, override val type: Int, override val subPackets: List<Packet>) :
            Operator(version, type, subPackets) {
            override fun eval(): Long {
                return subPackets.map { it.eval() }.maxOf { it }
            }
        }

        data class GreaterThan(
            override val version: Int,
            override val type: Int,
            override val subPackets: List<Packet>
        ) : Operator(version, type, subPackets) {
            override fun eval(): Long {
                return if (subPackets[0].eval() > subPackets[1].eval()) {
                    1L
                } else {
                    0L
                }
            }
        }

        data class LessThan(override val version: Int, override val type: Int, override val subPackets: List<Packet>) :
            Operator(version, type, subPackets) {
            override fun eval(): Long {
                return if (subPackets[0].eval() < subPackets[1].eval()) {
                    1L
                } else {
                    0L
                }
            }
        }

        data class Equal(override val version: Int, override val type: Int, override val subPackets: List<Packet>) :
            Operator(version, type, subPackets) {
            override fun eval(): Long {
                return if (subPackets[0].eval() == subPackets[1].eval()) {
                    1L
                } else {
                    0L
                }
            }
        }
    }

    abstract fun versionSum(): Int
    abstract fun eval(): Long
}

private fun parseLiteral(bytes: String): Pair<Long, String> {
    var remaining = bytes
    var numberStr = ""
    var stop = false
    while (!stop) {
        val num = remaining.take(5)
        if (num.first() == '0') {
            stop = true
        }
        numberStr += num.drop(1)
        remaining = remaining.drop(5)
    }
    val size = numberStr.length
    var result = 0L
    for (i in 0 until size) {
        val num = Integer.parseInt(numberStr[i].toString()) * 2.toDouble().pow(size - i - 1).toLong()
        result += num
    }
    return result to remaining
}

private fun parseOperator(bytes: String): Pair<List<Packet>, String> {
    val rest = bytes.drop(1)
    return when (bytes.first()) {
        '0' -> {
            val totalLength = Integer.parseInt(rest.take(15), 2)
            val subPackets = mutableListOf<Packet>()
            var remaining = rest.drop(15).take(totalLength)
            while (remaining.isNotEmpty()) {
                val (packet, rem) = parsePacket(remaining)
                subPackets.add(packet)
                remaining = rem
            }
            subPackets to rest.drop(15).drop(totalLength)//remaining
        }
        '1' -> {
            val numPackages = Integer.parseInt(rest.take(11), 2)
            val subPackets = mutableListOf<Packet>()
            var remaining = rest.drop(11)
            for (i in 1..numPackages) {
                val (packet, rem) = parsePacket(remaining)
                subPackets.add(packet)
                remaining = rem
            }
            subPackets to remaining
        }
        else -> throw IllegalArgumentException("oh no")
    }
}

private fun parsePacket(binary: String): Pair<Packet, String> {
    val header = binary.take(6)
    val rest = binary.drop(6)
    val version = Integer.parseInt(header.take(3), 2)
    val type = Integer.parseInt(header.drop(3), 2)
    return when (type) {
        4 -> {
            val (number, rem) = parseLiteral(rest)
            Packet.Literal(version, number) to rem
        }
        else -> {
            val (subPackets, rem) = parseOperator(rest)
            val operator = when(type){
                0 -> Packet.Operator.Sum(version, type, subPackets)
                1 -> Packet.Operator.Product(version, type, subPackets)
                2 -> Packet.Operator.Minimum(version, type, subPackets)
                3 -> Packet.Operator.Maximum(version, type, subPackets)
                5 -> Packet.Operator.GreaterThan(version, type, subPackets)
                6 -> Packet.Operator.LessThan(version, type, subPackets)
                7 -> Packet.Operator.Equal(version, type, subPackets)
                else -> throw IllegalArgumentException("invalid operator type")
            }
            operator to rem
        }
    }
}

private fun toBinary(line: String): String {
    return line.map { hexToByte[it] }.joinToString("")
}

fun main() {
    val transmission = parsePacket(toBinary(input.first())).first
    val task1 = transmission.versionSum()
    println("Task1: $task1") // 991
    val task2 = transmission.eval()
    println("Task2: $task2") // 1264485568252
}