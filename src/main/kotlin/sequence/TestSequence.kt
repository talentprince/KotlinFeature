package sequence

/**
 * Iterator & Sequence
 */
fun main() {
    val listOfSongs = listOf(
        Song("冰雨", "刘德华"),
        Song("白玫瑰", "陈奕迅"),
        Song("惊雷", "六道"),
        Song("浮夸", "陈奕迅"),
        Song("无所谓", "杨坤")
    )

    listOfSongs.filter {
        println("filter $it")
        it.author == "陈奕迅"
    }.map { println("map $it") }.take(1)

    listOfSongs.asSequence().filter {
        println("filter $it")
        it.author == "陈奕迅"
    }.map { println("map $it") }.take(1).toList()
}

data class Song(val name: String, val author: String)