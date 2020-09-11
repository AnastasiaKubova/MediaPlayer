package com.example.musicplayer.ui.playlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.model.Track
import com.example.musicplayer.utility.TimeConverter
import kotlinx.android.synthetic.main.item_track.view.*

class PlayListViewHolder(itemView: View, var listener: TrackListener?) : RecyclerView.ViewHolder(itemView) {

    fun bind(track: Track) {
        itemView.title_track.text = track.title
        itemView.name_group.text = track.singer
        itemView.track_duration.text = TimeConverter.milliSecondsToTimer(track.duration)
        itemView.setOnClickListener {
            listener?.onTrackClickListener(track)
        }
        itemView.track_settings.setOnClickListener {
            listener?.onTrackSettingsClickListener(track)
        }
    }

    interface TrackListener {
        fun onTrackClickListener(track: Track)
        fun onTrackSettingsClickListener(track: Track)
    }
}