package com.example.s2784.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PlusDialog extends AppCompatDialogFragment {

    private EditText et_title;
    private EditText et_content;
    private PlusDialogListener plusDialogListener;
    private static Toast toast;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            plusDialogListener = (PlusDialogListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement PlusDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_plus,null);
        builder.setView(view)
                .setTitle("發佈新主題")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = et_title.getText().toString();
                        String content = et_content.getText().toString();
                        if (title != null && !title.equals("") && content != null && !content.equals(""))
                        {
                            plusDialogListener.applyTexts(title, content);
                        }
                        else
                        {
                            Context context = getActivity().getApplicationContext();
                            makeTextAndShow(context, "沒有主題或內容，請重新輸入", Toast.LENGTH_SHORT);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("選擇檔案", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                        picker.setType("*/*");
                        startActivity(picker);
                    }
                });


        et_title = view.findViewById(R.id.plus_et_title);
        et_content = view.findViewById(R.id.plus_et_content);
        return builder.create();
    }

    public interface PlusDialogListener{
        void applyTexts(String title, String content);
    }
    private static void makeTextAndShow(final Context context, final String text, final int duration)
    {
        if (toast == null)
        {
            //如果還沒有用過makeText方法，才使用
            toast = android.widget.Toast.makeText(context, text, duration);
        }
        else
        {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }
}