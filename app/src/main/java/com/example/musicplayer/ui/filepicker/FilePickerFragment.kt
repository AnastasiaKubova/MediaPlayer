package com.example.musicplayer.ui.filepicker

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
import com.example.musicplayer.enum.FileType
import com.example.musicplayer.ui.BaseFragment
import com.example.musicplayer.ui.filepicker.model.FileData
import kotlinx.android.synthetic.main.toolbar_filepicker.*

class FilePickerFragment : BaseFragment(), FilePickerAdapter.FileListener,
    BaseFragment.DialogInterface {

    private lateinit var filepickerRecyclerView: RecyclerView
    private lateinit var filepickerAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val viewModel by viewModels<FilePickerViewModel>()
    private var navController: NavController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.filepicker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialogListener = this
        navController = findNavController()

        /* Init callbacks. */
        initObservers()

        /* Init data. */
        initList()

        /* Init view. */
        viewModel.updateFileList()
    }

    override fun onFolderListener(file: FileData) {
        if (file.fileType.equals(FileType.None)) {
            viewModel.updateListForParentFolder()
        } else {
            viewModel.updateListForSelectFolder(file)
        }
    }

    override fun positiveClickListener() {
        viewModel.saveFolder()
        openPreviewFragment()
    }

    override fun negativeClickListener() {
        openPreviewFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogListener = null
    }

    private fun initList() {
        viewManager = LinearLayoutManager(activity)
        filepickerAdapter = FilePickerAdapter(mutableListOf(), this)
        filepickerRecyclerView = requireActivity().findViewById<RecyclerView>(R.id.files_list_view).apply {
            layoutManager = viewManager
            adapter = filepickerAdapter
        }
    }

    private fun initObservers() {
        val fileList = Observer<MutableList<FileData>> { list ->
            updateAdapter(list)
        }
        val currentName = Observer<String> { name -> updateToolbar(name) }
        viewModel.filesList.observe(requireActivity(), fileList)
        viewModel.currentFileName.observe(requireActivity(), currentName)
    }

    private fun updateToolbar(title: String) {
        title_filepicker_toolbar.text = title
        back_filepicker_toolbar.visibility = View.VISIBLE
        save_filepicker_toolbar.setOnClickListener {
            showDialog(getString(R.string.warning), getString(R.string.sure_for_save_folder_question), getString(
                R.string.yes), getString(R.string.cancel))
        }
        back_filepicker_toolbar.setOnClickListener {
            openPreviewFragment()
        }
    }

    private fun updateAdapter(list: MutableList<FileData>) {
        (filepickerAdapter as FilePickerAdapter).filesList = list
        filepickerAdapter.notifyDataSetChanged()
    }

    private fun openPreviewFragment() {
        navController?.popBackStack()
        navController?.navigate(R.id.viewPagerFragment)
    }
}