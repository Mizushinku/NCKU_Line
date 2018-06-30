package com.example.s2784.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * 長按好友或群組清單時會出現的dialog
 * 因為功能相似所以只在這邊解釋
 * 使用繼承自Fragment的OnAttach方法來設定callback function 實作方
 * childPos為辨識被長按者位置的參數
 * 詳細請參照https://developer.android.com/guide/topics/ui/dialogs
 * ... 2018/6/30
 */

public class FriendLongClickDialogFragment extends DialogFragment{

    public interface FLCMListener {
        //which參數 用
        public void FLCM_onItemClick(DialogFragment dialog, int which, int childPos);
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

        //R.array.friendLongClickMenu 在res/values/strings.xml 定義
        builder.setTitle(R.string.FLCM_title).setItems(R.array.friendLongClickMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.FLCM_onItemClick(FriendLongClickDialogFragment.this, which, childPos);
            }
        });

        return builder.create();
    }
}
