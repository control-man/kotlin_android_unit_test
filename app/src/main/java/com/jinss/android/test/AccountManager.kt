package com.jinss.android.test

import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class AccountManager : CoroutineScope {

    val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun updateProfile(time: Long = 5000, dispatcher: CoroutineDispatcher = Dispatchers.IO ) = launch (dispatcher) {
        println("updateProfile")
        delay(1000)
        queryProfile()
        delay(1000)
        try {
            withTimeout(5000) {
                print("value ${queryGuidByUsingService(time).await()}")
            }
        } catch (e: Exception) {
            println("time out exception $e")
        }
    }

    fun queryProfile() {
        println("queryProfile")
    }

    fun queryGuidByUsingService() = async {
        println("queryGuidByUsingService")
        // Something long work
        delay(5000)
        println("queryGuidByUsingService done!!!!!")
        "MY_GUID"
    }

    fun queryGuidByUsingService(time: Long) = async {
        println("queryGuidByUsingService")
        println ("delay time : $time")
        // Something long work
        delay(time)
        println("queryGuidByUsingService done!!!!!")
        "MY_GUID"
    }

    fun updateProfile2(time: Long = 5000, dispatcher: CoroutineDispatcher = Dispatchers.IO) = launch(dispatcher) {
        println("updateProfile")
        delay(1000)
        queryProfile()
        delay(1000)
        try {
            withTimeout(5000) {
                queryGuidByUsingService2(time).await()
            }
        } catch (e: Exception) {
            println("time out exception $e")
        }
    }

    fun queryGuidByUsingService2(time: Long) = CoroutineScope(SupervisorJob() + Dispatchers.Default).async {
        println("queryGuidByUsingService")
        while(true) {}
        println("queryGuidByUsingService done")
        "MY_GUID"
    }

    companion object {
        private const val TIME_OUT_LIMIT = 5000 // 5sec
    }
}