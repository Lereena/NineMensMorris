package ninemensmorris

fun main(args: Array<String>) {
    val first = args[0]
    val game = Game(if (first == "0") Player.AI else Player.USER)
    game.start()
}