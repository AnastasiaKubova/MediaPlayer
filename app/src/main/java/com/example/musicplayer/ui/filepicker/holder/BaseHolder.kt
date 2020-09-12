package com.example.musicplayer.ui.filepicker.holder

import com.example.musicplayer.ui.filepicker.model.FileData

interface BaseHolder {
    fun bind(file: FileData)
}