package `1주차`.jaewon

/**
 * continuation 객체는 함수의 상태를 저장
*/

suspend fun readBook() {
    var page = 1
}

/**
 * 위 함수가 suspend 되면 page(내부변수)도 continuation 객체에 저장됨
 *
 */