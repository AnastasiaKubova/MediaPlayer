package com.example.musicplayer.ui.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.Track
import com.example.musicplayer.service.BackgroundPlayerService
import com.example.musicplayer.service.PlayerServiceConnection

class PlayListViewModel: ViewModel(), BackgroundPlayerService.PlayerListener {

    val playlist: MutableLiveData<MutableList<Track>> by lazy {
        MutableLiveData<MutableList<Track>>()
    }

    fun attachListener() {
        PlayerServiceConnection.mConnection.mService?.listeners?.add(this)
    }

    fun detachListener() {
        PlayerServiceConnection.mConnection.mService?.listeners?.remove(this)
    }

    fun updatePlaylist() {
        playlist.value = PlayerServiceConnection.mConnection.mService?.listTracks
    }

    fun updateTrack(track: Track) {
        PlayerServiceConnection.mConnection.mService?.playTrack(track)
    }

    override fun onPlaylistChangeListener(playlist: MutableList<Track>) {
        updatePlaylist()
    }

    override fun onCurrentTrackChangeListener(track: Track, duration: Int?) {
    }

    override fun onPlayerReleaseListener() {
    }

    override fun onTrackStop(track: Track) {
    }

    override fun onTrackPlay(track: Track) {
    }

    override fun onTrackPause(track: Track) {
    }

    override fun onPlayCompletion() {
    }
}