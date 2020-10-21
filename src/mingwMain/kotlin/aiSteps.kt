package ninemensmorris

import kotlin.random.Random

class EvaluatedBoard(var board: Board = Board(Array(0) { GameColor.F }), var characteristics: Int = Int.MIN_VALUE)

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
    val move = bestMove(board, aiColor, oppositeColor(aiColor)).board

    return move.difference(board, aiColor)
}

fun bestMove(board: Board, playerColor: GameColor, opponentColor: GameColor, depth: Int = 3,
             alpha: Int = Int.MIN_VALUE, beta: Int = Int.MAX_VALUE, firstPlayer: Boolean = true): EvaluatedBoard {
    val finalBoard = EvaluatedBoard(board)
    var currentAlpha = alpha
    var currentBeta = beta

    if (depth == 0) {
        finalBoard.characteristics = heuristics(board, playerColor)
        return finalBoard
    }

    val possibleConfigurations = board.possibleMoves(playerColor).distinct()
    val steps = possibleConfigurations.asSequence().map { it.difference(board, playerColor) }.toList()

    for (configuration in possibleConfigurations) {
        val currentBoard = bestMove(board, opponentColor, playerColor, depth - 1, currentAlpha, currentBeta, !firstPlayer)
        if (firstPlayer) {
            if (currentBoard.characteristics > currentAlpha) {
                currentAlpha = currentBoard.characteristics
                finalBoard.board = configuration
                finalBoard.characteristics = currentAlpha
            }
        } else {
            if (currentBoard.characteristics < currentBeta) {
                currentBeta = currentBoard.characteristics
                finalBoard.board = configuration
                finalBoard.characteristics = currentBeta
            }
        }
        if (currentAlpha >= currentBeta)
            break
    }

    finalBoard.characteristics = if (firstPlayer) currentAlpha else currentBeta
    return finalBoard
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
