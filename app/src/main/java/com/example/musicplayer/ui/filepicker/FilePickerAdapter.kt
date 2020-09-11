package com.example.musicplayer.ui.filepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import java.io.File

class FilePickerAdapter (var filesList: MutableList<File>, var listener: FilePickerViewHolder.FileListener?): RecyclerView.Adapter<FilePickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)
        return FilePickerViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onBindViewHolder(holder: FilePickerViewHolder, position: Int) {
        holder.bind(filesList[position])
    }

}