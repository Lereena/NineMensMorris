package ninemensmorris

import platform.posix.exit

typealias Board = Array<GameColor>

class Game(first: Player) {
    var board: Board = Array(24) { GameColor.F }
    private val userColor: GameColor = if (first == Player.USER) GameColor.W else GameColor.B
    private val aiColor: GameColor = if (first == Player.AI) GameColor.W else GameColor.B
    private var turn: Player = first
    private var nextMovePoint = 0
    private val depth = 3
    private val alpha = Int.MIN_VALUE;
    private val beta = Int.MAX_VALUE

    fun start() {
        println("Первая стадия: начальная расстановка")
        for (step in 1..9) {
            if (turn == Player.USER) {
                val userStep = firstStageUserStep()
                println("$userStep")
                printBoard(board)
                val aiStep = firstStageAIStep(step)
                printErr("$aiStep")
                println("$aiStep")
                printBoard(board)
            } else {
                val aiStep = firstStageAIStep(step)
                println("$aiStep")
                printErr("$aiStep")
                printBoard(board)
                val userStep = firstStageUserStep()
                println("$userStep")
                printBoard(board)
            }
        }

        println("Вторая стадия: движение")
        while (true) {
            if (turn == Player.AI) {
                val aiStep = secondStageAIStep()
                println("${aiStep.first} ${aiStep.second}")
                printErr("${aiStep.first} ${aiStep.second}")
                printBoard(board)
                evaluateState()
                val userStep = secondStageUserStep()
                println("${userStep.first} ${userStep.second}")
                printErr("${userStep.first} ${userStep.second}")
                printBoard(board)
            } else {
                val userStep = secondStageUserStep()
                println("${userStep.first} ${userStep.second}")
                printErr("${userStep.first} ${userStep.second}")
                printBoard(board)
                evaluateState()
                val aiStep = secondStageAIStep()
                println("${aiStep.first} ${aiStep.second}")
                printErr("${aiStep.first} ${aiStep.second}")
                printBoard(board)
            }
            evaluateState()
        }
    }

    private fun firstStageUserStep(): Int {
        println("Ход пользователя: ")
        val position = validUserTo(board)
        board[position] = userColor
        return position
    }

    private fun secondStageUserStep(): Pair<Int, Int> {
        println("Ход пользователя (два числа через пробел): ")
        val step = validUserStep2(board, userColor)
        val fromPosition = step.first
        val toPosition = step.second
        board[fromPosition] = GameColor.F
        board[toPosition] = userColor
        return step
    }

    private fun firstStageAIStep(step: Int): Int {
        println("Ход компьютера: ")
        if (step == 1) {
            nextMovePoint = randomMove(board, aiColor)
            return nextMovePoint
        }

        val point = nextMovePoint
        if (!closeMill(point, board)) {
            val pointForMill = pointForMill(board, aiColor, point)
            if (pointForMill != -1) {
                board[pointForMill] = aiColor
                nextMovePoint = pointForMill
                return nextMovePoint
            }
        }

        val neighbors = neighbors[point]
        for (neighbor in neighbors)
            if (freePlace(board, neighbor)) {
                board[neighbor] = aiColor
                nextMovePoint = neighbor
                return nextMovePoint
            }

        nextMovePoint = randomMove(board, aiColor)
        return nextMovePoint
    }

    private fun secondStageAIStep(): Pair<Int, Int> {
        print("Ход компьютера: ")
        val move: Pair<Int, Int>
        val evaluation = alphaBetaPruning(true, board, aiColor, userColor, depth, alpha, beta)
        move = subtractBoards(board, evaluation.board)
        board = evaluation.board
        return move
    }

    private fun evaluateState() {
        val user = board.count { x -> x == userColor }
        val ai = board.count { x -> x == aiColor }
        // TODO проверить на ничью
        if (user <= 2) {
            println("Компьютер выиграл")
            exit(0)
        }
        if (ai <= 2) {
            println("Вы выиграли")
            exit(3)
        }
    }
}

enum class Player { AI, USER }
enum class GameColor { B, W, F }

class Evaluation(var evaluator: Int = 0, var board: Board = Array(0) { GameColor.F })
