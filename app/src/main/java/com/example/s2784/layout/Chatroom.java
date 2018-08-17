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
import java.util.StringTokenizer;

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
        Tabs.mqtt.setProcessingCode("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        getWindow().setBackgroundDrawableResource(R.drawable.chattingbackground) ;
        Log.d("TAG","Create");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        id = intent.getStringExtra("id");


        btn = findViewById(R.id.btn_send);
        lv = findViewById(R.id.lv);
        et = findViewById(R.id.et);
        status = findViewById(R.id.status); // not used


        txv_chatName = findViewById(R.id.chatName); // label on the top of the chat room

        btn.setOnClickListener(this);

        //設定該class為callback function 實作方
        LinkModule.getInstance().setListener(this);

        //設定正在執行的chat room
        Tabs.mqtt.setProcessingCode(code);

        //拿到聊天紀錄
        Tabs.mqtt.GetRecord(code);


        Bubble_list = new bubble_list(Chatroom.this,Bubble);
        lv.setAdapter(Bubble_list);

    }

    @Override
    public void onClick(View v) {
        if(v == btn) {
            if(!et.getText().toString().equals("")) {
                //發送聊天紀錄
                String msg = code + "\t" + id + "\t" + et.getText().toString();
                Tabs.mqtt.SendMessage(msg);
            }
            et.setText("");
        }
    }

    //callback function 實作
    @Override
    public void updateMsg(String sender, String text, String time) {
        if(sender.equals(id)) {
            Bubble.add(new bubble(1,text,sender,time));
        }else {
            Bubble.add(new bubble(0,text,sender,time,Tabs.mqtt.MapBitmap(sender)));
        }
        //更新一則訊息
        Bubble_list.notifyDataSetChanged(lv,Bubble_list.getCount());

        lv.setSelection(Bubble_list.getCount());
    }

    @Override
    public void fetchRecord(String record) {
        StringTokenizer stringTokenizer = new StringTokenizer(record,",");
        while(stringTokenizer.hasMoreElements()){
            String token = stringTokenizer.nextToken();
            String[] token_splitLine = token.split("\t");
            updateMsg(token_splitLine[0],token_splitLine[1],token_splitLine[2]);
        }
    }

}