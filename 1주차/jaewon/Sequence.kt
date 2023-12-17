package `1주차`.jaewon

fun main() {
    interviewRacer().take(10).forEach {
        println(it)
    }
}

private fun interviewRacer() = sequence {
    var place = 1
    while (true) {
        yield("$place 등 한 것에 대해 기쁩니다")
        place += 1
    }
}