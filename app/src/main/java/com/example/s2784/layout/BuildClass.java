package com.example.s2784.layout;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BuildClass extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_BuildGroup = 2;

    private EditText etGroupName;
    //    private Button btn_selectFriend;
    private Button btn_build_class;

    private TextView groupName;

    private ListViewAdapter listViewAdapter;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_class);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        else{
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color
        ListView listView;
        ArrayList<RoomInfo> friendlist;

        friendlist = getIntent().getParcelableArrayListExtra("frinedlist");
        etGroupName = findViewById(R.id.etGroupName);
        btn_build_class = findViewById(R.id.btn_CreateGroup);
        listView = findViewById(R.id.buildGroup_friendList);
        listViewAdapter = new ListViewAdapter(this, friendlist);
        //listView.setAdapter(listViewAdapter);

        etGroupName.setOnClickListener(this);
//        btn_selectFriend.setOnClickListener(this);
        btn_build_class.setOnClickListener(this);

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
        if (v == btn_build_class) {
            if (!etGroupName.getText().toString().equals("")) {
                Intent intent = getIntent();
                intent.putExtra("groupName", etGroupName.getText().toString());
                intent.putStringArrayListExtra("memberList", listViewAdapter.getGroupMember());
                setResult(REQUEST_CODE_BuildGroup, intent);
                this.finish();
            } else {
                Toast.makeText(BuildClass.this, "請輸入群組名稱", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
