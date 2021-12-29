package days

import common.Point3d
import common.readLines
import kotlin.math.abs

private const val fileName = "day19.txt"
private val input = readLines(fileName)
private val testInput = """
    --- scanner 0 ---
    404,-588,-901
    528,-643,409
    -838,591,734
    390,-675,-793
    -537,-823,-458
    -485,-357,347
    -345,-311,381
    -661,-816,-575
    -876,649,763
    -618,-824,-621
    553,345,-567
    474,580,667
    -447,-329,318
    -584,868,-557
    544,-627,-890
    564,392,-477
    455,729,728
    -892,524,684
    -689,845,-530
    423,-701,434
    7,-33,-71
    630,319,-379
    443,580,662
    -789,900,-551
    459,-707,401

    --- scanner 1 ---
    686,422,578
    605,423,415
    515,917,-361
    -336,658,858
    95,138,22
    -476,619,847
    -340,-569,-846
    567,-361,727
    -460,603,-452
    669,-402,600
    729,430,532
    -500,-761,534
    -322,571,750
    -466,-666,-811
    -429,-592,574
    -355,545,-477
    703,-491,-529
    -328,-685,520
    413,935,-424
    -391,539,-444
    586,-435,557
    -364,-763,-893
    807,-499,-711
    755,-354,-619
    553,889,-390
    
    --- scanner 2 ---
    649,640,665
    682,-795,504
    -784,533,-524
    -644,584,-595
    -588,-843,648
    -30,6,44
    -674,560,763
    500,723,-460
    609,671,-379
    -555,-800,653
    -675,-892,-343
    697,-426,-610
    578,704,681
    493,664,-388
    -671,-858,530
    -667,343,800
    571,-461,-707
    -138,-166,112
    -889,563,-600
    646,-828,498
    640,759,510
    -630,509,768
    -681,-892,-333
    673,-379,-804
    -742,-814,-386
    577,-820,562

    --- scanner 3 ---
    -589,542,597
    605,-692,669
    -500,565,-823
    -660,373,557
    -458,-679,-417
    -488,449,543
    -626,468,-788
    338,-750,-386
    528,-832,-391
    562,-778,733
    -938,-730,414
    543,643,-506
    -524,371,-870
    407,773,750
    -104,29,83
    378,-903,-323
    -778,-728,485
    426,699,580
    -438,-605,-362
    -469,-447,-387
    509,732,623
    647,635,-688
    -868,-804,481
    614,-800,639
    595,780,-596

    --- scanner 4 ---
    727,592,562
    -293,-554,779
    441,611,-461
    -714,465,-776
    -743,427,-804
    -660,-479,-426
    832,-632,460
    927,-485,-438
    408,393,-506
    466,436,-512
    110,16,151
    -258,-428,682
    -393,719,612
    -211,-452,876
    808,-476,-593
    -575,615,604
    -485,667,467
    -680,325,-822
    -627,-443,-432
    872,-547,-609
    833,512,582
    807,604,487
    839,-516,451
    891,-625,532
    -652,-548,-490
    30,-46,-14
""".trimIndent().lines()

data class Scanner(val id: Int, val points: List<Point3d>)

private fun parseInput(lines: List<String>): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var id = 0
    var points = mutableListOf<Point3d>()
    for (line in lines) {
        if (line.isEmpty()) {
            scanners.add(Scanner(id, points))
            points = mutableListOf()
        } else if (line.startsWith("---")) {
            id = Integer.parseInt(line.split(" ")[2])
        } else {
            val coords = line.split(",")
            points.add(
                Point3d(
                    x = Integer.parseInt(coords[0]),
                    y = Integer.parseInt(coords[1]),
                    z = Integer.parseInt(coords[2])
                )
            )
        }
    }
    scanners.add(Scanner(id, points))

    return scanners
}

val rotations: List<(Point3d) -> Point3d> =
    listOf(-1, 1)
        .flatMap { x -> listOf(-1, 1).flatMap { y -> listOf(-1, 1).map { z -> Point3d(x, y, z) } } }
        .map { { pt -> Point3d(pt.x * it.x, pt.y * it.y, pt.z * it.z) } }

val permutations: List<(Point3d) -> Point3d> =
    listOf(
        { pt -> Point3d(pt.x, pt.y, pt.z) },
        { pt -> Point3d(pt.x, pt.z, pt.y) },
        { pt -> Point3d(pt.y, pt.x, pt.z) },
        { pt -> Point3d(pt.y, pt.z, pt.x) },
        { pt -> Point3d(pt.z, pt.x, pt.y) },
        { pt -> Point3d(pt.z, pt.y, pt.x) }
    )

val rotationsAndPermutations = rotations.flatMap { o -> permutations.map { p -> o to p } }

private fun intersect(scanner1: Scanner, scanner2: Scanner): Triple<Int, Int, (Point3d) -> Point3d>? {
    for (pt1 in scanner1.points) {
        for (pt2 in scanner2.points) {
            for ((rotation, perm) in rotationsAndPermutations) {
                val newPt2 = perm.invoke(rotation.invoke(pt2))
                val factor1 = pt1.x - newPt2.x
                val factor2 = pt1.y - newPt2.y
                val factor3 = pt1.z - newPt2.z
                val transform: (Point3d) -> Point3d = { pt ->
                    val trans = perm.invoke(rotation.invoke(pt))
                    Point3d(trans.x + factor1, trans.y + factor2, trans.z + factor3)
                }
                val transformedPoints =
                    scanner2.points.map(transform)
                val commonBeacons = transformedPoints.intersect(scanner1.points.toSet())
                if (commonBeacons.size >= 12) {
                    return Triple(scanner2.id, scanner1.id, transform)
                }
            }
        }
    }
    return null
}

fun totalTransformation(
    id: Int,
    transformations: List<Triple<Int, Int, (Point3d) -> Point3d>>,
    visited: List<Int>,
    acc: (Point3d) -> Point3d
): ((Point3d) -> Point3d)? {
    if (id == 0) {
        return acc
    } else {
        val possibleTransformations = transformations.filter { (x, _, _) -> !visited.contains(x) && x == id }
        val lst = mutableListOf<((Point3d) -> Point3d)?>()
        for ((x, y, trans) in possibleTransformations){
            lst.add(totalTransformation(y, transformations, visited + listOf(x)){pt -> trans.invoke(acc.invoke(pt))} )
        }
        return lst.filterNotNull().firstOrNull()
    }
}

fun manhattan(pt1: Point3d, pt2: Point3d): Int {
    return abs(pt1.x - pt2.x) + abs(pt1.y - pt2.y) + abs(pt1.z - pt2.z)
}

fun main() {
    val scanners = parseInput(input)
    val scannerPairs = scanners.flatMap { s1 -> scanners.map { s2 -> s1 to s2 } }
        .filter { (s1, s2) -> s1.id != s2.id }
    val pairTransformations = scannerPairs.mapNotNull { (s1, s2) -> intersect(s1, s2) }
    val transformToZero = scanners.map { it to totalTransformation(it.id, pairTransformations, mutableListOf()) { pt -> pt }!! }
    val task1 = transformToZero.flatMap { (s, trans) -> s.points.map(trans) }.toSet().count()

    println("Task1: $task1") // 479
    val scannerPositions = transformToZero.map { (_, trans) -> trans(Point3d(0,0,0)) }
    val task2 = scannerPositions.flatMap { x -> scannerPositions.map { y -> x to y } }.map { manhattan(it.first, it.second) }.maxOf { it }
    println("Task2: $task2") // 13113
}