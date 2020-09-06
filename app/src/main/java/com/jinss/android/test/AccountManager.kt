package com.jinss.android.test

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AccountManager : CoroutineScope {

    val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun updateProfile() = launch {
        println("updateProfile")
        delay(5000)
        queryProfile()
        delay(5000)
        queryGuidByUsingService().await()
    }

    fun queryProfile() {
        println("queryProfile")
    }

    fun queryGuidByUsingService() = async {
        println("queryGuidByUsingService")
        delay(5000)
        print("queryGuidByUsingService done")
        "MY_GUID"
    }

    companion object {
        private const val TIME_OUT_LIMIT = 5000 // 5sec
    }
}