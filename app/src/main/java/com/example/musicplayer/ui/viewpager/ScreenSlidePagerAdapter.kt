package com.example.musicplayer.ui.viewpager

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicplayer.ui.BaseFragment
import com.example.musicplayer.ui.play.PlayFragment
import com.example.musicplayer.ui.playlist.PlayListFragment

class ScreenSlidePagerAdapter (fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): BaseFragment {
        return if (position == 1) PlayListFragment() else PlayFragment()
    }
}