package com.jinss.android.test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Before
    fun setup() {
        println("setup")
    }

    @Test
    fun `Should do my test`() = runBlocking {
        val accountManager = AccountManager()
        accountManager.updateProfile(5000)
        println("Done should do my test")
    }

    @Test
    fun `My coroutine study test`() = runBlocking {
        launch {
            println("hello")  // 1
        }
        launch {
            delay(1000)
            println("my world")  // 3
            delay (3000)
            println("keep going") // 5
        }

        coroutineScope { // coroutineScope 는 {...}를 실행하는 CoroutineScope를 그대로 가져와서 실행하겠다 라는말임. CoroutineScope(coroutineContext).launch {... 와는 조금 다르다
            // CoroutineScope 는 말그대로 새로운 CoroutineScope를 만드는 것임.

            println("I want to know coroutine exactly") // 2
            launch {
                delay(5000)
                println("Created form run blocking") // 6
            }
            launch {
                delay(2000)
                println("4444") // 4
            }
        } // Wait for done coroutineScope // coroutineScope {...} 까지는 마치 async, await 한 동작 같다.
        // 중요 ★★★★★★★★★★★

        // start
        // 6 or 7
        launch {
            println("hi")
        }
        println("end of the line")
    }

    val myCoroutineContext = SupervisorJob() + Dispatchers.IO

    @Test
    fun `My coroutine study test2`() = runBlocking {
        val myScope = SupervisorJob() + Dispatchers.IO

        launch {
            println("hello")  // 1
            CoroutineScope(myCoroutineContext).launch {
                delay(5000)
                println("world") // <- 이게 안나온다 ~~~ done까지 뜨면서 끝나버려서
                // CoroutineScope와(빌더) coroutineScope(실행시키는 주체의 코루틴 스코프를 이용하겠다)는 다르다.
                // 코루틴 스코프가 달라지면 따로 백그라운드에서 돌기때문에 Runblocking이 기다려주질못한다.
            }
            println("hello222")  // 2
            launch {
                delay(100)
                println("hello~~~~~~~~") //3
            }
        }
        println("done")
    }

    @Test
    fun `My coroutine study test3`() = runBlocking {
        val myScope = SupervisorJob() + Dispatchers.IO
        var job: Job?  = CoroutineScope(myCoroutineContext).launch(start = CoroutineStart.LAZY) {
            delay(5000)
            println("world")
        }
        launch {
            println("hello")  // 1
            // job?.join()
        }
        job?.join() // 기다려준다
        println("done")
    }

    @Test
    fun `My coroutine study test4`() = runBlocking {
        var job: Job?  = CoroutineScope(myCoroutineContext).launch(start = CoroutineStart.LAZY) {
            delay(5000)
            println("world")
        }
        launch {
            delay(1000)
            println("hello")  // 1
            job?.join() // 기다려준다.
        }
        println("done")
    }

    @Test
    fun `My coroutine study test4_2`() = runBlocking {
        var job: Job?  = CoroutineScope(myCoroutineContext).launch(start = CoroutineStart.LAZY) {
            launch {
                println("11111")
                launch {
                    println("2222")
                }
            }
            delay(5000)
            println("world")
            launch {
                delay(5000)
                println("pretty late")
            }
            launch {
                println("33333")
            }
        }
        launch {
            delay(1000)
            println("hello I'll wait~~~")  // 1
            job?.join() // 기다려준다. job의 모든 CoroutineScope자식들로 실행된것들이 끝날때까지 기다려준다.
        }
        println("done")
    }

    @Test
    fun `My coroutine study test4_3`() = runBlocking {
        var job: Job?  = CoroutineScope(myCoroutineContext).launch(start = CoroutineStart.LAZY) {
            launch {
                println("11111")
                launch {
                    println("2222")
                }
            }
            delay(5000)
            println("world")
            launch {
                delay(5000)
                println("pretty late")
            }
            launch {
                println("33333")
            }
        }
        launch {
            delay(1000)
            println("hello I'll wait~~~")  // 1
            job?.start() // 실행되다가 전부 실행안되고 끝나게 되버림. 코루틴스코프가 달라져버리니까 그렇다. 아무리 runBlocking 이라도 더이상 다른 코루틴스코프까지는 기다려주지 못한다.
        }
        println("done")
    }

    // 1
    // done
    // 2
    @Test
    fun `My coroutine study runblonckingTest study`() = runBlockingTest {
        launch {
            println(1)   // executes eagerly when foo() is called due to runBlockingTest
            delay(1_000) // suspends until time is advanced by at least 1_000
            println(2)   // executes after advanceTimeBy(1_000)
        }
        // the coroutine launched by foo has not completed here, it is suspended waiting for delay(1_000)
        // advanceTimeBy(1_000) // progress time, this will cause the delay to resume
        // the coroutine launched by foo has completed here
        println("done")
    }

    // 1
    // 2
    // done
    @Test
    fun `My coroutine study runblonckingTest study2`() = runBlockingTest {
        foo()
        // the coroutine launched by foo has not completed here, it is suspended waiting for delay(1_000)
        advanceTimeBy(1_000) // progress time, this will cause the delay to resume
        // the coroutine launched by foo has completed here
        println("done")
    }
    suspend fun foo() {
        coroutineScope {
            launch {
                println(1)   // executes eagerly when foo() is called due to runBlockingTest
                delay(1_000) // suspends until time is advanced by at least 1_000
                println(2)   // executes after advanceTimeBy(1_000)
            }
        } // coroutineScope 끝날때까지 await 한다~
    }

    // 아래와 같은 오류가 발생
    // kotlinx.coroutines.test.UncompletedCoroutinesError: Test finished with active jobs: ["coroutine#3":StandaloneCoroutine{Active}@6fb554cc]
    // 왜 그런걸까?
    // -> runBlockingTest 는 delay를 실제로하지않고 가상으로 생각해서 행동한다. 이런걸 eager execution 이라 부르는것 같다. 최대한 빨리 실행해버리는것.
    @Test
    fun `My coroutine study test5`() = runBlockingTest {
        var job: Job?  = CoroutineScope(myCoroutineContext).launch(start = CoroutineStart.LAZY) {
            delay(5000)
            println("world")
        }
        launch {
            println("hello")  // 1
            job?.join()
        }
        println("done")
    }

    private val testDispatcher = TestCoroutineDispatcher()
    // 위의 문제를 막기위해 dispatcher를 통일 시켜버린다.
    @Test
    fun `My coroutine study test5_1`() = testDispatcher.runBlockingTest {
        var job: Job?  = CoroutineScope(testDispatcher).launch(start = CoroutineStart.LAZY) {
            delay(5000)
            println("world")
        }
        launch {
            println("hello")  // 1
            job?.join()
        }
        advanceTimeBy(5000) // 이걸써야 실제로 클락을 이동시켜버림...
        println("done")
    }

    // 아래와 같은 오류발생!!!
    // java.lang.IllegalStateException: This job has not completed yet
    // 왜 그런걸까?
    // ->
    @Test
    fun `My coroutine study test6`() = runBlockingTest {
        var job: Job?  = CoroutineScope(myCoroutineContext).launch(start = CoroutineStart.LAZY) {
            delay(5000)
            println("world")
        }
        launch {
            println("hello")  // 1
        }
        job?.join()
        println("done")
    }

    // 아래와 같은 오류발생!!!
    // java.lang.IllegalStateException: This job has not completed yet
    // 왜 그런걸까?
    // -> testDispatcher 로 해결가능하다!!!
    @Test
    fun `My coroutine study test6_1`() = testDispatcher.runBlockingTest {
        var job: Job?  = CoroutineScope(testDispatcher).launch(start = CoroutineStart.LAZY) {
            delay(5000)
            println("world")
        }
        launch {
            println("hello")  // 1
        }
        job?.join()
        println("done")
    }

    // 디스채펴만 같다고 해서 같은 코루틴스코프로 잡히게 될까???
    // 같이 종료가 가능할까??? -> 아니다....ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ~~~~
    val myCoroutineContext2 = SupervisorJob() + Dispatchers.IO
    @Test
    fun `My coroutine study test7`() = runBlocking(myCoroutineContext2) {
        launch {
            println("hello")  // 1
            CoroutineScope(myCoroutineContext2).launch {
                delay(5000)
                println("world")
            }
        }
        println("done")
    }

    // runBlocking이 자식들을 전부 기다려주니깐 실행가능
    @Test
    fun `My coroutine study test8`() = runBlocking {
        launch {
            println("hello")  // 2
            launch {
                println("world") // 3
                launch {
                    println("how are you") // 4
                }
            }
        }
        println("done") // 1
    }

    // runBlocking 이든 coroutineScope든 자식들을 전부 기다려주긴하지만, 다른자식이면 못기다림.
    // runBlocking안에서 또다른 CoroutineScope이 생성되었지만 그놈은 기다려줄수가없다.
    @Test
    fun `My coroutine study test9`() = runBlocking {
        val myCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        myCoroutineScope.launch {
            println("hello")  // 2
            launch {
                println("world") // 실행안됨
                launch {
                    println("how are you") // 실행안됨
                }
            }
        }
        println("done") // 1
    }

    // 다만 myCoroutineScope 안에서 실행된 자식들은 부모가 전부 기다려주긴하는데, 그래서 자식코루틴을 컨트롤할수있긴함.
    // 현재 스레드를 blocking 시키진않으므로 다른 스레드도 실행할 기회를 얻어서 실행된다.
    @Test
    fun `My coroutine study test10`() = runBlocking {
        val myCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        myCoroutineScope.launch {
            println("hello")  // 1
            launch {
                println("world") // 2
                launch {
                    println("how are you") // 4 순서보장이되진않음
                }
            }
        }
        myCoroutineScope.launch {
            println("hello2~~~~~~~~") // 3
            launch {
                println("hello2~~~222222") //6 순서보장이 되진않네 ㅎㅎ
            }
        }
        myCoroutineScope.launch {
            println("hello3~~~~~~~~") // 5
        }
        delay(5000) // 딜레이를줘서 myCoroutineScope 안에놈들이 실행되는거 수동으로 기다리게끔함.
        println("done") // 4
    }

    @Test
    fun `My coroutine study test11`() = runBlocking {
        val myCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        myCoroutineScope.launch {
            println("hello")  // 1
            launch {
                println("world") // 2
                launch {
                    println("how are you") // 4 순서보장이되진않음 ㅎㅎ
                }
            }
        }
        myCoroutineScope.async {
            println("hello2~~~~~~~~") // 3
            launch {
                    println("hello2~~~222222") // 6
                launch {
                    println("hello2~~~333333") // 7
                }
            }
            launch {
                println("hello2~~~~~4") // 5
            }
        }.await() //여기까진 기다려줌!! join이나 다름없으니깐 그런듯.

        myCoroutineScope.launch {
            println("hello3~~~~~~~~") // 9 순서보장이되는건아님
        }
        println("done") // 8
    }


    // coroutineScope대신 runblocking을 쓰게되면 다른 쓰레드를 block 하기때문에
    // 그 코루틴과 같은 스코프의 자식들이 전부끝나야 실행이된다.
    @Test
    fun `My coroutine study test12`() = runBlocking {
        val myCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        runBlocking {
            println("hello")  // 1
            launch {
                println("world") // 2
                launch {
                    println("how are you") // 3
                }
            }
        }
        runBlocking {
            println("hello2~~~~~~~~") // 4
            launch {
                println("hello2~~~222222") // 5ㅎ
            }
        }
        myCoroutineScope.launch {
            println("hello3~~~~~~~~") // 6 이건 순서보장안되지 ㅎㅎ
        }
        println("done") // 5
    }
}