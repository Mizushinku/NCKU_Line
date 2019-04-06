package com.example.s2784.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PenDialog extends AppCompatDialogFragment {

    private EditText et_content;
    private PenDialogListener penDialogListener;

    AlertDialog dialogDemo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            penDialogListener = (PenDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement PenDialogListener");
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_pen,null);
//        builder.setView(view)
//                .setTitle("回覆")
//                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String content = et_content.getText().toString();
//                        if (content != null && !content.equals(""))
//                        {
//                            penDialogListener.applyTexts(content);
//                        }
//                        else
//                        {
//                            Context context = getActivity().getApplicationContext();
//                            makeTextAndShow(context, "沒有回覆訊息，請重新輸入", Toast.LENGTH_SHORT);
//                        }
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//        et_content = view.findViewById(R.id.pen_et_content);
//        return builder.create();
//    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_pen,null);
        dialogBuild.setCancelable(false);
        dialogBuild.setTitle("回覆");
        dialogBuild.setView(view);
        dialogBuild.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = et_content.getText().toString();
                if (content != null && !content.equals("")) {
                    penDialogListener.applyTexts(content);
                    //該條件下關閉dialog
                    closeDialog(dialogDemo);
                    //此句最好不要省略，否則在有些情況下dialog不關閉
                    dialog.dismiss();

                } else {
                    //該條件下不關閉dialog
                    keepDialogOpen(dialogDemo);
                    Snackbar.make(view, "請輸入回覆訊息", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuild.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消按鈕在任何條件下都關閉
                closeDialog(dialogDemo);
                //此句最好不要省略，否則在有些情況下dialog不關閉
                dialog.dismiss();
            }
        });

        et_content = view.findViewById(R.id.pen_et_content);
        dialogDemo = dialogBuild.create();
        dialogDemo.show();

    }

    public interface PenDialogListener{
        void applyTexts(String content);
    }

    //保持dialog不關閉的方法
    private void keepDialogOpen(AlertDialog dialog) {
        try {
            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //關閉dialog的方法
    private void closeDialog(AlertDialog dialog) {
        try {
            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
