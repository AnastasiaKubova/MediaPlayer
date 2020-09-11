package com.example.musicplayer.ui.filepicker

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import kotlinx.android.synthetic.main.file_item.view.*
import java.io.File

class DefaultItemViewHolder (itemView: View, var listener: FilePickerAdapter.FileListener?) : RecyclerView.ViewHolder(itemView), BaseHolder {

    override fun bind(file: File) {
        itemView.setOnClickListener { listener?.onFolderListener(file) }
    }
}