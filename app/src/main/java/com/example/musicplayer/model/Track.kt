package com.example.musicplayer.model

import android.graphics.Bitmap
import android.net.Uri

data class Track(
    var title: String,
    var singer: String,
    var image: Bitmap?,
    var path: Uri,
    var duration: Int,
    var size: Long
) {
}