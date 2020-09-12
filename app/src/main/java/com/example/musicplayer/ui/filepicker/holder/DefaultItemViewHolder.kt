package com.example.musicplayer.ui.filepicker.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.ui.filepicker.model.FileData
import com.example.musicplayer.ui.filepicker.FilePickerAdapter

class DefaultItemViewHolder (itemView: View, var listener: FilePickerAdapter.FileListener?) : RecyclerView.ViewHolder(itemView),
    BaseHolder {

    override fun bind(file: FileData) {
        itemView.setOnClickListener { listener?.onFolderListener(file) }
    }
}