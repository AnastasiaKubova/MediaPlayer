package com.example.musicplayer.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicplayer.R
import com.example.musicplayer.model.Track
import com.example.musicplayer.service.BackgroundPlayerService
import com.example.musicplayer.service.PlayerServiceConnection
import com.example.musicplayer.helper.FilePicker
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity(), FragmentListener {

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
    }

    override fun onStart() {
        super.onStart()

        /* Init listeners. */
        BaseFragment.listener = this

        /* Start service. */
        val intent = Intent(this, BackgroundPlayerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, PlayerServiceConnection, 0)
    }

    override fun onStop() {
        super.onStop()

        /* Remove listeners. */
        BaseFragment.listener = null

        /* Disconnect service. */
        if (PlayerServiceConnection.mBound) {
            unbindService(PlayerServiceConnection)
            PlayerServiceConnection.mBound = false
        }
    }

    override fun bottomSheetOpenListener(track: Track?) {
        if (track == null) {
            return
        }
        dialogView.findViewById<TextView>(R.id.track_name_bottomsheet).text = track.title
        dialogView.findViewById<TextView>(R.id.remove_track_bottomsheet).setOnClickListener {
            FilePicker.removeAudioFile(track.path)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.play_next_bottomsheet).setOnClickListener {
            PlayerServiceConnection.mService?.playTrackAsNext(track)
            dialog.cancel()
        }
        dialog.show()
    }
}