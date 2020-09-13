package com.example.musicplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.example.musicplayer.utility.Constants

class AudioFocusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            val intent = Intent(context, BackgroundPlayerService::class.java)
            intent.putExtra(Constants.NOTIFICATION_INTENT, Constants.AUDIO_FOCUS_CHANGED)
            context.startService(intent)
        }
    }
}