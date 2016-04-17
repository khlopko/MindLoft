package com.aavens.mindloft.ui.detail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.ThingsTypeDialogListener;

public class ThingTypeDialogFragment extends DialogFragment {

    private static final String INDEX_ARG = "INDEX_ARG";

    private ThingsTypeDialogListener listener;

    public static ThingTypeDialogFragment newInstance(ThingsTypeDialogListener listener) {
        ThingTypeDialogFragment fragment = new ThingTypeDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    public ThingTypeDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_dialog_title);
        builder.setItems(R.array.thing_types_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onThingTypeInteraction(which);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ThingsTypeDialogListener) {
            listener = (ThingsTypeDialogListener) context;
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
