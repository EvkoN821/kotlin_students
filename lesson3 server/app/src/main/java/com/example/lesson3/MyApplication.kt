package com.example.lesson3

import android.app.Application
import android.content.Context
import com.example.lesson3.repository.AppRepository

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppRepository.getInstance().loadData()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        val context
            get()= applicationContext()

        private fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
    
}