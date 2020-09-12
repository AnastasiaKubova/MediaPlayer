package com.example.musicplayer.ui.filepicker.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.ui.filepicker.model.FileData
import com.example.musicplayer.ui.filepicker.FilePickerAdapter
import kotlinx.android.synthetic.main.file_item.view.*

class FilePickerViewHolder(itemView: View, var listener: FilePickerAdapter.FileListener?) : RecyclerView.ViewHolder(itemView),
    BaseHolder {

    override fun bind(file: FileData) {
        itemView.file_title.text = file.file!!.name
        if (file.file!!.isDirectory) {
            itemView.file_image.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.folder))
            itemView.setOnClickListener { listener?.onFolderListener(file) }
        } else {
            itemView.file_image.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.audio_file))
            itemView.setOnClickListener { }
        }
    }
}