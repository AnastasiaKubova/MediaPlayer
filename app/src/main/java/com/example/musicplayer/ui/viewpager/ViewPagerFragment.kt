package com.example.musicplayer.ui.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.musicplayer.R
import com.example.musicplayer.service.PlayerServiceConnection
import com.example.musicplayer.ui.BaseFragment
import com.example.musicplayer.ui.MainActivity
import kotlinx.android.synthetic.main.bottom_fragments.*
import kotlinx.android.synthetic.main.view_pager_fragment.*

class ViewPagerFragment: BaseFragment() {

    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private var navController: NavController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.view_pager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Set on click listeners. */
        refresh_music_list.setOnClickListener { onRefreshClick() }
        repeat_track.setOnClickListener { onRepeatClick() }
        current_track_view.setOnClickListener { openPlayFragment() }
        music_list_view.setOnClickListener { openPlaylistFragment() }

        /* Init view pager. */
        initViewPager()
    }

    private fun openPlayFragment() {
        navController?.popBackStack()
        navController?.navigate(R.id.playFragment)
        pager.currentItem = 0
    }

    private fun openPlaylistFragment() {
        navController?.popBackStack()
        navController?.navigate(R.id.playListFragment)
        pager.currentItem = 1
    }

    private fun onRepeatClick() {
        val isLooper = PlayerServiceConnection.mService?.isTrackLooping()
        if (isLooper != null) {
            PlayerServiceConnection.mService?.setLooping(!isLooper)
            if (!isLooper) {
                repeat_track.background = activity?.getDrawable(R.drawable.bg_selected_small)
            } else {
                repeat_track.background = null
            }
        }
    }

    private fun onRefreshClick() {
        PlayerServiceConnection.mService?.mixPlaylist()
    }

    private fun initViewPager() {

        // The pager adapter, which provides the pages to the view pager widget.
        pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        pager.adapter = pagerAdapter
        pager.currentItem = 0
        pager.setPageTransformer(ZoomOutPageTransformer())
    }
}