package com.example.s2784.layout;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BuildGroup extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_SELECT_FRIEND = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;

    private EditText etGroupName;
//    private Button btn_selectFriend;
    private Button btn_CreateGroup;
    private ListView listView;

    private ArrayList<String> groupMember;
    private ArrayList<RoomInfo> friendlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_group);

        //Intent intentFromMain = getIntent();
        //Bundle bundle = intentFromMain.getBundleExtra("BUNDLE");
        //friendList = (ArrayList<RoomInfo>) bundle.getSerializable("A");
        //friendList = (ArrayList<RoomInfo>)intentFromMain.getSerializableExtra("friendList");
        groupMember = new ArrayList<String>();
        friendlist = getIntent().getParcelableArrayListExtra("friendlist");
        etGroupName = findViewById(R.id.etGroupName);
//        btn_selectFriend = findViewById(R.id.btn_selectFriend);
        btn_CreateGroup = findViewById(R.id.btn_CreateGroup);
        listView = findViewById(R.id.buildGroup_friendList);
        final ListViewAdapter listViewAdapter = new ListViewAdapter(this,friendlist);
        listView.setAdapter(listViewAdapter);

        // change color when click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomInfo roomInfo = (RoomInfo) listViewAdapter.getItem(position);
                CheckedTextView checkedTextView = view.findViewById(R.id.name);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                //選取成員時給user的回饋(被選中的人顏色會改變)
                if(!groupMember.contains(roomInfo.getStudentID())) {
//                    view.setBackgroundColor(Color.BLUE);
                    groupMember.add(roomInfo.getStudentID());
                } else {
//                    view.setBackgroundColor(Color.TRANSPARENT);
                    groupMember.remove(roomInfo.getStudentID());
                }

            }
        });

        etGroupName.setOnClickListener(this);
//        btn_selectFriend.setOnClickListener(this);
        btn_CreateGroup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
//        if (v == btn_selectFriend) {
//            Intent intent_selectFriend = new Intent(BuildGroup.this, SelectFriendIntoGroup.class);
//            //intent_selectFriend.putExtra("friendList", (Serializable) friendList);
//            intent_selectFriend.putParcelableArrayListExtra("friendlist",friendlist);
//            startActivityForResult(intent_selectFriend, REQUEST_CODE_SELECT_FRIEND);
//        } else
        if (v == btn_CreateGroup) {
            if(!etGroupName.getText().toString().equals("")){
                Intent intent = getIntent();
                intent.putExtra("groupName", etGroupName.getText().toString());
                intent.putStringArrayListExtra("memberList", groupMember);
                setResult(REQUEST_CODE_BuildGroup, intent);
                this.finish();
            }else{
                Toast.makeText(BuildGroup.this,"請輸入群組名稱", Toast.LENGTH_SHORT).show();
            }
        } else if (v == etGroupName) {
            etGroupName.setHint(null);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (resultCode) {
//            case REQUEST_CODE_SELECT_FRIEND:
//                //拿到user想加入群組的成員清單(學號)
//                groupMember = data.getStringArrayListExtra("memberList");
//                for (int i = 0; i < groupMember.size(); i++) {
//                    Log.d("Array", "Array:" + groupMember.get(i));
//                }
//                break;
//        }
//    }
}
