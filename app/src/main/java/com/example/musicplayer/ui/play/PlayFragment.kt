package com.example.musicplayer.ui.play

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.musicplayer.R
import com.example.musicplayer.helper.BaseFragment
import com.example.musicplayer.utility.TimeConverter
import com.example.musicplayer.model.Track
import kotlinx.android.synthetic.main.manager_track_panel.*
import kotlinx.android.synthetic.main.play_fragment.*
import kotlinx.android.synthetic.main.seekbar_panel.*

class PlayFragment: BaseFragment() {

    private lateinit var viewModel: PlayViewModel
    private val seekHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    companion object {
        fun newInstance() = PlayFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.play_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayViewModel::class.java)

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

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        /* Init listeners and observers*/
        initObservers()
        viewModel.attachListener()

        /* Set info. */
        Log.d("TEST_LOG_TAG", "updateCurrentTrack")
        viewModel.updateCurrentTrack()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
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
        viewModel.trackPosition.observe(activity!!, positionTrack)
        viewModel.durationTrack.observe(activity!!, durationTrack)
        viewModel.playTrackStatus.observe(activity!!, playTrack)
        viewModel.currentTrack.observe(activity!!, currentTrack)
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
}