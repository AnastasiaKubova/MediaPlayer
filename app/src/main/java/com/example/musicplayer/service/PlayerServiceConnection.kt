package com.example.musicplayer.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

object PlayerServiceConnection : ServiceConnection {

    var listener: PlayerServiceConnectionListener? = null
    var mService: BackgroundPlayerService? = null
    var mBound = false

    override fun onServiceDisconnected(className: ComponentName?) {
        mBound = false
        listener?.onServiceDisconnectedListener()
    }

    override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
        val binder = service as (BackgroundPlayerService.LocalBinder)
        mService = binder.service
        mBound = true
        listener?.onServiceConnectedListener()
    }

    interface PlayerServiceConnectionListener {
        fun onServiceConnectedListener()
        fun onServiceDisconnectedListener()
    }
}