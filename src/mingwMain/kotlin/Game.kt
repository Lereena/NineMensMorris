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
                printBoard(board)
                firstStageUserStep()
                printBoard(board)
                val aiStep = firstStageAIStep(step)
                println(" $aiStep")
            } else {
                printBoard(board)
                val aiStep = firstStageAIStep(step)
                println(" $aiStep")
                printBoard(board)
                firstStageUserStep()
            }
        }

        println("Вторая стадия: движение")
        while (true) {
            if (turn == Player.AI) {
                printBoard(board)
                secondStageAIStep()
            } else {
                printBoard(board)
                secondStageUserStep()
                when {
                    evaluateState() == aiColor -> println("Компьютер выиграл")
                    evaluateState() == userColor -> println("Вы выиграли")
                }
            }
        }
    }

    private fun firstStageUserStep(): Int {
        print("Ход пользователя:")
        val position = validUserTo(board)
        board[position] = userColor
        turn = Player.AI
        return position
    }

    private fun secondStageUserStep() {
        println("Ход пользователя (два числа на разных строках):")
        val fromPosition = validUserFrom(board, userColor)
        val toPosition = validUserTo(board, fromPosition)
        board[fromPosition] = GameColor.F
        board[toPosition] = userColor
        turn = Player.AI
    }

    private fun firstStageAIStep(step: Int): Int {
        print("Ход компьютера:")
        if (step == 0) {
            nextMovePoint = randomMove(board, aiColor)
        } else {
            val point = nextMovePoint
            if (!closeMill(point, board)) {
                val pointForMill = pointForMill(board, aiColor, point)
                if (pointForMill != -1) {
                    board[pointForMill] = aiColor
                    nextMovePoint = pointForMill
                }
                for (neighbor in neighbors[point])
                    if (board[neighbor] == GameColor.F) {
                        board[neighbor] = aiColor
                        nextMovePoint = neighbor
                    }
                nextMovePoint = randomMove(board, aiColor)
            }
        }

        turn = Player.USER
        return nextMovePoint
    }

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
        turn = Player.USER
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
