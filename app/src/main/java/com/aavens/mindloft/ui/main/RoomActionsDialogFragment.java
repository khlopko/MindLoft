package com.aavens.mindloft.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.DialogFragmentActionsListener;


public class RoomActionsDialogFragment extends DialogFragment {

    private static final String INDEX_ARG = "INDEX_ARG";

    private DialogFragmentActionsListener listener;
    private Integer index;

    public static RoomActionsDialogFragment newInstance(Integer index, DialogFragmentActionsListener listener) {
        RoomActionsDialogFragment fragment = new RoomActionsDialogFragment();
        fragment.listener = listener;
        Bundle args = new Bundle();
        args.putInt(INDEX_ARG, index);
        fragment.setArguments(args);
        return fragment;
    }

    public RoomActionsDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        index = getArguments().getInt(INDEX_ARG);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_dialog_title);
        builder.setItems(R.array.room_actions_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onActionInteraction(index);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogFragmentActionsListener) {
            listener = (DialogFragmentActionsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
