package com.example.s2784.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PlusDialog extends AppCompatDialogFragment {

    private EditText et_title;
    private EditText et_content;
    private PlusDialogListener plusDialogListener;

    AlertDialog dialogDemo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            plusDialogListener = (PlusDialogListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement PlusDialogListener");
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_plus,null);
//        builder.setView(view)
//                .setTitle("發佈新主題")
//                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String title = et_title.getText().toString();
//                        String content = et_content.getText().toString();
//                        if (title != null && !title.equals("") && content != null && !content.equals(""))
//                        {
//                            plusDialogListener.applyTexts(title, content);
//                        }
//                        else
//                        {
//                            Context context = getActivity().getApplicationContext();
//                            makeTextAndShow(context, "沒有主題或內容，請重新輸入", Toast.LENGTH_SHORT);
//                        }
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setNeutralButton("選擇檔案", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
//                        picker.setType("*/*");
//                        startActivity(picker);
//                    }
//                });
//
//
//        et_title = view.findViewById(R.id.plus_et_title);
//        et_content = view.findViewById(R.id.plus_et_content);
//        return builder.create();
//    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_plus,null);
        dialogBuild.setTitle("發佈新主題");
        dialogBuild.setView(view);
        dialogBuild.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                if (title != null && !title.equals("") && content != null && !content.equals("")) {
                    plusDialogListener.applyTexts(title, content);
                    //該條件下關閉dialog
                    closeDialog(dialogDemo);
                    //此句最好不要省略，否則在有些情況下dialog不關閉
                    dialog.dismiss();

                } else {
                    //該條件下不關閉dialog
                    keepDialogOpen(dialogDemo);
                    Snackbar.make(view, "沒有主題或內容，請重新輸入", Snackbar.LENGTH_SHORT).show();
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

        dialogBuild.setNeutralButton("選擇檔案", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //this will crash the app
//                Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
//                picker.setType("*/*");
//                startActivity(picker);
            }
        });
        et_title = view.findViewById(R.id.plus_et_title);
        et_content = view.findViewById(R.id.plus_et_content);
        dialogDemo = dialogBuild.create();
        dialogDemo.show();

    }

    public interface PlusDialogListener{
        void applyTexts(String title, String content);
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