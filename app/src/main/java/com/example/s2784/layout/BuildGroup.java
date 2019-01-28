package com.example.s2784.layout;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BuildGroup extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_BuildGroup = 2;

    private EditText etGroupName;
    //    private Button btn_selectFriend;
    private Button btn_CreateGroup;

    private TextView groupName;

    private ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        ListView listView;
        ArrayList<RoomInfo> friendlist;

        friendlist = getIntent().getParcelableArrayListExtra("friendlist");
        etGroupName = findViewById(R.id.etGroupName);
        btn_CreateGroup = findViewById(R.id.btn_CreateGroup);
        listView = findViewById(R.id.buildGroup_friendList);
        listViewAdapter = new ListViewAdapter(this, friendlist);
        listView.setAdapter(listViewAdapter);

        etGroupName.setOnClickListener(this);
//        btn_selectFriend.setOnClickListener(this);
        btn_CreateGroup.setOnClickListener(this);

//        etGroupName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String currentText = editable.toString();
//                int currentLength = currentText.length();
//                if(currentLength == 0){
//                    etGroupName.setHint("群組名稱");
//                }
//                else{
//                    etGroupName.setHint(null);
//                }
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        if (v == btn_CreateGroup) {
            if (!etGroupName.getText().toString().equals("")) {
                Intent intent = getIntent();
                intent.putExtra("groupName", etGroupName.getText().toString());
                intent.putStringArrayListExtra("memberList", listViewAdapter.getGroupMember());
                setResult(REQUEST_CODE_BuildGroup, intent);
                this.finish();
            } else {
                Toast.makeText(BuildGroup.this, "請輸入群組名稱", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
