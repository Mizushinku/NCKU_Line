package com.example.s2784.layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BuildGroup extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_SELECT_FRIEND = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;

    private EditText etGroupName;
    private Button btn_selectFriend;
    private Button btn_CreateGroup;
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

        friendlist = getIntent().getParcelableArrayListExtra("friendlist");
        etGroupName = findViewById(R.id.etGroupName);
        btn_selectFriend = findViewById(R.id.btn_selectFriend);
        btn_CreateGroup = findViewById(R.id.btn_CreateGroup);

        etGroupName.setOnClickListener(this);
        btn_selectFriend.setOnClickListener(this);
        btn_CreateGroup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btn_selectFriend) {
            Intent intent_selectFriend = new Intent(BuildGroup.this, SelectFriendIntoGroup.class);
            //intent_selectFriend.putExtra("friendList", (Serializable) friendList);
            intent_selectFriend.putParcelableArrayListExtra("friendlist",friendlist);
            startActivityForResult(intent_selectFriend, REQUEST_CODE_SELECT_FRIEND);
        } else if (v == btn_CreateGroup) {
            Intent intent = getIntent();
            intent.putExtra("groupName", etGroupName.getText().toString());
            intent.putStringArrayListExtra("memberList", groupMember);
            setResult(REQUEST_CODE_BuildGroup, intent);
            this.finish();
        } else if (v == etGroupName) {
            etGroupName.setHint(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_CODE_SELECT_FRIEND:
                //拿到user想加入群組的成員清單(學號)
                groupMember = data.getStringArrayListExtra("memberList");
                for (int i = 0; i < groupMember.size(); i++) {
                    Log.d("Array", "Array:" + groupMember.get(i));
                }
                break;
        }
    }
}
