package com.example.musicplayer.ui.play

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.Track
import com.example.musicplayer.service.BackgroundPlayerService
import com.example.musicplayer.service.PlayerServiceConnection

class PlayViewModel : ViewModel(), BackgroundPlayerService.PlayerListener {

    val playTrackStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val currentTrack: MutableLiveData<Track> by lazy {
        MutableLiveData<Track>()
    }

    val durationTrack: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val trackPosition: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun updateCurrentTrack() {
        currentTrack.value = PlayerServiceConnection.mService?.currentTrack
        durationTrack.value = PlayerServiceConnection.mService?.getDuration()
        playTrackStatus.value = PlayerServiceConnection.mService?.isTrackPlay()
        trackPosition.value = PlayerServiceConnection.mService?.getCurrentPosition()
    }

    fun playTrackStatusChange() {
        if (PlayerServiceConnection.mService == null) {
            return
        }
        if (PlayerServiceConnection.mService!!.isTrackPlay()) {
            PlayerServiceConnection.mService?.pause()
        } else {
            PlayerServiceConnection.mService?.play()
        }
    }

    fun skipAndNextTrack() {
        PlayerServiceConnection.mService?.next()
    }

    fun skipAndBeforeTrack() {
        PlayerServiceConnection.mService?.before()
    }

    fun updateSeekPositionForTrack() {
        trackPosition.value = PlayerServiceConnection.mService?.getCurrentPosition()
    }

    fun setSeekPosition(pos: Int) {
        PlayerServiceConnection.mService?.setSeekPosition(pos)
        updateCurrentTrack()
    }

    fun attachListener() {
        PlayerServiceConnection.mService?.listeners?.add(this)
    }

    fun detachListener() {
        PlayerServiceConnection.mService?.listeners?.remove(this)
    }

    override fun onPlaylistChangeListener(playlist: MutableList<Track>) {
        updateCurrentTrack()
    }

    override fun onCurrentTrackChangeListener(track: Track, duration: Int?) {
        currentTrack.value = track
        durationTrack.value = duration ?: 0
    }

    override fun onPlayerReleaseListener() {
    }

    override fun onTrackStop(track: Track) {
        playTrackStatus.value = false
    }

    override fun onTrackPlay(track: Track) {
        playTrackStatus.value = true
        updateCurrentTrack()
    }

    override fun onTrackPause(track: Track) {
        playTrackStatus.value = false
    }

    override fun onPlayCompletion() {
        playTrackStatus.value = false
    }
}