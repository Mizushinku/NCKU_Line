package com.example.s2784.layout;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendIntoGroup extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_FRIEND = 1;


    private ImageView photo;
    private TextView name;

    private ListView listView;
    private Button btn_OK;

    public ArrayList<String> groupMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_into_group);

        //Intent intentFromBuildGroup = getIntent();
        //friendList = (List<RoomInfo>) intentFromBuildGroup.getSerializableExtra("friendList");

        listView = findViewById(R.id.listView);
        final ListViewAdapter listViewAdapter = new  ListViewAdapter(this, Main.friendList);
        listView.setAdapter(listViewAdapter);

        btn_OK = findViewById(R.id.btn_OK);

        groupMember = new ArrayList<>();

        // change color when click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomInfo roomInfo = (RoomInfo) listViewAdapter.getItem(position);

                //選取成員時給user的回饋(被選中的人顏色會改變)
                if(!groupMember.contains(roomInfo.getStudentID())) {
                    view.setBackgroundColor(Color.BLUE);
                    groupMember.add(roomInfo.getStudentID());
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                    groupMember.remove(roomInfo.getStudentID());
                }

            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putStringArrayListExtra("memberList",groupMember);
                setResult(REQUEST_CODE_SELECT_FRIEND,intent);
                finish();
            }
        });
    }
}
