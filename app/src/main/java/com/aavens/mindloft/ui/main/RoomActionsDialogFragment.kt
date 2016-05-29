package com.aavens.mindloft.ui.main

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.aavens.mindloft.R
import com.aavens.mindloft.listeners.DialogFragmentActionsListener

class RoomActionsDialogFragment(index: Int, listener: DialogFragmentActionsListener?): DialogFragment() {

    constructor() : this(0, null) {
    }

    init {
        val args = Bundle()
        args.putInt(Args.index, index)
        arguments = args
    }

    private object Args {
        val index: String = "INDEX_ARG";
    }
    private var index: Int = index
    private var listener: DialogFragmentActionsListener? = listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        index = arguments.getInt(Args.index);
        val builder = AlertDialog.Builder(activity);
        builder.setTitle(R.string.choose_dialog_title);
        builder.setItems(
                R.array.room_actions_array,
                { dialogInterface: DialogInterface, i: Int -> listener?.onActionInteraction(index) })
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DialogFragmentActionsListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach();
        listener = null;
    }
}