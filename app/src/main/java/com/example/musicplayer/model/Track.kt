package com.example.musicplayer.model

import android.graphics.Bitmap
import android.net.Uri

data class Track(
    val title: String,
    val singer: String,
    val image: Bitmap?,
    val path: Uri,
    val duration: Int,
    val size: Long
) {
}