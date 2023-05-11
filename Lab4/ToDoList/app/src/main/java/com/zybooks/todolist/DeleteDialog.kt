package com.zybooks.todolist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteDialog: DialogFragment() {
    interface OnYesClickListener {
        fun onYesClick()
    }

    private lateinit var listener: OnYesClickListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete List Items")
        builder.setMessage("Confirm?")
        builder.setPositiveButton("Yes") { _, _ ->
            listener.onYesClick()
        }
        builder.setNegativeButton("No", null)
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnYesClickListener
    }
}