package days

import common.readLines

private const val fileName = "day18.txt"
private val input = readLines(fileName)
private val testInput = """
    [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
    [[[5,[2,8]],4],[5,[[9,9],0]]]
    [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
    [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
    [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
    [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
    [[[[5,4],[7,7]],8],[[8,3],8]]
    [[9,3],[[9,9],[6,[4,9]]]]
    [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
    [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
""".trimIndent().lines()

sealed class Tree {
    class Leaf(var value: Int) : Tree() {
        override fun toString(): String {
            return "Leaf($value)"
        }

        override fun magnitude(): Long {
            return value.toLong()
        }
    }

    class Node(var left: Tree, var right: Tree) : Tree() {
        override fun toString(): String {
            return "Node(${left}, ${right})"
        }

        override fun magnitude(): Long {
            return 3 * left.magnitude() + 2 * right.magnitude()
        }
    }

    abstract fun magnitude(): Long
}

fun findNodeToExplode(tree: Tree, acc: Int): Tree.Node? {
    return when (tree) {
        is Tree.Node -> if (acc == 4 && tree.left is Tree.Leaf && tree.right is Tree.Leaf) {
            tree
        } else {
            findNodeToExplode(tree.left, acc + 1) ?: findNodeToExplode(tree.right, acc + 1)
        }
        is Tree.Leaf -> null
    }
}

fun findLeafToSplit(tree: Tree): Tree.Leaf? {
    return when (tree) {
        is Tree.Node -> findLeafToSplit(tree.left) ?: findLeafToSplit(tree.right)
        is Tree.Leaf -> if (tree.value >= 10) {
            return tree
        } else null
    }
}

fun findFirstLeft(node: Tree): Tree.Leaf? {
    return when (node) {
        is Tree.Leaf -> node
        is Tree.Node -> findFirstLeft(node.left)
    }
}

fun findFirstRight(node: Tree): Tree.Leaf? {
    return when (node) {
        is Tree.Leaf -> node
        is Tree.Node -> findFirstRight(node.right)
    }
}

fun findLeftLeaf(node: Tree.Node, tree: Tree): Tree.Leaf? {
    var prev = node
    var parent = findParent(node, tree)
    while (parent != null) {
        if (parent.left is Tree.Leaf) {
            return parent.left as Tree.Leaf
        } else if (parent.left != prev) {
            return findFirstRight(parent.left as Tree.Node)
        }
        prev = parent
        parent = findParent(parent, tree)
    }
    return null
}

fun findRightLeaf(node: Tree.Node, tree: Tree): Tree.Leaf? {
    var prev = node
    var parent = findParent(node, tree)
    while (parent != null) {
        if (parent.right is Tree.Leaf) {
            return parent.right as Tree.Leaf
        } else if (parent.right != prev) {
            return findFirstLeft(parent.right as Tree.Node)
        }

        prev = parent
        parent = findParent(parent, tree)
    }
    return null
}

fun findParent(node: Tree, tree: Tree): Tree.Node? {
    return when (tree) {
        is Tree.Node -> if (tree.left == node || tree.right == node) {
            return tree
        } else {
            findParent(node, tree.left) ?: findParent(node, tree.right)
        }
        is Tree.Leaf -> null
    }
}

fun explode(tree: Tree): Boolean {
    val toExplode = findNodeToExplode(tree, 0)
    if (toExplode != null) {
        val left = findLeftLeaf(toExplode, tree)
        val parent = findParent(toExplode, tree)
        val right = findRightLeaf(toExplode, tree)
        left?.let { it.value += (toExplode.left as Tree.Leaf).value }
        right?.let { it.value += (toExplode.right as Tree.Leaf).value }
        parent?.let {
            if (it.left == toExplode) {
                it.left = Tree.Leaf(0)
            } else {
                it.right = Tree.Leaf(0)
            }
        }
        return true
    }
    return false
}

