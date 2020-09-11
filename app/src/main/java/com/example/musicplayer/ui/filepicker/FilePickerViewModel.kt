package com.example.musicplayer.ui.filepicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.service.PlayerServiceConnection
import com.example.musicplayer.utility.FilePicker
import com.example.musicplayer.utility.Preference
import java.io.File
import java.net.URI

class FilePickerViewModel: ViewModel() {

    val filesList: MutableLiveData<MutableList<File>> by lazy {
        MutableLiveData<MutableList<File>>()
    }

    val currentFileName: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun saveFolder() {
        Preference.instance.setSourceFolder(FilePicker.instance.currentFolderPath.path)
        PlayerServiceConnection.mConnection.mService?.setUriContent(FilePicker.instance.currentFolderPath)
    }

    fun updateFileList() {
        filesList.value = FilePicker.instance.getLastFolderList()
        updateCurrentFolderName()
    }

    fun updateListForParentFolder() {
        filesList.value = FilePicker.instance.getParentFiles()
        updateCurrentFolderName()
    }

    fun updateListForSelectFolder(file: File) {
        filesList.value = FilePicker.instance.getFromSelectFolderList(URI.create(file.absolutePath))
        updateCurrentFolderName()
    }

    private fun updateCurrentFolderName() {
        currentFileName.value = FilePicker.instance.getFileName()
    }
}