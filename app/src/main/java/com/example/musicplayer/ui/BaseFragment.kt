package com.example.musicplayer.ui

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.bottom_fragments.*


open class BaseFragment : Fragment() {

    companion object {
        var listener: FragmentListener? = null
        var dialogListener: DialogInterface? = null
    }

    fun showDialog(title: String, message: String, positive: String, negative: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        builder
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(
                positive
            ) { dialog, value ->
                dialogListener?.positiveClickListener()
                dialog.cancel()
            }
            .setNegativeButton(
                negative
            ) { dialog, value ->
                dialogListener?.negativeClickListener()
                dialog.cancel()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showBottomDialog(visibility: Int) {
        requireActivity().bottom_menu_panel.visibility = visibility
    }

    interface DialogInterface {
        fun positiveClickListener()
        fun negativeClickListener()
    }
}