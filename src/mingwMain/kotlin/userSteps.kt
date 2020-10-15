package ninemensmorris

fun readPosition(): Int {
    var position: Int?
    while (true) {
        val line = readLine()!!.trim()
        if (line == "") {
            println("Введите число от 0 до 23")
            continue
        }
        position = line.toInt()
        if (position !in 0..23) {
            println("Введите число от 0 до 23")
            continue
        }
        return position
    }
}

fun validUserStep(board: Board, userColor: GameColor): Pair<Int, Int> {
    var first: Int?
    var second: Int?
    while (true) {
        val line = readLine()!!.trim().split(' ')
        if (line.size != 2) {
            println("Введите два числа от 0 до 23")
            continue
        }
        first = line[0].toInt()
        second = line[1].toInt()
        if (first !in 0..23 || second !in 0..23) {
            println("Введите два числа от 0 до 23")
            continue
        }
        if (board[first] != userColor) {
            println("На этой позиции нет вашей фишки")
            continue
        }
        if (!freePlace(board, second)) {
            println("Это место уже занято")
            continue
        }
        if (second == first) {
            println("Фишка уже на этой позиции")
            continue
        }
        // TODO добавить проверку на движение по линиям

        return Pair(first, second)
    }
}

fun validUserFrom(board: Board, userColor: GameColor): Int {
    while (true) {
        val position = readPosition()
        if (board[position] != userColor) {
            println("На этой позиции нет вашей фишки")
            continue
        }
        return position
    }
}

fun validUserTo(board: Board, startPosition: Int? = null): Int {
    while (true) {
        val position = readPosition()
        if (!freePlace(board, position)) {
            println("Это место уже занято")
            continue
        }
        if (startPosition != null && position == startPosition) {
            println("Фишка уже на этой позиции")
            continue
        }
        // TODO добавить проверку на движение по линиям
        return position
    }
}

