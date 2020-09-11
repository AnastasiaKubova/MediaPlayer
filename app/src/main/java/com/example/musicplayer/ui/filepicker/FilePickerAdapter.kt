package com.example.musicplayer.ui.filepicker

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.textclassifier.TextClassifier.TYPE_EMAIL
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.utility.FilePicker
import java.io.File

const val DEFAULT = 1
const val NONE = 0

class FilePickerAdapter (var filesList: MutableList<File>, var listener: FileListener?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NONE -> {
                return DefaultItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_folder_item, parent, false), listener)
            }
            DEFAULT -> {
                return FilePickerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false), listener)
            }
        }
        return FilePickerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false), listener)
    }

    override fun getItemViewType(position: Int): Int {
        return if (filesList[position].name.equals(FilePicker.DEFAULT_FOLDER)) {
            NONE
        } else {
            DEFAULT
        }
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseHolder).bind(filesList[position])
    }

    interface FileListener {
        fun onFolderListener(file: File)
    }
}