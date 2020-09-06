package com.jinss.android.test

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class AccountTest {

    private val testDispatcher = TestCoroutineDispatcher()

    lateinit var accountManager: AccountManager

    @Before
    fun setup() {
        print ("setup")
        Dispatchers.setMain(testDispatcher)
        accountManager = AccountManager()
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    // 되긴 되지만 시간이 delay만큼 걸리게된다.
    @Test
    fun `My fault test case`() = runBlocking {
        accountManager.updateProfile(5000).join()
        println("done my fault test case")
    }

    // Timeout Test
    @Test
    fun `My fault test case2`() = runBlocking {
        accountManager.updateProfile(10000).join()
        println("done my fault test case2")
    }

    // Timeout Test
    @Test
    fun `My fault test case2_2`() = runBlocking {
        accountManager.updateProfile2(10000).join()
        println("done my fault test case2")
    }

    // Fail
    // This job has not completed yet
    @Test
    fun `My fault test case3`() = runBlockingTest {
        accountManager.updateProfile(500).join()
        println("done my fault test case3")
    }

    // join을 쓸필요도 없게된다. runBlockingTest 가 모두 기다려준다.
    // 문제는 자꾸 항상! TimeOut이 발생한다 ㅡㅡ..
    @Test
    fun `Should update profile using runBlockingTest`() = testDispatcher.runBlockingTest {
        accountManager.updateProfile(1000, testDispatcher)
    }

    // delay가 있다면 awiat을 무조건 오랜시간으로 보는것같은데...?
    // 아래도 exception 발생
    @Test(expected = TimeoutCancellationException::class)
    fun `Should update profile using runBlockingTest2`() = testDispatcher.runBlockingTest {
        accountManager.updateProfile(1, testDispatcher)
    }

    // runBlocking 은 현실 반영이 되버려서 실제로 delay까지하게됨.
    @Test
    fun `Should update profile using runBlockingTest2_1`() = runBlocking {
        accountManager.updateProfile(1).join()
        println("done")
    }

    fun myMockFunc() = GlobalScope.async {
        "MY MODIFIED GUID"
    }

    // timeout이 안나게하려면?
    // async 메서드 자체를 mocking 해야하는듯?
    @Test
    fun `Should update profile using runBlockingTest2_2`() = testDispatcher.runBlockingTest {
        val accountManager = spyk(AccountManager(), recordPrivateCalls = true)
        // answers는 실행할 메서드가있으면 해주고 항상 마지막 값이 반환되게 된다.
        every {accountManager["queryGuidByUsingService"](any<Long>())} answers { myMockFunc() }
        accountManager.updateProfile(1, testDispatcher)
        println("done")
    }

    class Car {
        fun drive() = accelerate()
        private fun accelerate() = "going faster"
        fun accelerate2(v: Int) = "going faster22222"
        fun drive3(v: Int) = acc3(v)
        private fun acc3(v: Int) = "going fast 3333333333333"
    }
    @Test
    fun `Should update profile using runBlockingTest2_3`() = testDispatcher.runBlockingTest {
        val mock = spyk(Car(), recordPrivateCalls = true)
        println(mock.drive())
        every { mock["accelerate"]() } returns "going not so fast"
        println(mock.drive())
        println(mock.accelerate2(3))
        every { mock.accelerate2(any()) } returns "going not so fast3333"
        println(mock.accelerate2(3))
        println(mock.drive3(3))
        // ★★★★★★★★★★★★★★★★  any<타입>() <- 이렇게 사용해야한다.
        every { mock["acc3"](any<Int>()) } returns "going not so fast~~~~~~~~~~~~3333"
        println(mock.drive3(4))
    }

}