package ninemensmorris

import kotlin.system.exitProcess

enum class GameColor { B, W, F }
enum class Player { AI, USER }

class Game(val first: Player) {
    var board: Board = Board(Array(24) { GameColor.F })
    private val userColor = if (first == Player.USER) GameColor.W else GameColor.B
    private val aiColor = if (first == Player.AI) GameColor.W else GameColor.B
    private val memorisedPositions: HashMap<Int, Int> = HashMap()
    private var lastAiMove: Int = 0

    fun start() {
        println("Первая стадия: начальная расстановка")
        board.print()
        for (step in 1..9) {
            if (first == Player.AI)
                aiUserFirstStageStep(step)
            else
                userAiFirstStageStep(step)
        }

        println("Вторая стадия: движение")
        while (true) {
            if (first == Player.AI)
                aiUserSecondStageStep()
            else
                userAiSecondStageStep()
        }
    }

    private fun aiUserFirstStageStep(step: Int) {
        val aiStep = firstStageAiStep(board, step, lastAiMove, aiColor)
        lastAiMove = aiStep
        println("$aiStep")
        printErr("$aiStep")
        board.print()
        val userStep = firstStageUserStep(board, userColor)
        println("$userStep")
        board.print()
    }

    private fun userAiFirstStageStep(step: Int) {
        val userStep = firstStageUserStep(board, userColor)
        println("$userStep")
        board.print()
        val aiStep = firstStageAiStep(board, step, lastAiMove, aiColor)
        lastAiMove = aiStep
        println("$aiStep")
        printErr("$aiStep")
        board.print()
    }

    private fun aiUserSecondStageStep() {
        evaluateState()
        val aiStep = secondStageAiStep(board, aiColor)
        applyMove(aiStep)
        printErr("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
        println("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
        board.print()
        memorisePosition(board)
        evaluateState()

        val userStep = secondStageUserStep(board, userColor)
        applyMove(userStep)
        println("${userStep.first} ${userStep.second} ${if (userStep.third != null) userStep.third.toString() else ""}")
        board.print()
        memorisePosition(board)
        evaluateState()
    }

    private fun userAiSecondStageStep() {
        evaluateState()
        val userStep = secondStageUserStep(board, userColor)
        applyMove(userStep)
        println("${userStep.first} ${userStep.second} ${if (userStep.third != null) userStep.third.toString() else ""}")
        board.print()
        memorisePosition(board)
        evaluateState()
        val aiStep = secondStageAiStep(board, aiColor)
        applyMove(aiStep)
        printErr("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
        println("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
        board.print()
        memorisePosition(board)
        evaluateState()
    }

    private fun applyMove(move: Triple<Int, Int, Int?>) {
        board[move.second] = board[move.first]
        board[move.first] = GameColor.F
        if (move.third != null)
            board[move.third!!] = GameColor.F
    }

    private fun memorisePosition(position: Board) {
        val hash = position.hashCode()

        if (memorisedPositions.containsKey(hash)) {
            memorisedPositions[hash] = memorisedPositions[hash]!! + 1
            if (memorisedPositions[hash] == 2) {
                println("Ничья")
                exitProcess(4)
            }
        } else
            memorisedPositions[hash] = 1
    }

    private fun evaluateState() {
        val evaluation = gameState()
        if (!evaluation.first)
            return

        when (evaluation.second) {
            0 -> println("Бот выиграл")
            3 -> println("Вы выиграли")
            4 -> println("Ничья")
        }
        exitProcess(evaluation.second!!)
    }

    fun gameState(): Pair<Boolean, Int?> {
        val user = board.count(userColor)
        val ai = board.count(aiColor)

        if (user <= 2)
            return Pair(true, 0)
        if (ai <= 2)
            return Pair(true, 3)

        return Pair(false, null)
    }
}
