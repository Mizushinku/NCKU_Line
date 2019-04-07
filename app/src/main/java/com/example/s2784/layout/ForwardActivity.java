package com.example.s2784.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ForwardActivity extends AppCompatActivity implements View.OnClickListener{

    protected static final int REQUEST_CODE_FORWARD = 2;

    private Button forward_button;
    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private ArrayList<RoomInfo> roomlist;
    private int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        roomlist = Tabs.testViewModel.getRoomList();
        index = getIntent().getIntExtra("index",-1);
        forward_button = findViewById(R.id.forward_btn);
        listView = findViewById(R.id.forward_roomlist);
        listViewAdapter = new ListViewAdapter(this, roomlist);
        listView.setAdapter(listViewAdapter);

        forward_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == forward_button){
            String member = "";
            for(int i=0;i<listViewAdapter.getCodeMember().size();i++){
                if(i==0){
                    member += listViewAdapter.getCodeMember().get(i);
                }else{
                    member += "," + listViewAdapter.getCodeMember().get(i);
                }
            }

            Intent intent = getIntent();
            intent.putExtra("index",index);
            intent.putExtra("member", member);
            setResult(REQUEST_CODE_FORWARD,intent);
            this.finish();
        }
    }
}
