## Job
* 코루틴을 취소하고, 상태를 파악하는등 다양하게 사용됨
* 수명을 가지고 있음
    * New: 지연 시작되는 코루틴만 New 상태임
    * Active: 대부분의 코루틴이 시작되는 상태. 코루틴을 실행하면 이 상태가 됨 이 상태에서 잡이 실행되고 코루틴은 잡을 수행함
        * isActive로 확인 가능
    * Completing: 실행이 완료 되었을 때의 상태, 이 상태에서 자식들을 기다림
    * Completed: 자식들까지 모두 완료된 상태, 마지막 상태임
        * isCompleted로 확인 가능
    * Cancelling: 실행도중 (Active, Completing) 실패하거나 취소하면 이 상태가 됨
    * Cancelled: 취소된 이후(Canceling) 후처리를 할 수 있음. 후처리까지 완료된 상태
        * isCancelled로 확인 가능

### 코루틴 빌더는 부모 잡을 기초로 자신의 잡을 만듦
* 대부분의 코루틴 빌터는 잡을 반환함
    * async{}의 Deferred<T>도 Job을 상속함


```kotlin
fun main(): Unit = runBlocking {
    val name = CoroutineName("Some name")
    val job = Job()

    launch(name + job) {
        val childName = coroutineContext[CoroutineName]
        println(childName == name) // true
        val childJob = coroutineContext[Job]
        println(childJob == job) // false
        println(childJob == job.children.first()) // true
    }
}
```
* Job은 코루틴이 부모의 것을 상속하지 않는 유일한 요소임
    * coroutineName은 그래도 사용하지만 Job은 코루틴 빌더가 새로 만들기 때문에 false가 나옴


### 자식 기다리기
* Job을 기다릴때는 join(), joinAll()함수를 사용함
    * Job이 Completed, Cancelled상태가 될 때 까지 기다리는 suspend 함수

```kotlin
fun main(): Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        println("Test1")
    }
    val job2 = launch {
        delay(2000)
        println("Test2")
    }

    job1.join()
    job2.join()
    println("All tests are done")
}
// (1초 후)
// Test1
// (2초 후)
// Text2
// All tests are done
```

* join으로 기다리는 job1과 job2가 끝난 후에 마지막 "All tests are done"이 호출됨

### 잡 팩토리 함수
* Job() <- 잡 팩토리 함수
```kotlin
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }
    job.join() // 여기서 영원히 대기하게 됨
    println("Will not be printed")
}
```
* 이렇게 되면 팩토리 함수로 만든 Job과 별개로 Text1의 코루틴과 Text2의 코루틴이 동작하기 때문에(Job은 부모의 것을 상속하지 않음) 코루틴이 완료되어도 상위에 있는 job이 Completed가 되지 않음
```kotlin
job.children.forEach{ it.join() } // 이렇게 바꾸는게 바람직함
```

### 유용한 함수
* complete() : Boolean - 잡을 완료하는데 사용. 잡이 완료되면 true를 반환, 그렇지 않은경우 (이미 잡이 완료되었을 때) false를 반환
    * complete()를 호출한 Job에서 새로운 코루틴이 시작될 수 없음
* completeExceptionally(exception: Throwable) : Boolean - 인자로 받은 예외로 잡을 완료시킴