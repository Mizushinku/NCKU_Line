package com.example.s2784.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarPlusDialog extends AppCompatDialogFragment {

    private Button date_choose;
    private Button time_choose;
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
//        //Set custom title
//        TextView customTitle = new TextView(getActivity());
//        customTitle.setText("新增行程!");
////        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
//        customTitle.setTextSize(24);
//        customTitle.setTextColor(Color.WHITE);
//        customTitle.setBackgroundColor(getResources().getColor(R.color.ncku_red));
//        //Set custom title

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.calendar_dialog_plus,null);

        date_choose =view.findViewById(R.id.choose_date);
        date_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getActivity().getSupportFragmentManager(), "DatePickerFragment");
            }
        });

        time_choose = view.findViewById(R.id.choose_time);
        time_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "TimePickerFragment");
            }
        });
        builder.setView(view)
                .setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
//                .setNeutralButton("選擇檔案", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
//                        picker.setType("*/*");
//                        startActivity(picker);
//                    }
//                });


//        et_title = view.findViewById(R.id.plus_et_title);
//        et_content = view.findViewById(R.id.plus_et_content);

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