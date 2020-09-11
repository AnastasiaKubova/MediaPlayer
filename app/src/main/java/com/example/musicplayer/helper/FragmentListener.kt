package com.example.musicplayer.helper

import com.example.musicplayer.enum.FragmentType
import com.example.musicplayer.model.Track

interface FragmentListener {
    fun fragmentClose(type: FragmentType)
    fun bottomSheetOpenListener(track: Track?)
}