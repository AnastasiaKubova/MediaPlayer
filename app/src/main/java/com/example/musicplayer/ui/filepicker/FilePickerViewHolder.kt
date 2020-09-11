package com.example.musicplayer.ui.filepicker

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import kotlinx.android.synthetic.main.file_item.view.*
import java.io.File

class FilePickerViewHolder(itemView: View, var listener: FilePickerAdapter.FileListener?) : RecyclerView.ViewHolder(itemView), BaseHolder {

    override fun bind(file: File) {
        itemView.file_title.text = file.name
        if (file.isDirectory) {
            itemView.file_image.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.folder))
            itemView.setOnClickListener { listener?.onFolderListener(file) }
        } else {
            itemView.file_image.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.audio_file))
            itemView.setOnClickListener { }
        }
    }
}