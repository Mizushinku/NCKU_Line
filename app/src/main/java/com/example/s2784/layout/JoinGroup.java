package com.example.s2784.layout;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class JoinGroup extends AppCompatActivity {

    ArrayList<RoomInfo> groupList;
    private static final int REQUEST_CODE_JoinGroup = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);


//        RoomInfo rmInfo1 = new RoomInfo();
//        RoomInfo rmInfo2 = new RoomInfo();
//        RoomInfo rmInfo3 = new RoomInfo();
//        groupList = new ArrayList<>();
//
//        rmInfo1.setName("aaa");
//        rmInfo1.setStudentID("歡迎來到 aaa");
//        rmInfo1.setIcon(BitmapFactory.decodeResource( getResources(), R.drawable.bubble_out));
//
//        groupList.add(rmInfo1);
//
//        rmInfo2.setName("bbb");
//        rmInfo2.setStudentID("這裡是 bbb");
//        rmInfo2.setIcon(BitmapFactory.decodeResource( getResources(), R.drawable.bubble_in));
//
//        groupList.add(rmInfo2);
//
//        rmInfo3.setName("ccc");
//        rmInfo3.setIcon(BitmapFactory.decodeResource( getResources(), R.drawable.bubble_in));
//
//        groupList.add(rmInfo3);
//
//
//        ListView groupListView = findViewById(R.id.groupListView);
//        final ListViewAdapter listViewAdapter = new  ListViewAdapter(this, groupList);
//        groupListView.setAdapter(listViewAdapter);
//
//
//        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RoomInfo roomInfo = (RoomInfo) listViewAdapter.getItem(position);
//
//                Intent intent = getIntent();
//                intent.putExtra("groupName",roomInfo.getName());
//                setResult(REQUEST_CODE_JoinGroup, intent);
//                finish();
//            }
//        });

    }

    public void onClick(View v)
    {
        Intent intent = getIntent();
        intent.putExtra("groupName","aaa");
        setResult(REQUEST_CODE_JoinGroup, intent);
    }



}
