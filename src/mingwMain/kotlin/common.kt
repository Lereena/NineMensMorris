package ninemensmorris

fun freePlace(board: Board, position: Int): Boolean {
    return board[position] == GameColor.F
}

fun invertedBoard(board: Board): Board {
    val result = Array(24) { GameColor.F }
    for (i in result.indices)
        if (board[i] == GameColor.B)
            result[i] = GameColor.W
        else if (board[i] == GameColor.W)
            result[i] = GameColor.B

    return result
}

fun invertedBoardList(boardList: Array<Board>): Array<Board> {
    val result = Array(boardList.size) { arrayOf(GameColor.F) }
    for (i in boardList.indices)
        result[i] = invertedBoard(boardList[i].copyOf())

    return result
}

fun closeMill(point: Int, board: Board): Boolean {
    val color = board[point]
    return checkMillFormation(board, color, linesNeighbors[point])
}

fun checkMillFormation(board: Board, color: GameColor, linesNeighbors: Array<Int>): Boolean {
    return board[linesNeighbors[0]] == color && board[linesNeighbors[1]] == color
            || board[linesNeighbors[2]] == color && board[linesNeighbors[3]] == color
}

fun removePiece(board: Board, boards: ArrayList<Board>, color: GameColor): ArrayList<Board> {
    for (i in board.indices)
        if (board[i] == color)
            if (!closeMill(i, board)) {
                val newBoard = board.copyOf()
                newBoard[i] = GameColor.F
                boards.add(newBoard)
            }
    return boards
}