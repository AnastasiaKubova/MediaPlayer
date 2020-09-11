package com.example.musicplayer.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicplayer.R
import com.example.musicplayer.helper.FragmentListener
import com.example.musicplayer.model.Track
import com.example.musicplayer.service.BackgroundPlayerService
import com.example.musicplayer.service.PlayerServiceConnection
import com.example.musicplayer.utility.FilePicker
import com.example.musicplayer.utility.Preference
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_fragments.*

class MainActivity : AppCompatActivity(), FragmentListener, PlayerServiceConnection.PlayerServiceConnectionListener {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var dialogView: View
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        /* Init bottom sheet dialog. */
        dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        /* Init fragments. */
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment!!.navController

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
        navController?.popBackStack()
        navController?.navigate(R.id.playFragment)
    }

    private fun openPlaylistFragment() {
        navController?.popBackStack()
        navController?.navigate(R.id.playListFragment)
    }

    private fun openFilePickerFragment() {
        navController?.navigate(R.id.filePickerFragment)
    }

    override fun onServiceConnectedListener() {
        showFirstFragment()
    }

    override fun onServiceDisconnectedListener() {
    }
}