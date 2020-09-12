package com.example.musicplayer.ui.filepicker.model

import com.example.musicplayer.enum.FileType
import java.io.File

data class FileData (var fileType: FileType, var file: File?) {
}