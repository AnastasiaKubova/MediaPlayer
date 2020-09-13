package com.example.musicplayer.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.ui.BaseFragment
import com.example.musicplayer.model.Track
import kotlinx.android.synthetic.main.toolbar_playlist.*

class PlayListFragment: BaseFragment(), PlayListViewHolder.TrackListener {

    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playListAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var navController: NavController? = null
    private val viewModel by viewModels<PlayListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.playlist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init navigate. */
        navController = findNavController()

        /* Init toolbar. */
        initToolbar()

        /* Init list. */
        initAdapter()

        /* Init listeners and observers. */
        initObservers()
        viewModel.attachListener()

        /* Set info. */
        viewModel.updatePlaylist()
        showBottomDialog(View.VISIBLE)
    }

    override fun onTrackClickListener(track: Track) {
        viewModel.updateTrack(track)
    }

    override fun onTrackSettingsClickListener(track: Track) {
        listener?.bottomSheetOpenListener(track)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
    }

    private fun initToolbar() {
        title_playlist_toolbar.text = activity?.resources?.getString(R.string.play_list)
        file_playlist_toolbar.setOnClickListener {
            navController?.navigate(R.id.filePickerFragment)
        }
    }

    private fun initObservers() {
        val playlist = Observer<MutableList<Track>> {list ->
            if (list == null) {
                return@Observer
            }
            updateAdapter(list)
        }
        viewModel.playlist.observe(requireActivity(), playlist)
    }

    private fun initAdapter() {
        viewManager = LinearLayoutManager(activity)
        playListAdapter = PlayListAdapter(mutableListOf(), this)
        playlistRecyclerView = requireActivity().findViewById<RecyclerView>(R.id.playlist_view).apply {
            layoutManager = viewManager
            adapter = playListAdapter
        }
    }

    private fun updateAdapter(list: MutableList<Track>) {
        (playListAdapter as PlayListAdapter).playList = list
        (playListAdapter as PlayListAdapter).notifyDataSetChanged()
    }
}