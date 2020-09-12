package com.example.musicplayer.utility

import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import com.example.musicplayer.enum.FileType
import com.example.musicplayer.ui.filepicker.model.FileData
import com.example.musicplayer.utility.Constants.audioExtension
import java.io.File
import java.net.URI

class FilePicker private constructor(){

    var currentFolderPath: URI = Environment.getRootDirectory().toURI()
        private set

    private val defaultFolder: File = File("DEFAULT_FOLDER")

    init {
        val url = Preference.instance.getSourceFolder()
        if (!TextUtils.isEmpty(url)) {
            currentFolderPath = URI.create(url)
        }
    }

    companion object {
        val instance = FilePicker()
        val rootFolder = "system"
    }

    fun getFileName(): String {
        return File(currentFolderPath.path).name
    }

    fun getLastFolderList(): MutableList<FileData> {
        return getFilesFromDirectory(currentFolderPath)
    }

    fun getFromSelectFolderList(path: URI): MutableList<FileData> {
        currentFolderPath = path
        return getFilesFromDirectory(currentFolderPath)
    }

    fun getParentFiles(): MutableList<FileData> {
        if (currentFolderPath.path.trim('/').equals(rootFolder)) {
            return getFilesFromDirectory(currentFolderPath)
        }
        val file = File(currentFolderPath.path)
        currentFolderPath = URI.create(file.parentFile.absolutePath)
        return getFilesFromDirectory(currentFolderPath)
    }

    fun removeAudioFile(uri: Uri) {
        val file = File(uri.path)
        if (file.exists() && file.isFile) {
            file.delete()
        }
    }

    private fun getFilesFromDirectory(uri: URI): MutableList<FileData> {
        val directory = File(uri.path)
        val fullFileList: MutableList<FileData> = mutableListOf()
        var files: Array<File> = arrayOf()
        if (!directory.name.equals(rootFolder)) {
            fullFileList.add(
                FileData(
                    FileType.None,
                    null
                )
            )
        }
        if (directory.list() != null) {
            files = directory.listFiles() { file ->
                file != null && (file.isDirectory || (file.extension != null && audioExtension.contains(
                    file.extension
                )))
            }
        }
        fullFileList.addAll(files.map { file ->
            FileData(
                FileType.File,
                file
            )
        })
        return fullFileList
    }
}