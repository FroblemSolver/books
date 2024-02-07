package `1주차`.jaewon

/**
 * - sequence: 필요할 때 마다 하나씩 값을 계산하는 지연(lazy) 방식을 사용
 */
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