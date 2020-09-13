package com.example.musicplayer.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.model.Track
import com.example.musicplayer.utility.Constants

class NotificationPlayerManager(var baseContext: Context) {

    /* Constants. */
    companion object {
        const val NOTIFY_ID = 100
        private const val CHANNEL_ID = "player.manager.service.CHANNEL_ID"
        private const val CHANNEL_TEXT = "player.manager.service.CHANNEL_TEXT"
        private const val SESSION_TRACK = "player.manager.service.SESSION_TRACK"
        private const val playButtonRequestCode = 0
        private const val pauseButtonRequestCode = 1
        private const val nextButtonRequestCode = 2
        private const val beforeButtonRequestCode = 3
    }

    private var notificationBuilder: NotificationCompat.Builder
    private var notificationManager: NotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var isNotificationIsShown: Boolean = false
    private var notify: Notification? = null

    init {
        notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mediaSession = MediaSessionCompat(baseContext, SESSION_TRACK)
        notificationBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
        notify = buildNotification()
    }

    fun showNotification() {
        notificationManager?.notify(NOTIFY_ID, notify)
    }

    fun buildNotification(): Notification {
        notificationBuilder

            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_launcher)

            // Apply the media style template
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1)
                    .setMediaSession(mediaSession?.sessionToken)
            )
            .color = baseContext.resources.getColor(R.color.colorPrimary)

        /* Init buttons on notification. */
        updatePauseActions()

        /* Init notification channel. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID
            val channel =
                NotificationChannel(channelId, CHANNEL_TEXT, NotificationManager.IMPORTANCE_LOW)
            notificationManager?.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        isNotificationIsShown = true
        return notificationBuilder.build()
    }

    fun clearNotification() {
        notificationManager!!.cancel(NOTIFY_ID)
    }

    fun updatePlayNotification(track: Track) {
        updateTextNotification(track)
        updatePlayActions()
    }

    fun updatePauseNotification(track: Track) {
        updateTextNotification(track)
        updatePauseActions()
    }

    private fun updateTextNotification(track: Track) {
        notificationBuilder
            .setContentTitle(track.title)
            .setContentText(track.singer)
            .setLargeIcon(track.image)

        /* Show notification. */
        if (!isNotificationIsShown) {
            return
        }
        notificationManager?.notify(NOTIFY_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun updatePlayActions() {
        notificationBuilder.mActions.clear()
        notificationBuilder.addAction(getBeforeButtonAction())
        notificationBuilder.addAction(getPauseButtonAction())
        notificationBuilder.addAction(getNextButtonAction())
        notificationBuilder.setOngoing(true)

        /* Show notification. */
        if (!isNotificationIsShown) {
            return
        }
        notificationManager?.notify(NOTIFY_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun updatePauseActions() {
        notificationBuilder.mActions.clear()
        notificationBuilder.addAction(getBeforeButtonAction())
        notificationBuilder.addAction(getPlayButtonAction())
        notificationBuilder.addAction(getNextButtonAction())
        notificationBuilder.setOngoing(false)

        /* Show notification. */
        if (!isNotificationIsShown) {
            return
        }
        notificationManager?.notify(NOTIFY_ID, notificationBuilder.build())
    }

    private fun getNextButtonAction(): NotificationCompat.Action {
        val intentNext = Intent(baseContext, BackgroundPlayerService::class.java)
        intentNext.putExtra(
            Constants.NOTIFICATION_INTENT,
            Constants.NOTIFICATION_PLAYER_NEXT_INTENT
        )
        val nextPendingIntent =
            PendingIntent.getService(baseContext, nextButtonRequestCode, intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.next_notity, baseContext.getString(R.string.next),nextPendingIntent)
    }

    private fun getBeforeButtonAction(): NotificationCompat.Action {
        val intentBefore = Intent(baseContext, BackgroundPlayerService::class.java)
        intentBefore.putExtra(
            Constants.NOTIFICATION_INTENT,
            Constants.NOTIFICATION_PLAYER_BEFORE_INTENT
        )
        val beforePendingIntent
                = PendingIntent.getService(baseContext, beforeButtonRequestCode, intentBefore, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.before_notity,baseContext.getString(R.string.previous), beforePendingIntent)
    }

    private fun getPauseButtonAction(): NotificationCompat.Action {
        val intentPause = Intent(baseContext, BackgroundPlayerService::class.java)
        intentPause.putExtra(Constants.NOTIFICATION_INTENT, Constants.NOTIFICATION_PAUSE_INTENT)
        val pausePendingIntent =
            PendingIntent.getService(baseContext, pauseButtonRequestCode, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.pause_notity, baseContext.getString(R.string.pause), pausePendingIntent)
    }

    private fun getPlayButtonAction(): NotificationCompat.Action {
        val intentPlay = Intent(baseContext, BackgroundPlayerService::class.java)
        intentPlay.putExtra(Constants.NOTIFICATION_INTENT, Constants.NOTIFICATION_PLAY_INTENT)
        val playPendingIntent =
            PendingIntent.getService(baseContext, playButtonRequestCode, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.play_notity, baseContext.getString(R.string.play), playPendingIntent)
    }
}