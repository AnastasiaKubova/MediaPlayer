package com.example.musicplayer.ui.filepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.helper.BaseFragment
import com.example.musicplayer.utility.FilePicker
import kotlinx.android.synthetic.main.toolbar_filepicker.*
import java.io.File

class FilePickerFragment : BaseFragment(), FilePickerAdapter.FileListener,
    BaseFragment.DialogInterface {

    private lateinit var filepickerRecyclerView: RecyclerView
    private lateinit var filepickerAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: FilePickerViewModel
    private var navController: NavController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.filepicker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilePickerViewModel::class.java)
        dialogListener = this
        navController = findNavController()

        /* Init callbacks. */
        initObservers()

        /* Init data. */
        initList()

        /* Init view. */
        viewModel.updateFileList()
        showBottomDialog(View.GONE)
    }

    override fun onFolderListener(file: File) {
        if (file.name.equals(FilePicker.DEFAULT_FOLDER)) {
            viewModel.updateListForParentFolder()
        } else {
            viewModel.updateListForSelectFolder(file)
        }
    }

    override fun positiveClickListener() {
        viewModel.saveFolder()
        navController?.navigateUp()
    }

    override fun negativeClickListener() {
        navController?.navigateUp()
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
        val fileList = Observer<MutableList<File>> { list ->
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
            navController?.navigateUp()
        }
    }

    private fun updateAdapter(list: MutableList<File>) {
        (filepickerAdapter as FilePickerAdapter).filesList = list
        filepickerAdapter.notifyDataSetChanged()
    }
}