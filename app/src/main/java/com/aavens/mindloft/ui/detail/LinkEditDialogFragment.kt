package com.aavens.mindloft.ui.detail

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText

import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.LinkEditDialogActionsListener

class LinkEditDialogFragment: DialogFragment() {

    private var listener: LinkEditDialogActionsListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_link, null)
        val editText = view.findViewById(R.id.link_enter_text) as EditText
        builder.setView(view)
        builder.setPositiveButton(R.string.done) { dialog, id ->
            listener?.onDoneClick(editText.text.toString())
            this@LinkEditDialogFragment.dialog.cancel()
        }.setNegativeButton(R.string.cancel) { dialog, id -> this@LinkEditDialogFragment.dialog.cancel() }
        return builder.create()
    }

    companion object {

        fun newInstance(listener: LinkEditDialogActionsListener): LinkEditDialogFragment {
            val fragment = LinkEditDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }
}
