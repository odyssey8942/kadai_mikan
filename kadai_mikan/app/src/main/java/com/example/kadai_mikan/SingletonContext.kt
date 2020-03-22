package com.example.kadai_mikan

import android.app.Application
import android.content.Context

/**
 * contextをどこからでも呼べるようにしたクラス
 */
class SingletonContext : Application() {

    // 他にやることないなら消してもいい
    override fun onCreate() {
        super.onCreate()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: SingletonContext? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}