package com.example.musicplayer.ui

import com.example.musicplayer.model.Track

interface FragmentListener {
    fun bottomSheetOpenListener(track: Track?)
}