package com.example.s2784.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class GroupLongClickDialogFragment extends DialogFragment {

    public interface GLCMListener {
        public void GLCM_onItemClick(DialogFragment dialog, int which, int childPos);
    }

    GLCMListener mListener;

    int childPos;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (GLCMListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement GLCMListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(getArguments() != null) {
            childPos = getArguments().getInt("childPos");
        }

        builder.setTitle(R.string.GLCM_title).setItems(R.array.groupLongClickMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.GLCM_onItemClick(GroupLongClickDialogFragment.this, which, childPos);
            }
        });

        return builder.create();
    }
}
