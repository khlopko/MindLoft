package com.aavens.mindloft.ui.detail

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle

import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.ThingsTypeDialogListener

class ThingTypeDialogFragment: DialogFragment() {

    private var listener: ThingsTypeDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.choose_dialog_title)
        builder.setItems(R.array.thing_types_array) { dialog, which -> listener?.onThingTypeInteraction(which) }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ThingsTypeDialogListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {

        private val INDEX_ARG = "INDEX_ARG"

        fun newInstance(listener: ThingsTypeDialogListener): ThingTypeDialogFragment {
            val fragment = ThingTypeDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }
}
