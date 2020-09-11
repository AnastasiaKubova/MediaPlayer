package com.example.musicplayer.helper

import android.R
import android.app.AlertDialog
import androidx.fragment.app.Fragment


open class BaseFragment : Fragment() {

    var listener: FragmentListener? = null
    var dialogListener: DialogInterface? = null

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

    interface DialogInterface {
        fun positiveClickListener()
        fun negativeClickListener()
    }
}