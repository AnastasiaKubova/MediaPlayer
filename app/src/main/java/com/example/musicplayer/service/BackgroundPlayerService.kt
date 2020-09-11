package com.example.musicplayer.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.model.Track
import com.example.musicplayer.utility.AudioProvider
import com.example.musicplayer.utility.Constants
import com.example.musicplayer.utility.Preference
import java.net.URI
import androidx.media.app.NotificationCompat as MediaNotificationCompat

class BackgroundPlayerService: Service(), AudioManager.OnAudioFocusChangeListener {

    /* Constants. */
    private val NOTIFY_ID = 100
    private val CHANNEL_ID = "player.manager.service.CHANNEL_ID"
    private val CHANNEL_TEXT = "player.manager.service.CHANNEL_TEXT"
    private val SESSION_TRACK = "player.manager.service.SESSION_TRACK"
    private var playButtonRequestCode = 0
    private var pauseButtonRequestCode = 1
    private var nextButtonRequestCode = 2
    private var beforeButtonRequestCode = 3

    /* Player variables. */
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex: Int = 0
    private var sourceFolder: URI? = null
    private var playNextTrack: Track? = null
    var listeners: MutableList<PlayerListener> = mutableListOf()
    var currentTrack: Track? = null
    var listTracks: MutableList<Track> = mutableListOf()

    /* Service variables. */
    private var mediaSession: MediaSessionCompat? = null
    private var notificationManager: NotificationManager? = null
    private val mBinder: IBinder = LocalBinder()
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var isNotificationIsShown: Boolean = false
    private var notify: Notification? = null

    inner class LocalBinder : Binder() {
        val service: BackgroundPlayerService
            get() = this@BackgroundPlayerService
    }