fun split(tree: Tree): Boolean {
    val toSplit = findLeafToSplit(tree)
    if (toSplit != null) {
        val parent = findParent(toSplit, tree)
        val leftVal = toSplit.value / 2
        val rightVal = if (leftVal * 2 == toSplit.value) {
            leftVal
        } else {
            leftVal + 1
        }
        val newNode = Tree.Node(Tree.Leaf(leftVal), Tree.Leaf(rightVal))
        parent?.let {
            if (it.left == toSplit) {
                it.left = newNode
            } else {
                it.right = newNode
            }
        }
        return true
    }
    return false
}

fun add(left: Tree, right: Tree): Tree {
    return Tree.Node(left, right)
}

fun reduce(tree: Tree): Tree {
    var stop = false
    while (!stop) {
        val explode = explode(tree)
        if (!explode) {
            val split = split(tree)
            if (!split) {
                stop = true
            }
        }
    }
    return tree
}

private fun toPair(line: String): String {
    return line.replace("[", "Pair(").replace("]", ")")
}

// lazy input parsing, run on input, paste into variable in code
fun printAsPair(lines: List<String>) {
    val list = lines.map { toPair(it) }.joinToString(",")
    println("listOf($list)")
}

fun pairToTree(pair: Pair<Any, Any>): Tree.Node {
    val left = if (pair.first is Int) {
        Tree.Leaf(pair.first as Int)
    } else {
        pairToTree(pair.first as Pair<Any, Any>)
    }
    val right = if (pair.second is Int) {
        Tree.Leaf(pair.second as Int)
    } else {
        pairToTree(pair.second as Pair<Any, Any>)
    }
    return Tree.Node(left, right)
}

fun main() {
    val treeData = testData.map { pairToTree(it) }
    val first = treeData.first() as Tree
    val res = treeData.drop(0).fold(first) { acc, next -> reduce(add(acc, next)) }
    println("Task1: ${res.magnitude()}") // 3935

    val arr = testData.toTypedArray()
    val idx =
        (0 until arr.size).flatMap { x -> (0 until arr.size).map { y -> Pair(x, y) } }.filter { it.first != it.second }
    val sums = idx.map { reduce(add(pairToTree(arr[it.first]), pairToTree(arr[it.second]))).magnitude() }
        .sortedByDescending { it }
    println("Task2: ${sums.first()}") // 4669
}

val testData: List<Pair<Any, Any>> = listOf(
    Pair(
        Pair(Pair(0, Pair(5, 8)), Pair(Pair(1, 7), Pair(9, 6))),
        Pair(Pair(4, Pair(1, 2)), Pair(Pair(1, 4), 2))
    ),
    Pair(Pair(Pair(5, Pair(2, 8)), 4), Pair(5, Pair(Pair(9, 9), 0))),
    Pair(6, Pair(Pair(Pair(6, 2), Pair(5, 6)), Pair(Pair(7, 6), Pair(4, 7)))),
    Pair(Pair(Pair(6, Pair(0, 7)), Pair(0, 9)), Pair(4, Pair(9, Pair(9, 0)))),
    Pair(Pair(Pair(7, Pair(6, 4)), Pair(3, Pair(1, 3))), Pair(Pair(Pair(5, 5), 1), 9)),
    Pair(Pair(6, Pair(Pair(7, 3), Pair(3, 2))), Pair(Pair(Pair(3, 8), Pair(5, 7)), 4)),
    Pair(Pair(Pair(Pair(5, 4), Pair(7, 7)), 8), Pair(Pair(8, 3), 8)),
    Pair(Pair(9, 3), Pair(Pair(9, 9), Pair(6, Pair(4, 9)))),
    Pair(Pair(2, Pair(Pair(7, 7), 7)), Pair(Pair(5, 8), Pair(Pair(9, 3), Pair(0, 2)))),
    Pair(Pair(Pair(Pair(5, 2), 5), Pair(8, Pair(3, 7))), Pair(Pair(5, Pair(7, 5)), Pair(4, 4)))
)


