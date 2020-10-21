package ninemensmorris

import platform.posix.fflush
import platform.posix.fprintf

fun subtractBoards(begin: Board, end: Board): Pair<Int, Int> {
    var from = 0
    var to = 0
    for (i in begin.indices) {
        if (begin[i] != end[i])
            if (end[i] == GameColor.F)
                from = i
            else
                to = i
    }
    return Pair(from, to)
}

val neighbors = arrayOf(
        arrayOf(1, 3),
        arrayOf(0, 2, 9),
        arrayOf(1, 4),
        arrayOf(0, 5, 11),
        arrayOf(2, 7, 12),
        arrayOf(3, 6),
        arrayOf(5, 7, 14),
        arrayOf(4, 6),
        arrayOf(9, 11),
        arrayOf(1, 8, 10, 17),
        arrayOf(9, 12),
        arrayOf(3, 8, 13, 19),
        arrayOf(4, 10, 15, 20),
        arrayOf(11, 14),
        arrayOf(6, 13, 15, 22),
        arrayOf(12, 14),
        arrayOf(17, 19),
        arrayOf(9, 16, 18),
        arrayOf(17, 20),
        arrayOf(11, 16, 21),
        arrayOf(12, 18, 23),
        arrayOf(19, 22),
        arrayOf(21, 23, 14),
        arrayOf(20, 22)
)

val linesNeighbors = arrayOf(
        arrayOf(1, 2, 3, 5),
        arrayOf(0, 2, 9, 17),
        arrayOf(0, 1, 4, 7),
        arrayOf(0, 5, 11, 19),
        arrayOf(2, 7, 12, 20),
        arrayOf(0, 3, 6, 7),
        arrayOf(5, 7, 14, 22),
        arrayOf(2, 4, 5, 6),
        arrayOf(9, 10, 11, 13),
        arrayOf(8, 10, 1, 17),
        arrayOf(8, 9, 12, 15),
        arrayOf(3, 19, 8, 13),
        arrayOf(20, 4, 10, 15),
        arrayOf(8, 11, 14, 15),
        arrayOf(13, 15, 6, 22),
        arrayOf(13, 14, 10, 12),
        arrayOf(17, 18, 19, 21),
        arrayOf(1, 9, 16, 18),
        arrayOf(16, 17, 20, 23),
        arrayOf(16, 21, 3, 11),
        arrayOf(12, 4, 18, 23),
        arrayOf(16, 19, 22, 23),
        arrayOf(6, 14, 21, 23),
        arrayOf(18, 20, 21, 22)
)

val STDERR = platform.posix.fdopen(2, "w")

fun printErr(message: String) {
    fprintf(STDERR, message + "\n")
    fflush(STDERR)
}