    override fun onCreate() {

        /* Init variables for notification. */
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mediaSession = MediaSessionCompat(baseContext, SESSION_TRACK)
        notificationBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
        notify = buildNotification()

        /* Show notification. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFY_ID, notify)
        } else {
            notificationManager?.notify(NOTIFY_ID, notify)
        }

        /* Init playlist. */
        val url = Preference.instance.getSourceFolder()
        if (!TextUtils.isEmpty(url)) {
            setUriContent(URI.create(url))
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when(intent.extras?.getString(Constants.NOTIFICATION_INTENT)) {
            Constants.NOTIFICATION_PLAYER_NEXT_INTENT -> {
                next()
            }
            Constants.NOTIFICATION_PLAYER_BEFORE_INTENT -> {
                before()
            }
            Constants.NOTIFICATION_PLAY_INTENT -> {
                play()
            }
            Constants.NOTIFICATION_PAUSE_INTENT -> {
                pause()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        resetData()
        notificationManager!!.cancel(NOTIFY_ID)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        // TODO
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {

            }
            AudioManager.AUDIOFOCUS_LOSS -> {

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // ... pausing or ducking depends on your app
            }
        }
    }

    fun setUriContent(path: URI) {
        this.sourceFolder = path
        initPlayerList()
        mediaPlayerCreate()
    }

    fun play() {
        if (currentTrack == null) {
            return
        }
        if (mediaPlayer == null) {
            mediaPlayerCreate()
        }
        mediaPlayer?.start()
        updateTextNotification()
        updatePlayActions()
        listeners.forEach {
            it.onTrackPlay(currentTrack!!)
        }
    }

    fun pause() {
        if (currentTrack == null) {
            return
        }
        if (mediaPlayer == null) {
            mediaPlayerCreate()
        }
        mediaPlayer?.pause()
        updateTextNotification()
        updatePauseActions()
        listeners.forEach {
            it.onTrackPause(currentTrack!!)
        }
    }

    fun isTrackPlay(): Boolean {
        return mediaPlayer != null && mediaPlayer!!.isPlaying
    }

    fun isTrackLooping(): Boolean {
        return mediaPlayer != null && mediaPlayer!!.isLooping
    }

    fun setLooping(isLooping: Boolean) {
        mediaPlayer?.isLooping = isLooping
    }

    fun playTrack(track: Track) {
        for (i in 0 until listTracks.size) {
            if (track == listTracks[i]) {
                resetPlayer()
                currentTrack = listTracks[i]
                currentTrackIndex = i
                play()
            }
        }
    }

    fun next() {
        if (listTracks.size != 0 && currentTrackIndex != listTracks.count() - 1) {
            resetPlayer()
            currentTrackIndex++
            currentTrack = listTracks[currentTrackIndex]
            play()
            updateTextNotification()
            updatePlayActions()
            listeners.forEach {
                it.onCurrentTrackChangeListener(currentTrack!!, mediaPlayer?.duration)
            }
        }
    }

    fun before() {
        if (listTracks.size > 0 && currentTrackIndex != 0) {
            resetPlayer()
            currentTrackIndex--
            currentTrack = listTracks[currentTrackIndex]
            play()
            updateTextNotification()
            updatePlayActions()
            listeners.forEach {
                it.onCurrentTrackChangeListener(currentTrack!!, mediaPlayer?.duration)
            }
        }
    }

    fun mixPlaylist() {
        if (listTracks.size == 0) {
            return
        }
        val isPlayed = isTrackPlay()
        resetData()
        listTracks.shuffle()
        currentTrack = listTracks[currentTrackIndex]
        mediaPlayerCreate()
        listeners.forEach {
            it.onPlaylistChangeListener(listTracks)
            it.onCurrentTrackChangeListener(currentTrack!!, mediaPlayer?.duration)
            if (isPlayed) {
                play()
            }
        }
    }

    fun getCurrentPosition(): Int {
        return if (mediaPlayer != null) mediaPlayer!!.currentPosition else 0
    }

    fun getDuration(): Int {
        return if (mediaPlayer != null) mediaPlayer!!.duration else 0
    }

    fun playTrackAsNext(track: Track) {
        playNextTrack = track
    }

    /************* Start methods. ***************/
    private fun buildNotification(): Notification {
        notificationBuilder

            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)

            // Apply the media style template
            .setStyle(
                MediaNotificationCompat.MediaStyle()
                    .setShowCancelButton(true)
                    .setShowActionsInCompactView(1)
                    .setMediaSession(mediaSession?.sessionToken)
            )
            .color = baseContext.resources.getColor(R.color.colorPrimary)

        /* Init buttons on notification. */
        updateTextNotification()
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

    private fun updateTextNotification() {
        val track = currentTrack ?: return
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
        return NotificationCompat.Action(R.drawable.next_notity,getString(R.string.next),nextPendingIntent)
    }

    private fun getBeforeButtonAction():NotificationCompat.Action {
        val intentBefore = Intent(baseContext, BackgroundPlayerService::class.java)
        intentBefore.putExtra(
            Constants.NOTIFICATION_INTENT,
            Constants.NOTIFICATION_PLAYER_BEFORE_INTENT
        )
        val beforePendingIntent
                = PendingIntent.getService(baseContext, beforeButtonRequestCode, intentBefore, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.before_notity,getString(R.string.previous), beforePendingIntent)
    }

    private fun getPauseButtonAction(): NotificationCompat.Action {
        val intentPause = Intent(baseContext, BackgroundPlayerService::class.java)
        intentPause.putExtra(Constants.NOTIFICATION_INTENT, Constants.NOTIFICATION_PAUSE_INTENT)
        val pausePendingIntent =
            PendingIntent.getService(baseContext, pauseButtonRequestCode, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.pause_notity, getString(R.string.pause), pausePendingIntent)
    }

    private fun getPlayButtonAction(): NotificationCompat.Action {
        val intentPlay = Intent(baseContext, BackgroundPlayerService::class.java)
        intentPlay.putExtra(Constants.NOTIFICATION_INTENT, Constants.NOTIFICATION_PLAY_INTENT)
        val playPendingIntent =
            PendingIntent.getService(baseContext, playButtonRequestCode, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(R.drawable.play_notity, getString(R.string.play), playPendingIntent)
    }

    private fun resetPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun resetData() {
        resetPlayer()
        playNextTrack = null
        currentTrack = null
        currentTrackIndex = 0
        listeners.forEach {
            it.onPlayerReleaseListener()
        }
    }

    private fun mediaPlayerCreate() {
        if (baseContext != null && currentTrack != null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(baseContext!!, currentTrack?.path!!)
            mediaPlayer?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            mediaPlayer?.setOnCompletionListener {
                listeners.forEach{
                    it.onPlayCompletion()
                    if (playNextTrack != null) {
                        playTrack(playNextTrack!!)
                        playNextTrack = null
                    } else if (isTrackLooping()) {
                        play()
                    } else {
                        next()
                    }
                }
            }
            mediaPlayer?.prepare()
        }
    }

    private fun initPlayerList() {
        if (baseContext != null && sourceFolder != null) {
            resetData()
            listTracks = AudioProvider.instance.getPlayList(baseContext!!, sourceFolder!!)
            if (listTracks.size > 0) {
                currentTrack = listTracks[currentTrackIndex]
                updateTextNotification()
                updatePauseActions()
                listeners.forEach {
                    it.onCurrentTrackChangeListener(currentTrack!!, mediaPlayer?.duration)
                }
            }
        }
    }

    interface PlayerListener {
        fun onPlaylistChangeListener(playlist: MutableList<Track>)
        fun onCurrentTrackChangeListener(track: Track, duration: Int?)
        fun onPlayerReleaseListener()
        fun onTrackStop(track: Track)
        fun onTrackPlay(track: Track)
        fun onTrackPause(track: Track)
        fun onPlayCompletion()
    }
}