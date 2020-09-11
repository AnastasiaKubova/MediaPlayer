package com.example.musicplayer.utility

import android.net.Uri
import android.os.Environment
import android.text.TextUtils
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
        val DEFAULT_FOLDER = "DEFAULT_FOLDER"
    }

    fun getFileName(): String {
        return File(currentFolderPath.path).name
    }

    fun getLastFolderList(): MutableList<File> {
        return getFilesFromDirectory(currentFolderPath)
    }

    fun getFromSelectFolderList(path: URI): MutableList<File> {
        currentFolderPath = path
        return getFilesFromDirectory(currentFolderPath)
    }

    fun getParentFiles(): MutableList<File> {
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

    private fun getFilesFromDirectory(uri: URI): MutableList<File> {
        val directory = File(uri.path)
        val fullFileList: MutableList<File> = mutableListOf()
        var files: Array<File> = arrayOf()
        if (!directory.name.equals(rootFolder)) {
            fullFileList.add(defaultFolder)
        }
        if (directory.list() != null) {
            files = directory.listFiles() { file ->
                file != null && (file.isDirectory || (file.extension != null && audioExtension.contains(
                    file.extension
                )))
            }
        }
        fullFileList.addAll(files.toMutableList())
        return fullFileList
    }
}