package com.example.musicplayer.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

object PlayerServiceConnection : ServiceConnection {

    val listener: MutableList<PlayerServiceConnectionListener> = mutableListOf()
    var mService: BackgroundPlayerService? = null
    var mBound = false

    override fun onServiceDisconnected(className: ComponentName?) {
        mBound = false
        listener.forEach { it.onServiceDisconnectedListener() }
    }

    override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
        val binder = service as (BackgroundPlayerService.LocalBinder)
        mService = binder.service
        mBound = true
        listener.forEach { it.onServiceConnectedListener() }
    }

    interface PlayerServiceConnectionListener {
        fun onServiceConnectedListener()
        fun onServiceDisconnectedListener()
    }
}