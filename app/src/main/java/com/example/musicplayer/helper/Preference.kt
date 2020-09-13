package com.example.musicplayer.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.musicplayer.App


object Preference {

    private val APP_PREFERENCES = "music_player_settings"
    private val SOURCE_PATH = "source_path"

    private var mSettings: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    init {
        mSettings = App.context?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        editor = mSettings!!.edit()
    }

    fun getSourceFolder(): String? {
        return mSettings?.getString(SOURCE_PATH, null)
    }

    fun setSourceFolder(path: String?) {
        editor?.putString(SOURCE_PATH, path);
        editor?.apply();
    }
}