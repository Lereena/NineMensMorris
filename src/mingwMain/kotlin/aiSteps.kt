package ninemensmorris

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun firstStageAiStep(board: Board, step: Int, lastMove: Int, aiColor: GameColor): Int {
    println("Ход компьютера:")
    if (step == 1)
        return randomMove(board, aiColor)

    val neighbors = neighbors[lastMove]
    var lastFreeNeighbor = -1
    for (neighbor in neighbors) {
        if (board.freePlace(neighbor)) {
            lastFreeNeighbor = neighbor
            val pointForMill = pointForMill(board, aiColor, neighbor)
            if (pointForMill != -1) {
                board[pointForMill] = aiColor
                return pointForMill
            }
        }
    }

    if (lastFreeNeighbor != -1) {
        board[lastFreeNeighbor] = aiColor
        return lastFreeNeighbor
    }

    return randomMove(board, aiColor)
}

fun secondStageAiStep(board: Board, aiColor: GameColor): Triple<Int, Int, Int?> {
    println("Ход компьютера:")
    val configurations = board.possibleMoves(aiColor)
    var bestMove = board
    var bestScore = Int.MAX_VALUE
    for (configuration in configurations) {
        val score = alphaBetaPruning(board, oppositeColor(aiColor), depth = 4)
        if (bestScore > score) {
            bestScore = score
            bestMove = configuration
        }
    }

    return bestMove.difference(board, aiColor)
}

fun randomSecondStageMove(board: Board, aiColor: GameColor): Board {
    val board = board.copyOf()
    val aiPositions = board.belongingPositions(aiColor)
    val freePositions = board.belongingPositions(GameColor.F)
    var fromPosition = aiPositions[Random.nextInt(0, aiPositions.size)]

    var candidates = if (aiPositions.size > 3) neighbors[fromPosition].filter { x -> board.freePlace(x) }.toTypedArray()
    else freePositions.toTypedArray()

    while (candidates.isEmpty()) {
        fromPosition = aiPositions[Random.nextInt(0, aiPositions.size)]
        candidates = if (aiPositions.size > 3) neighbors[fromPosition].filter { x -> board.freePlace(x) }.toTypedArray()
        else freePositions.toTypedArray()
    }

    val toPosition = candidates[Random.nextInt(0, candidates.size)]

    var removePosition: Int? = null
    if (board.closeMill(toPosition, aiColor)) {
        val opponentPositions = board.belongingPositions(oppositeColor(aiColor))
        removePosition = opponentPositions[Random.nextInt(0, opponentPositions.size)]
    }

    board[fromPosition] = GameColor.F
    board[toPosition] = aiColor
    if (removePosition != null)
        board[removePosition] = GameColor.F

    return board
}

fun alphaBetaPruning(board: Board, playerColor: GameColor, depth: Int = 3, alpha: Int = Int.MIN_VALUE, beta: Int = Int.MAX_VALUE, maximizing: Boolean = false): Int {
    var currentAlpha = alpha
    var currentBeta = beta
    if (depth == 0) {
        return heuristics(board, playerColor)
    }

    val possibleConfigurations = board.possibleMoves(playerColor).distinct()

    if (maximizing) {
        var score = Int.MIN_VALUE
        for (configuration in possibleConfigurations) {
            score = max(score, alphaBetaPruning(configuration, oppositeColor(playerColor), depth - 1, currentAlpha, currentBeta, false))
            currentAlpha = max(currentAlpha, score)
            if (currentAlpha >= currentBeta)
                break
        }
        return score
    } else {
        var score = Int.MAX_VALUE
        for (configuration in possibleConfigurations) {
            score = min(score, alphaBetaPruning(configuration, oppositeColor(playerColor), depth - 1, currentAlpha, currentBeta, true))
            currentBeta = min(currentBeta, score)
            if (currentBeta <= currentAlpha)
                break
        }
        return score
    }

}

fun randomMove(board: Board, playerColor: GameColor): Int {
    var position = Random.nextInt(0, 23)
    while (!board.freePlace(position))
        position = Random.nextInt(0, 23)
    board[position] = playerColor

    return position
}

fun movesIfRemovePiece(board: Board, playerColor: GameColor): ArrayList<Board> {
    val boards = ArrayList<Board>()
    val opponentColor = oppositeColor(playerColor)
    val inMills = HashSet<Int>()
    for (i in board.indices) {
        if (board[i] == opponentColor) {
            if (!board.inMill(i, opponentColor)) {
                val newBoard = board.copyOf()
                newBoard[i] = GameColor.F
                boards.add(newBoard)
            } else
                inMills.add(i)
        }
    }

    if (boards.isEmpty())
        for (i in inMills) {
            val newBoard = board.copyOf()
            newBoard[i] = GameColor.F
            boards.add(newBoard)
        }

    return boards
}

fun pointForMill(board: Board, playerColor: GameColor, point: Int): Int {
    val neighbors = linesNeighbors[point]
    val first = checkTriple(board, playerColor, neighbors[0], neighbors[1])
    val second = checkTriple(board, playerColor, neighbors[2], neighbors[3])
    return when {
        first != -1 && board.freePlace(first) -> first
        second != -1 && board.freePlace(second) -> second
        else -> -1
    }
}

fun checkTriple(board: Board, playerColor: GameColor, p1: Int, p2: Int): Int {
    return when {
        (board[p1] == playerColor && board.freePlace(p2)) -> p2
        (board[p2] == playerColor && board.freePlace(p1)) -> p1
        else -> -1
    }
}
