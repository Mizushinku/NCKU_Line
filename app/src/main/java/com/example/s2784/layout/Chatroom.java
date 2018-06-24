package com.example.s2784.layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Chatroom extends AppCompatActivity implements View.OnClickListener, LinkModule.MListener {

    private ImageButton btn;
    private ListView lv;
    private ArrayList<bubble> Bubble = new ArrayList<>();
    private bubble_list Bubble_list;
    private TextView status;
    private EditText et;

    private String id;
    private String code;

    private String chatName;
    private TextView txv_chatName;


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG","Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG","Restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Main.mqtt.setProcessingCode("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Log.d("TAG","Create");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        id = intent.getStringExtra("id");


        btn = findViewById(R.id.btn_send);
        lv = findViewById(R.id.lv);
        et = findViewById(R.id.et);
        status = findViewById(R.id.status);


        txv_chatName = findViewById(R.id.chatName);

        btn.setOnClickListener(this);

        LinkModule.getInstance().setListener(this);

        Main.mqtt.setProcessingCode(code);

        Main.mqtt.GetRecord(code);

        Bubble_list = new bubble_list(Chatroom.this,Bubble);
        lv.setAdapter(Bubble_list);

    }

    @Override
    public void onClick(View v) {
        if(v == btn) {
            String msg = code + "\t" + id + "\t" + et.getText().toString();
            if(!msg.equals("")){
                Main.mqtt.SendMessage(msg);
            }
            et.setText("");
        }
    }

    @Override
    public void updateMsg(String sender, String text) {
        if(sender.equals(id)) {
            Bubble.add(new bubble(1,text));
        }else {
            Bubble.add(new bubble(0,text));
        }
        Bubble_list.notifyDataSetChanged(lv,Bubble_list.getCount());
        lv.setSelection(Bubble_list.getCount());
    }

}