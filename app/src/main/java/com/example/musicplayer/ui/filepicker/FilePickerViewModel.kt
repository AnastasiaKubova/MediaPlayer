package com.example.musicplayer.ui.filepicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.service.PlayerServiceConnection
import com.example.musicplayer.ui.filepicker.model.FileData
import com.example.musicplayer.helper.FilePicker
import com.example.musicplayer.helper.Preference
import java.net.URI

class FilePickerViewModel: ViewModel() {

    val filesList: MutableLiveData<MutableList<FileData>> by lazy {
        MutableLiveData<MutableList<FileData>>()
    }

    val currentFileName: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun saveFolder() {
        Preference.setSourceFolder(FilePicker.currentFolderPath.path)
        PlayerServiceConnection.mService?.setUriContent(FilePicker.currentFolderPath)
    }

    fun updateFileList() {
        filesList.value = FilePicker.getLastFolderList()
        updateCurrentFolderName()
    }

    fun updateListForParentFolder() {
        filesList.value = FilePicker.getParentFiles()
        updateCurrentFolderName()
    }

    fun updateListForSelectFolder(file: FileData) {
        filesList.value = FilePicker.getFromSelectFolderList(URI.create(file.file!!.absolutePath))
        updateCurrentFolderName()
    }

    private fun updateCurrentFolderName() {
        currentFileName.value = FilePicker.getFileName()
    }
}