## 코루틴 컨텍스트
* 코루틴 컨텍스트: 코루틴에 관련된 정보를 객체로 그룹화하고 전달하는 보편적인 방법
    * 코루틴의 상태 확인
    * 어떤 스레트를 선택할지 등 코루틴의 작동 방식을 정할 수 있음
* 코루틴 컨텍스트 아래에는 여러 요소들을 담을 수 있음
    * 모든 요소는 식별할 수 있는 key가 있음

### CoroutineContext에서 원소 찾기
```kotlin
fun main() {
    val ctx: CoroutineContext = CoroutineName("A name")

    val coroutineName: CoroutineName? = ctx[CoroutineName]
    
    println(coroutineName?.name) // A name
    val job: Job? = ctx[Job]
    println(job) // null
}
```
* ctx 안에 CoroutineName은 넣어뒀지만 Job은 들어가있지 않기 때문에 null이 나옴

### 컨텍스트 더하기
* 왜 정말 유용한 기능일지 고민해보고싶음
```kotlin
fun main() {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name) // Name1
    println(ctx1[Job]?.isActive) // null

    val ctx2: CoroutineContext = Job()
    println(ctx2[CoroutineName]?.name) // null
    println(ctx2[Job]?.isActive) // true

    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name) // Name1
    println(ctx3[Job]?.isActive) // true
}
```
* CoroutineName만 있는 ctx1과 Job만 있는 ctx2를 더해서 둘 다 있는 ctx3를 만들었음
* 어떤 상황에 쓸 수 있을까
    * 어떤 값을 요청하는 코루틴을 만들었을 때 둘이 합칠 수 있다면 같이 관리할 수 있어서 좋으려나

### 이외의 컨택스트 연산들
* 비어있는 컨택스트도 있을 수 있음
```kotlin
val empty: CoroutineContext = EmptyCoroutineContext
```
* 원소 제거도 가능함
```kotlin
val ctx = CoroutineName("Name1") + Job()
val ctx1 = ctx1.minusKey(CoroutineName)
println(ctx1[CoroutieName].name) // null
println(ctx[Job].isActive) // true
```
* fold 가능
```kotlin
val ctx = CoroutineName("Name1") + Job()
ctx.fold(""){ acc, element -> "$acc$element " }.also(::println) 
// CoroutineName(Name1) JobImpl{Active}@2bdl4283
```

### 코루틴 컨텍스트와 빌더
* 자식들은 부모의 코루틴 컨텍스트를 상속받음
* 자식 코루틴에서 코루틴 컨텍스트를 새로 정의하면 부모와 자식사이의 상관관계가 없어짐
* 중단 함수에서 코루틴 컨텍스트에 접근 가능함
```kotlin
suspend fun printName() {
    println(coroutineContext[CoroutineName]?.name)
}

suspend fun main() = withContextCoroutineName("Outer") {
    printName() // Outer
    launch(CoroutineName("Inner")) {
        printName() // Inner
    }
    printName() // Outer
}
```
* 컨텍스트를 커스텀해서 만들 수 있음
```kotlin
class MyCustomContext : CoroutineContext.Element {
     override val key: CoroutineContext.Key<*> = Key

     companion object Key : CoroutineContext.Key<MyCustomContext>
}
```