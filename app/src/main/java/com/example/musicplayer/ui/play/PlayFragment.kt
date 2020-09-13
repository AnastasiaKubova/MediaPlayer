package com.example.musicplayer.ui.play

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.musicplayer.R
import com.example.musicplayer.ui.BaseFragment
import com.example.musicplayer.utility.TimeConverter
import com.example.musicplayer.model.Track
import com.example.musicplayer.service.PlayerServiceConnection
import kotlinx.android.synthetic.main.manager_track_panel.*
import kotlinx.android.synthetic.main.play_fragment.*
import kotlinx.android.synthetic.main.seekbar_panel.*

class PlayFragment: BaseFragment(), PlayerServiceConnection.PlayerServiceConnectionListener {

    private val viewModel by viewModels<PlayViewModel>()
    private val seekHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.play_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init view listeners. */
        play_pause.setOnClickListener { onPlayOrPauseClick() }
        skip_next.setOnClickListener { onSkipAndNextClick() }
        skip_before.setOnClickListener { onSkipAndBeforeClick() }
        track_settings.setOnClickListener { onTrackSettingsClick(viewModel.currentTrack.value) }

        /* Init data for seek bar. */
        runnable = Runnable { updateSeekBar() }
        seekbar_track.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                if (seek != null) {
                    viewModel.setSeekPosition(seek.progress)
                }
            }
        })

        /* Init listeners and observers*/
        initObservers()

        /* Set info. */
        viewModel.updateCurrentTrack()
        showBottomDialog(View.VISIBLE)
    }

    override fun onStart() {
        super.onStart()
        viewModel.attachListener()
        PlayerServiceConnection.listener  = this
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
        PlayerServiceConnection.listener = null
    }

    private fun updateSeekBar() {
        viewModel.updateSeekPositionForTrack()
        seekHandler.postDelayed(runnable, 50)
    }

    private fun initObservers() {
        val playTrack = Observer<Boolean> { isPlay ->
            if (isPlay == null) {
                return@Observer
            }
            if (isPlay) {
                play_pause.setImageDrawable(activity?.resources?.getDrawable(R.drawable.pause))
                updateSeekBar()
            } else {
                play_pause.setImageDrawable(activity?.resources?.getDrawable(R.drawable.play))
            }
        }
        val currentTrack = Observer<Track> { track ->
            showCurrentTrack(track)
        }
        val durationTrack = Observer<Int> { duration ->
            if (duration == null) {
                return@Observer
            }
            total_track_minute.text = TimeConverter.milliSecondsToTimer(duration)
            seekbar_track.max = duration
        }
        val positionTrack = Observer<Int> { pos ->
            if (pos == null) {
                return@Observer
            }
            seekbar_track?.progress = pos
            current_track_minute?.text =
                TimeConverter.milliSecondsToTimer(seekbar_track.max - seekbar_track.max - pos)
        }
        viewModel.trackPosition.observe(requireActivity(), positionTrack)
        viewModel.durationTrack.observe(requireActivity(), durationTrack)
        viewModel.playTrackStatus.observe(requireActivity(), playTrack)
        viewModel.currentTrack.observe(requireActivity(), currentTrack)
    }

    private fun onPlayOrPauseClick() {
        viewModel.playTrackStatusChange()
    }

    private fun onSkipAndNextClick() {
        viewModel.skipAndNextTrack()
    }

    private fun onSkipAndBeforeClick() {
        viewModel.skipAndBeforeTrack()
    }

    private fun onTrackSettingsClick(track: Track?) {
        listener?.bottomSheetOpenListener(track)
    }

    private fun showCurrentTrack(track: Track?) {
        if (track == null) {
            return
        }
        title_track.text = track.title
        group_name.text = track.singer
    }

    override fun onServiceConnectedListener() {
        viewModel.attachListener()
        viewModel.updateCurrentTrack()
    }

    override fun onServiceDisconnectedListener() {
        viewModel.detachListener()
    }
}