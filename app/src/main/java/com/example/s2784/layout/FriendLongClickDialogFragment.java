package com.example.s2784.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class FriendLongClickDialogFragment extends DialogFragment{

    public interface FLCMListener {
        public void onItemClick(DialogFragment dialog, int which, int childPos);
    }

    FLCMListener mListener;

    int childPos;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (FLCMListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FLCMListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(getArguments() != null) {
            childPos = getArguments().getInt("childPos");
        }

        builder.setTitle(R.string.FLCM_title).setItems(R.array.friendLongClickMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onItemClick(FriendLongClickDialogFragment.this, which, childPos);
            }
        });

        return builder.create();
    }
}
