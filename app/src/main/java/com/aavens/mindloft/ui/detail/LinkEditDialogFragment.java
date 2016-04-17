package com.aavens.mindloft.ui.detail;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aavens.mindloft.R;
import com.aavens.mindloft.listeners.LinkEditDialogActionsListener;

public class LinkEditDialogFragment extends DialogFragment {

    private LinkEditDialogActionsListener listener;

    public static LinkEditDialogFragment newInstance(LinkEditDialogActionsListener listener) {
        LinkEditDialogFragment fragment = new LinkEditDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    public LinkEditDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_link, null);
        final EditText editText = (EditText) view.findViewById(R.id.link_enter_text);
        builder.setView(view);
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDoneClick(editText.getText().toString());
                        LinkEditDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LinkEditDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
