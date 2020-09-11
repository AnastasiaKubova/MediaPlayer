package com.example.musicplayer.ui.filepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.enum.FragmentType
import com.example.musicplayer.helper.BaseFragment
import com.example.musicplayer.utility.FilePicker
import kotlinx.android.synthetic.main.toolbar_filepicker.*
import java.io.File

class FilePickerFragment : BaseFragment(), FilePickerViewHolder.FileListener,
    BaseFragment.DialogInterface {

    private lateinit var filepickerRecyclerView: RecyclerView
    private lateinit var filepickerAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: FilePickerViewModel

    companion object {
        fun newInstance() = FilePickerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.filepicker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilePickerViewModel::class.java)
        dialogListener = this
        initObservers()
        initList()
        viewModel.updateFileList()
    }

    override fun onFolderListener(file: File) {
        viewModel.updateListForSelectFolder(file)
    }

    override fun positiveClickListener() {
        viewModel.saveFolder()
        listener?.fragmentClose(FragmentType.Play)
    }

    override fun negativeClickListener() {
        listener?.fragmentClose(FragmentType.Play)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogListener = null
    }

    private fun initList() {
        viewManager = LinearLayoutManager(activity)
        filepickerAdapter = FilePickerAdapter(mutableListOf(), this)
        filepickerRecyclerView = activity!!.findViewById<RecyclerView>(R.id.files_list_view).apply {
            layoutManager = viewManager
            adapter = filepickerAdapter
        }
    }

    private fun initObservers() {
        val fileList = Observer<MutableList<File>> { list ->
            updateAdapter(list)
        }
        val currentName = Observer<String> { name -> updateToolbar(name) }
        viewModel.filesList.observe(activity!!, fileList)
        viewModel.currentFileName.observe(activity!!, currentName)
    }

    private fun updateToolbar(title: String) {
        title_filepicker_toolbar.text = title
        back_filepicker_toolbar.visibility = View.VISIBLE
        save_filepicker_toolbar.setOnClickListener {
            showDialog(getString(R.string.warning), getString(R.string.sure_for_save_folder_question), getString(
                R.string.yes), getString(R.string.cancel))
        }
        back_filepicker_toolbar.setOnClickListener {
            if (title.equals(FilePicker.rootFolder)) {
                listener?.fragmentClose(FragmentType.Playlist)
            } else {
                viewModel.updateListForParentFolder()
            }
        }
    }

    private fun updateAdapter(list: MutableList<File>) {
        (filepickerAdapter as FilePickerAdapter).filesList = list
        filepickerAdapter.notifyDataSetChanged()
    }
}