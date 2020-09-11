package com.example.musicplayer

import android.app.Application
import android.content.Context

class App: Application() {

    companion object {
        var context: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}