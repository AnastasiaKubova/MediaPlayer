package com.example.musicplayer.helper

import com.example.musicplayer.model.Track

interface FragmentListener {
    fun bottomSheetOpenListener(track: Track?)
}