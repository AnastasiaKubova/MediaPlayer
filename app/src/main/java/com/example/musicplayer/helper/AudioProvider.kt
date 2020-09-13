package com.example.musicplayer.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.TextUtils
import com.example.musicplayer.R
import com.example.musicplayer.model.Track
import com.example.musicplayer.utility.Constants.audioExtension
import java.io.File
import java.net.URI

class AudioProvider {

    companion object {
        val instance = AudioProvider()
    }

    fun getPlayList(context: Context, path: URI): MutableList<Track> {
        val playlist = mutableListOf<Track>()
        val folder = File(path.path)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles().forEach {
                val track = parseMeta(context, it)
                if (track != null) {
                    playlist += track
                }
            }
        }
        return playlist
    }

    private fun parseMeta(context: Context, file: File) : Track? {
        if (file.isFile && audioExtension.contains(file.extension)) {
            val metadata = MediaMetadataRetriever()
            metadata.setDataSource(file.path)
            var title = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            if (title == null) {
                title = file.name.replace(".${file.extension}", "")
            }
            var artists = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            if (artists == null) {
                artists = context.getString(R.string.unknown)
            }
            var duration = 0
            if (!TextUtils.isEmpty(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) {
                duration = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
            }
            val art: ByteArray? = metadata.getEmbeddedPicture()
            var image: Bitmap? = null
            if (art != null) {
                image = BitmapFactory.decodeByteArray(art, 0, art.size)
            } else {
                image = BitmapFactory.decodeResource(context.resources, R.drawable.track_preview)
            }
            metadata.release()
            return Track(title, artists, image, Uri.parse(file.path), duration, file.length())
        }
        return null
    }
}