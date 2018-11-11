package com.example.s2784.layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class InviteFriend extends AppCompatActivity implements View.OnClickListener{

    private Button btn_invite_friend;
    private ArrayList<RoomInfo> friendlist;
    private ListViewAdapter listViewAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        friendlist = Tabs.testViewModel.getFriend();
        btn_invite_friend = findViewById(R.id.btn_InviteFriend);
        listView = findViewById(R.id.inviteFriend_friendList);
        listViewAdapter = new ListViewAdapter(this,friendlist);
        listView.setAdapter(listViewAdapter);

        btn_invite_friend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_invite_friend){
            this.finish();
        }
    }
}
