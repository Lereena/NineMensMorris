package ninemensmorris

import kotlin.system.exitProcess

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
    private var pruned = 0
    private var reachedStates = 0

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
                secondStageAIStep()
//                printBoard(board)
            } else {
                secondStageUserStep()
//                printBoard(board)
                when {
                    evaluateState() == aiColor -> println("Компьютер выиграл")
                    evaluateState() == userColor -> println("Вы выиграли")
                }
            }
        }
    }

    private fun firstStageUserStep(): Int {
        println("Ход пользователя: ")
        val position = validUserTo(board)
        board[position] = userColor
//        turn = Player.AI
        return position
    }

    private fun secondStageUserStep() {
        println("Ход пользователя (два числа через пробел): ")
        val step = validUserStep(board, userColor)
        val fromPosition = step.first
        val toPosition = step.second
        board[fromPosition] = GameColor.F
        board[toPosition] = userColor
//        turn = Player.AI
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
//        turn = Player.USER
        return nextMovePoint
    }

//    private fun firstStageAIStep(step: Int): Int {
//        println("Ход компьютера: ")
//        if (step == 1) {
//            nextMovePoint = randomMove(board, aiColor)
//        } else {
//            val point = nextMovePoint
//            if (!closeMill(point, board)) {
//                val pointForMill = pointForMill(board, aiColor, point)
//                if (pointForMill != -1) {
//                    board[pointForMill] = aiColor
//                    nextMovePoint = pointForMill
//                } else {
//                    val neighbors = neighbors[point]
//                    for (neighbor in neighbors)
//                        if (freePlace(board, neighbor)) {
//                            board[neighbor] = aiColor
//                            nextMovePoint = neighbor
//                            break
//                        } else if (neighbor == neighbors[neighbors.size - 1])
//                            nextMovePoint = randomMove(board, aiColor)
//                }
//            }
//        }
//
//        turn = Player.USER
//        return nextMovePoint
//    }

    private fun secondStageAIStep() {
        print("Ход компьютера: ")
        val move: Pair<Int, Int>
        val evaluation = alphaBetaPruning(false, board, aiColor, userColor, depth, alpha, beta)
        if (evaluation.evaluator == Int.MIN_VALUE) {
            print("Компьютер выиграл")
            exitProcess(0)
        } else {
            move = subtractBoards(board, evaluation.board)
            board = evaluation.board
        }
        println("${move.first} ${move.second}")
//        turn = Player.USER
        printErr("${move.first} ${move.second}")
    }

    private fun evaluateState(): GameColor {
        val white = board.count { x -> x == GameColor.W }
        val black = board.count { x -> x == GameColor.B }
        if (white <= 2)
            return GameColor.B
        if (black <= 2)
            return GameColor.W
        return GameColor.F
    }
}

enum class Player { AI, USER }
enum class GameColor { B, W, F }

class Evaluation(var evaluator: Int = 0, var board: Board = Array(0) { GameColor.F })
