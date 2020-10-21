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
        val aiStep = secondStageAiStep(board, aiColor)
        printErr("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
        println("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
//        printErr("${aiStep.first} ${aiStep.second}")
        board.print()
        memorisePosition(board)
        evaluateState()

        val userStep = secondStageUserStep(board, userColor)
        println("${userStep.first} ${userStep.second} ${if (userStep.third != null) userStep.third.toString() else ""}")
//        printErr("${userStep.first} ${userStep.second}")
        board.print()
        memorisePosition(board)
        evaluateState()
    }

    private fun userAiSecondStageStep() {
        val userStep = secondStageUserStep(board, userColor)
        println("${userStep.first} ${userStep.second} ${if (userStep.third != null) userStep.third.toString() else ""}")
//        printErr("${userStep.first} ${userStep.second}")
        board.print()
        memorisePosition(board)
        evaluateState()
        val aiStep = secondStageAiStep(board, aiColor)
        printErr("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
        println("${aiStep.first} ${aiStep.second} ${if (aiStep.third != null) aiStep.third.toString() else ""}")
//        printErr("${aiStep.first} ${aiStep.second}")
        board.print()
        memorisePosition(board)
        evaluateState()
    }

    private fun memorisePosition(position: Board) {
        val hash = position.hashCode()

        if (memorisedPositions.containsKey(hash))
            memorisedPositions[hash] = memorisedPositions[hash]!! + 1
        else
            memorisedPositions[hash] = 1
    }

    private fun evaluateState() {
        val user = board.count(userColor)
        val ai = board.count(userColor)

        if (memorisedPositions.containsValue(3)) {
            println("Ничья")
            exitProcess(4)
        }
        if (user <= 2) {
            println("Компьютер выиграл")
            exitProcess(0)
        }
        if (ai <= 2) {
            println("Вы выиграли")
            exitProcess(2)
        }
    }
}
