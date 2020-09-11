package com.example.musicplayer.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.R
import com.example.musicplayer.enum.FragmentType
import com.example.musicplayer.helper.BaseFragment
import com.example.musicplayer.helper.FragmentListener
import com.example.musicplayer.model.Track
import com.example.musicplayer.service.BackgroundPlayerService
import com.example.musicplayer.service.PlayerServiceConnection
import com.example.musicplayer.ui.filepicker.FilePickerFragment
import com.example.musicplayer.ui.play.PlayFragment
import com.example.musicplayer.ui.playlist.PlayListFragment
import com.example.musicplayer.utility.FilePicker
import com.example.musicplayer.utility.Preference
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_fragments.*

class MainActivity : AppCompatActivity(), FragmentListener, PlayerServiceConnection.PlayerServiceConnectionListener {

    private var currentFragment: BaseFragment? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        /* Init bottom sheet dialog. */
        dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        /* Init listeners. */
        refresh_music_list.setOnClickListener { onRefreshClick() }
        repeat_track.setOnClickListener { onRepeatClick() }
        current_track_view.setOnClickListener { openPlayFragment() }
        music_list_view.setOnClickListener { openPlaylistFragment() }
    }

    override fun onStart() {
        super.onStart()
        PlayerServiceConnection.mConnection.listener = this
        val intent = Intent(this, BackgroundPlayerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, PlayerServiceConnection.mConnection, 0)
    }

    override fun onStop() {
        super.onStop()
        if (PlayerServiceConnection.mConnection.mBound) {
            PlayerServiceConnection.mConnection.listener = null
            unbindService(PlayerServiceConnection.mConnection)
            PlayerServiceConnection.mConnection.mBound = false
        }
    }

    override fun fragmentClose(type: FragmentType) {
        currentFragment?.listener = null
        when(type) {
            FragmentType.FilePicker -> {
                openFilePickerFragment()
            }
            FragmentType.Play -> {
                openPlayFragment()
            }
            FragmentType.Playlist -> {
                openPlaylistFragment()
            }
        }
    }

    override fun bottomSheetOpenListener(track: Track?) {
        if (track == null) {
            return
        }
        dialogView.findViewById<TextView>(R.id.track_name_bottomsheet).text = track.title
        dialogView.findViewById<TextView>(R.id.remove_track_bottomsheet).setOnClickListener {
            FilePicker.instance.removeAudioFile(track.path)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.play_next_bottomsheet).setOnClickListener {
            PlayerServiceConnection.mConnection.mService?.playTrackAsNext(track)
            dialog.cancel()
        }
        dialog.show()
    }

    private fun showFirstFragment() {

        /* Set fragment by the default. */
        if (TextUtils.isEmpty(Preference.instance.getSourceFolder())) {
            openFilePickerFragment()
        } else {
            openPlayFragment()
        }
    }

    private fun onRepeatClick() {
        val isLooper = PlayerServiceConnection.mConnection.mService?.isTrackLooping()
        if (isLooper != null) {
            PlayerServiceConnection.mConnection.mService?.setLooping(!isLooper)
            if (!isLooper) {
                repeat_track.background = getDrawable(R.drawable.bg_selected_small)
            } else {
                repeat_track.background = null
            }
        }
    }

    private fun onRefreshClick() {
        PlayerServiceConnection.mConnection.mService?.mixPlaylist()
    }

    private fun openPlayFragment() {
        if (currentFragment != PlayFragment.newInstance()) {
            currentFragment = PlayFragment.newInstance()
            updateFragment()
        }
        bottom_menu_panel.visibility = View.VISIBLE
    }

    private fun openPlaylistFragment() {
        if (currentFragment != PlayListFragment.newInstance()) {
            currentFragment = PlayListFragment.newInstance()
            updateFragment()
        }
        bottom_menu_panel.visibility = View.VISIBLE
    }

    private fun openFilePickerFragment() {
        if (currentFragment != FilePickerFragment.newInstance()) {
            currentFragment = FilePickerFragment.newInstance()
            updateFragment()
        }
        bottom_menu_panel.visibility = View.GONE
    }

    private fun updateFragment() {
        if (currentFragment == null) {
            return
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, currentFragment!!)
            .commitNow()
        currentFragment!!.listener = this
    }

    override fun onServiceConnectedListener() {
        showFirstFragment()
    }

    override fun onServiceDisconnectedListener() {
    }
}