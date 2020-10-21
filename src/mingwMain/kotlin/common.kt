package ninemensmorris

fun validFirstStageStep(board: Board, position: Int): Pair<Boolean, String> {
    if (position !in 0..23)
        return Pair(false, "Введите число от 0 до 23")
    if (!board.freePlace(position))
        return Pair(false, "Это место уже занято")

    return Pair(true, "")
}

fun validSecondStageStep(board: Board, positions: Triple<Int, Int, Int?>, playerColor: GameColor): Pair<Boolean, String> {
    if (positions.first !in 0..23 || positions.second !in 0..23)
        return Pair(false, "Введите два числа от 0 до 23")
    if (board[positions.first] != playerColor)
        return Pair(false, "На этой позиции нет вашей фишки")
    if (!board.freePlace(positions.second))
        return Pair(false, "Это место уже занято")
    if (positions.first == positions.second)
        return Pair(false, "Фишка уже на этой позиции")
    if (positions.third != null && board[positions.third!!] != oppositeColor(playerColor))
        return Pair(false, "Вы не можете убрать фишку с позиции ${positions.third}")
    val userCount = board.count(playerColor)
    if (userCount > 3 && !neighbors[positions.first].contains(positions.second))
        return Pair(false, "На этой стадии вы не можете двигаться не по линиям")

    return Pair(true, "")
}

fun heuristics(board: Board, playerColor: GameColor): Int {
    val opponentColor = if (playerColor == GameColor.B) GameColor.W else GameColor.B
    var evaluation = 0

    val playerPieces = board.count(playerColor)
    val opponentPieces = board.count(opponentColor)
    val possiblePlayerMills = board.possibleMillsCount(playerColor)
    val possiblePlayerMoves = board.possibleMoves(playerColor).size
    val potentialOpponentMills = board.possibleMillsCount(playerColor)

    if (playerPieces <= 2 || possiblePlayerMoves == 0)
        return Int.MIN_VALUE

    if (opponentPieces <= 2)
        return Int.MAX_VALUE

    evaluation += if (playerPieces < 4)
        100 * possiblePlayerMills + 200 * potentialOpponentMills
    else
        200 * possiblePlayerMills + 100 * potentialOpponentMills

    evaluation -= 25 * possiblePlayerMoves
    evaluation += 50 * (opponentPieces - playerPieces)

    return evaluation
}

fun drawCheck(board: Board, userColor: GameColor, aiColor: GameColor) : Boolean {
//    val userPossibleMoves = possibleMoves(board, userColor)
//    val aiPossibleMoves = possibleMoves(board, userColor)
    return false
}

fun oppositeColor(color: GameColor): GameColor {
    return when (color) {
        GameColor.B -> GameColor.W
        GameColor.W -> GameColor.B
        else -> throw IllegalArgumentException("Невозможно получить цвет противника")
    }
}