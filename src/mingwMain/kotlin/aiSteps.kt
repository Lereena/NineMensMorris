package ninemensmorris

import kotlin.random.Random

fun randomMove(board: Board, aiColor: GameColor): Int {
    var position = Random.nextInt(0, 23)
    while (!freePlace(board, position))
        position = Random.nextInt(0, 23)
    board[position] = aiColor

    return position
}

fun pointForMill(board: Board, userColor: GameColor, point: Int): Int {
    val neighbors = linesNeighbors[point]
    val first = checkTriple(board, userColor, neighbors[0], neighbors[1])
    val second = checkTriple(board, userColor, neighbors[2], neighbors[3])
    if (first != -1 && freePlace(board, first))
        return first
    if (second != -1 && freePlace(board, second))
        return second
    return -1
//    return if (first != -1) first else second
}

fun checkTriple(board: Board, userColor: GameColor, p1: Int, p2: Int): Int {
    if (board[p1] == userColor && board[p2] == GameColor.F)
        return p2
    if (board[p2] == userColor && board[p1] == GameColor.F)
        return p1
    return -1
}

fun alphaBetaPruning(
        pl1: Boolean, board: Board, aiColor: GameColor, userColor: GameColor,
        depth: Int, alpha: Int, beta: Int,
        heur: (Board) -> Int = { x -> heuristics(x, aiColor, userColor) }
): Evaluation {
    val finalEvaluation = Evaluation()
    var currentAlpha = alpha
    var currentBeta = beta
    if (depth == 0) {
        finalEvaluation.evaluator =
                if (pl1) heur(board)
                else heur(invertedBoard(board))
        return finalEvaluation
    }

    val possibleConfigurations =
            if (pl1) stage23Moves(board, aiColor)
            else invertedBoardList(stage23Moves(invertedBoard(board), userColor))

    for (configuration in possibleConfigurations) {
        val currentEvaluation =
                alphaBetaPruning(!pl1, configuration, aiColor, userColor, depth - 1, currentAlpha, currentBeta)
        if (pl1) {
            if (currentEvaluation.evaluator > currentAlpha) {
                currentAlpha = currentEvaluation.evaluator
                finalEvaluation.board = configuration
            }
        } else {
            if (currentEvaluation.evaluator < currentBeta) {
                currentBeta = currentEvaluation.evaluator
                finalEvaluation.board = configuration
            }
        }
        if (currentAlpha >= currentBeta)
            break
    }
    finalEvaluation.evaluator =
            if (pl1) currentAlpha else currentBeta
    return finalEvaluation
}

private fun stage23Moves(board: Board, playerColor: GameColor): Array<Board> {
    return if (board.count { x -> x == playerColor } == 3)
        moves(board, playerColor, 3)
    else
        moves(board, playerColor, 2)
}

fun moves(board: Board, aiColor: GameColor, stage: Int): Array<Board> {
    var boards = ArrayList<Board>()
    for (i in board.indices) {
        if (board[i] == aiColor) {
            val candidates =
                    if (stage == 2) neighbors[i]
                    else board.indices.toList().toTypedArray()
            for (neighbor in candidates) {
                if (board[neighbor] == GameColor.F) {
                    val boardClone = board.copyOf()
                    boardClone[i] = GameColor.F
                    boardClone[neighbor] = aiColor
                    if (closeMill(neighbor, boardClone))
                        boards = removePiece(boardClone, boards, aiColor)
                    else
                        boards.add(boardClone)
                }
            }
        }
    }
    return boards.toTypedArray()
}

fun heuristics(board: Board, aiColor: GameColor, userColor: GameColor): Int {
    var evaluation = 0
    val aiPieces = board.count { x -> x == aiColor }
    val userPieces = board.count { x -> x == userColor }
    val possibleMillsAI = possibleMillsCount(board, aiColor)
    val movablePieces = stage23Moves(board, aiColor).size
    val potentialMillsUser = potentialMillsPieces(board, userColor, aiColor, userColor)
    if (userPieces <= 2 || movablePieces == 0)
        evaluation = Int.MAX_VALUE
    else if (aiPieces <= 2)
        evaluation = Int.MIN_VALUE
    else {
        if (aiPieces < 4) {
            evaluation += 100 * possibleMillsAI
            evaluation += 200 * potentialMillsUser
        } else {
            evaluation += 200 * possibleMillsAI
            evaluation += 100 * potentialMillsUser
        }
        evaluation -= 25 * movablePieces
        evaluation += 50 * (aiPieces - userPieces)
    }

    return evaluation
}

private fun potentialMills(position: Int, board: Board, color: GameColor): Boolean {
    val neighbors = neighbors[position]
    for (i in neighbors) {
        if (board[i] == color && !(checkMillFormation(board, color, linesNeighbors[i])))
            return true
    }
    return false
}

fun possibleMillsCount(board: Board, color: GameColor): Int {
    var count = 0
    for (i in board.indices)
        if (board[i] == GameColor.F && (checkMillFormation(board, color, linesNeighbors[i])))
            count++

    return count
}

fun potentialMillsPieces(board: Board, color: GameColor, aiColor: GameColor, userColor: GameColor): Int {
    var count = 0
    for (i in board.indices)
        if (board[i] == color) {
            val neighbors = neighbors[i]
            for (position in neighbors) {
                if (color == aiColor && board[position] == userColor) {
                    board[i] = userColor
                    if (closeMill(i, board))
                        count++
                    board[i] = color
                } else if (board[position] == aiColor
                        && potentialMills(position, board, aiColor)
                )
                    count++
            }
        }

    return count
}
