package com.example.musicplayer.ui.filepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.enum.FileType
import com.example.musicplayer.ui.filepicker.holder.BaseHolder
import com.example.musicplayer.ui.filepicker.holder.DefaultItemViewHolder
import com.example.musicplayer.ui.filepicker.holder.FilePickerViewHolder
import com.example.musicplayer.ui.filepicker.model.FileData

class FilePickerAdapter (var filesList: MutableList<FileData>, var listener: FileListener?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            FileType.None.number -> {
                return DefaultItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.empty_folder_item, parent, false),
                    listener
                )
            }
            FileType.File.number -> {
                return FilePickerViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false),
                    listener
                )
            }
        }
        return FilePickerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false),
            listener
        )
    }

    override fun getItemViewType(position: Int): Int {
        return filesList[position].fileType.number
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseHolder).bind(filesList[position])
    }

    interface FileListener {
        fun onFolderListener(file: FileData)
    }
}