package `1주차`.jaewon

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * - 코루틴을 중단할 때 Continuation 객체를 반환
 * -- Continuation 객체를 통해 멈췄던 곳에서 다시 코루틴 실행 가능
 * --- 스레드에는 이런 기능이 없음(코루틴이 더욱 강력한 이유)
 */

suspend fun main() {
    print(requestCarListApi().joinToString())
}

suspend fun requestCarListApi() = suspendCoroutine { continuation ->
    continuation.resume(listOf("쏘나타", "스포티지", "투산"))
}