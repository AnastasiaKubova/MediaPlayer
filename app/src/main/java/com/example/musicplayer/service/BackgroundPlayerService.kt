package com.example.musicplayer.service

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import com.example.musicplayer.model.Track
import com.example.musicplayer.utility.AudioProvider
import com.example.musicplayer.utility.Constants
import com.example.musicplayer.utility.Preference
import java.net.URI

class BackgroundPlayerService: Service() {

    /* Player variables. */
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex: Int = 0
    private var sourceFolder: URI? = null
    private var playNextTrack: Track? = null
    var listeners: MutableList<PlayerListener> = mutableListOf()
    var currentTrack: Track? = null
    var listTracks: MutableList<Track> = mutableListOf()

    /* Service variables. */
    private val mBinder: IBinder = LocalBinder()
    private var notificationManager: NotificationPlayerManager? = null

    inner class LocalBinder : Binder() {
        val service: BackgroundPlayerService
            get() = this@BackgroundPlayerService
    }

    override fun onCreate() {

        /* Init variables for notification. */
        notificationManager = NotificationPlayerManager(baseContext)

        /* Show notification. */
        val notify = notificationManager!!.buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFY_ID, notify)
        } else {
            notificationManager?.showNotification()
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
        notificationManager?.clearNotification()
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
        notificationManager?.updatePlayNotification(currentTrack!!)
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
        notificationManager?.updatePauseNotification(currentTrack!!)
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

                /* Reset player and data. */
                resetPlayer()

                /* Update current data. */
                currentTrack = listTracks[i]
                currentTrackIndex = i

                /* Start play. */
                play()
            }
        }
    }

    fun next() {
        if (listTracks.size != 0 && currentTrackIndex != listTracks.count() - 1) {

            /* Reset player and data. */
            resetPlayer()

            /* Update current data. */
            currentTrackIndex++
            currentTrack = listTracks[currentTrackIndex]

            /* Start play. */
            play()

            /* Notify listeners. */
            notificationManager?.updatePlayNotification(currentTrack!!)
            listeners.forEach {
                it.onCurrentTrackChangeListener(currentTrack!!, mediaPlayer?.duration)
            }
        }
    }

    fun before() {
        if (listTracks.size > 0 && currentTrackIndex != 0) {

            /* Reset player and data. */
            resetPlayer()

            /* Update current data. */
            currentTrackIndex--
            currentTrack = listTracks[currentTrackIndex]

            /* Start play. */
            play()

            /* Notify listeners. */
            notificationManager?.updatePlayNotification(currentTrack!!)
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

        /* Reset player and data. */
        resetData()

        /* Mix playlist. */
        listTracks.shuffle()

        /* Update current state. */
        currentTrack = listTracks[currentTrackIndex]

        /* Init player. */
        mediaPlayerCreate()

        /* Notify listeners. */
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
                    when {
                        playNextTrack != null -> {
                            playTrack(playNextTrack!!)
                            playNextTrack = null
                        }
                        isTrackLooping() -> {
                            play()
                        }
                        else -> {
                            next()
                        }
                    }
                }
            }
            mediaPlayer?.prepare()
        }
    }

    private fun initPlayerList() {
        if (baseContext != null && sourceFolder != null) {

            /* Reset player and data. */
            resetData()

            /* Init playlist. */
            listTracks = AudioProvider.instance.getPlayList(baseContext!!, sourceFolder!!)
            if (listTracks.size > 0) {

                /* Update current data. */
                currentTrack = listTracks[currentTrackIndex]

                /* Notify listeners. */
                notificationManager?.updatePauseNotification(currentTrack!!)
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