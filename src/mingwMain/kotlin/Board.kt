package ninemensmorris

class Board(var b: Array<GameColor>) {

    val indices = b.indices

    operator fun get(i: Int) = b[i]
    operator fun set(i: Int, elem: GameColor) {
        b[i] = elem
    }

    fun copyOf(): Board = Board(b.copyOf())

    fun count(playerColor: GameColor) = b.count { x -> x == playerColor }

    fun freePlace(i: Int) = b[i] == GameColor.F

    fun closeMill(i: Int, playerColor: GameColor): Boolean {
        val linesNeighbors = linesNeighbors[i]
        return b[linesNeighbors[0]] == playerColor && b[linesNeighbors[1]] == playerColor
                || b[linesNeighbors[2]] == playerColor && b[linesNeighbors[3]] == playerColor
    }

    fun inMill(i: Int, playerColor: GameColor): Boolean {
        return closeMill(i, playerColor) && b[i] == playerColor
    }

    fun possibleMillsCount(playerColor: GameColor): Int {
        var count = 0
        for (i in indices)
            if (neighbors[i].any { x -> b[x] == playerColor } && freePlace(i) && closeMill(i, playerColor))
                count++

        return count
    }

    fun possibleMoves(playerColor: GameColor): Array<Board> {
        val stage = if (count(playerColor) > 3) 2 else 3
        val boards = ArrayList<Board>()

        for (i in this.indices) {
            if (b[i] == playerColor) {
                val candidates =
                    if (stage == 2) neighbors[i]
                    else b.indices.toList().toTypedArray()
                for (candidate in candidates) {
                    if (freePlace(candidate)) {
                        val boardClone = copyOf()
                        boardClone[i] = GameColor.F
                        boardClone[candidate] = playerColor
                        if (boardClone.inMill(candidate, playerColor))
                            boards.addAll(movesIfRemovePiece(boardClone, playerColor))
                        else
                            boards.add(boardClone)
                    }
                }
            }
        }
        return boards.toTypedArray()
    }

    fun blockedPieces(playerColor: GameColor): Int {
        val opponentColor = oppositeColor(playerColor)
        var count = 0

        for (position in belongingPositions(playerColor)) {
            val neighbors = neighbors[position]
            var blockingNeighborsCount = 0
            for (neighbor in neighbors) {
                if (this[neighbor] == opponentColor)
                    blockingNeighborsCount++
            }
            if (blockingNeighborsCount == neighbors.size)
                count++
        }
        return count
    }

    fun belongingPositions(color: GameColor): ArrayList<Int> {
        val result = ArrayList<Int>()
        for (i in indices)
            if (this[i] == color)
                result.add(i)
        return result
    }


    fun difference(oldBoard: Board, playerColor: GameColor? = null): Triple<Int, Int, Int?> {
        var from = -1
        var to = -1
        var removed: Int? = null

        for (i in indices) {
            if (this[i] != oldBoard[i]) {
                if (playerColor != null && freePlace(i) && oldBoard[i] == oppositeColor(playerColor))
                    removed = i
                else if (oldBoard.freePlace(i))
                    to = i
                else
                    from = i
            }
        }

        if (from == -1 || to == -1)
            throw IllegalArgumentException("Провалена проверка хода при вычитании досок")

        return Triple(from, to, removed)
    }

    override fun toString(): String {
        var res = ""
        for (i in b.indices) {
            res += "(" + b[i] + "," + i + ")"
        }
        return res
    }

    override fun hashCode(): Int {
        return b.contentHashCode()
    }

    fun print() {
        println("${b[0]}(0)---------------------- ${b[1]}(1)---------------------- ${b[2]}(2)")
        println("|                           |                           |")
        println("|       ${b[8]}(8)------------- ${b[9]}(9)------------ ${b[10]}(10)        |")
        println("|       |                   |                   |       |")
        println("|       |         ${b[16]}(16)--- ${b[17]}(17)--- ${b[18]}(18)       |       |")
        println("|       |         |                   |         |       |")
        println("${b[3]}(3)-- ${b[11]}(11)-- ${b[19]}(19)                 ${b[20]}(20)-- ${b[12]}(12)-- ${b[4]}(4)")
        println("|       |         |                   |         |       |")
        println("|       |         ${b[21]}(21)--- ${b[22]}(22)--- ${b[23]}(23)       |       |")
        println("|       |                   |                   |       |")
        println("|       ${b[13]}(13)-------------- ${b[14]}(14)------------ ${b[15]}(15)     |")
        println("|                           |                           |")
        println("${b[5]}(5)---------------------- ${b[6]}(6)---------------------- ${b[7]}(7)")
    }
}