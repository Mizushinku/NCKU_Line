package com.example.s2784.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PenDialog extends AppCompatDialogFragment {

    private EditText et_content;
    private PenDialogListener penDialogListener;
    private static Toast toast;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            penDialogListener = (PenDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement PenDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pen,null);
        builder.setView(view)
                .setTitle("回覆")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = et_content.getText().toString();

                        if (content != null && !content.equals(""))
                        {
                            penDialogListener.applyTexts(content);
                        }
                        else
                        {
                            Context context = getActivity().getApplicationContext();
                            makeTextAndShow(context, "沒有回覆訊息，請重新輸入", Toast.LENGTH_SHORT);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        et_content = view.findViewById(R.id.pen_et_content);
        return builder.create();
    }

    public interface PenDialogListener{
        void applyTexts(String content);
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
