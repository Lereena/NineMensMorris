package ninemensmorris

fun firstStageUserStep(board: Board, userColor: GameColor): Int {
    println("Ход пользователя:")
    var position: Int?
    while (true) {
        val line = readLine()!!.trim()
        if (line == "") {
            println("Введите число от 0 до 23")
            continue
        }
        position = line.toInt()
        val validity = validFirstStageStep(board, position)

        if (!validity.first)
            println(validity.second)
        else {
            board[position] = userColor
            return position
        }
    }
}

fun secondStageUserStep(board: Board, userColor: GameColor): Triple<Int, Int, Int?> {
    println("Ход пользователя (два числа через пробел):")
    var first: Int?
    var second: Int?
    var third: Int? = null

    while (true) {
        val line = readLine()!!.trim().split(' ')
        if (line.size !in 2..3) {
            println("Введите два или три числа от 0 до 23")
            continue
        }

        first = line[0].toInt()
        second = line[1].toInt()
        if (line.size == 3)
            third = line[2].toInt()

        val validity = validSecondStageStep(board, Triple(first, second, third), userColor)

        if (!validity.first)
            println(validity.second)
        else
            return Triple(first, second, third)
    }
}
