package ninemensmorris

fun main(args: Array<String>) {
    //println("Кто ходит первым?")
    /*var first: String
    do {
        println("0: компьютер, 1: пользователь")
        first = readLine()!!.trim()
    } while (first != "0" && first != "1")
*/
    val first = args[0]
    val game = Game(if (first == "0") Player.AI else Player.USER)
    game.start()
}