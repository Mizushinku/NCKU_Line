package com.example.s2784.layout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Chatroom extends AppCompatActivity implements View.OnClickListener, LinkModule.MListener {

    String group_letters[] = {"群組成員","邀請好友"};
    int group_icons[] = { R.drawable.group_member,R.drawable.invite_friend};
    String friend_letters[] = {"群組成員"};
    int friend_icons[] = { R.drawable.group_member};


    private ImageButton btn;
    private Button slide_btn;
    private ListView lv;
    private ArrayList<bubble> Bubble = new ArrayList<>();
    private bubble_list Bubble_list;
    private EditText et;
    private RoomInfo roomInfo;
    private ArrayList<RoomInfo> friendlist;
    private android.support.v7.widget.Toolbar toolbar;
    private SlidingDrawer slidingDrawer;
    private Button handle_btn;
    private GridView gridView;
    private Grid_Adapter gridAdapter;

    private String id;
    private String code;

    private String memberID;



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
        getWindow().setBackgroundDrawableResource(R.drawable.chatroom3) ;
        Log.d("TAG","Create");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        id = intent.getStringExtra("id");
        roomInfo = intent.getParcelableExtra("roomInfo");
        friendlist = getIntent().getParcelableArrayListExtra("friendlist");

        toolbar = findViewById(R.id.chat_toolbar);
        if(roomInfo.getType().equals("F")){
            toolbar.setTitle(roomInfo.getRoomName());
        }else{
            toolbar.setTitle(roomInfo.getRoomName() + "(" + roomInfo.getMemberID().size() + ")");
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        btn = findViewById(R.id.btn_send);
        lv = findViewById(R.id.lv);
        et = findViewById(R.id.et);
        slide_btn = toolbar.findViewById(R.id.slide_btn);
        handle_btn = findViewById(R.id.handle_btn);
        slidingDrawer = findViewById(R.id.slide_drawer);
        slidingDrawer.close();
        gridView = findViewById(R.id.grid_view);

        if(roomInfo.getType().equals("F")){
            gridAdapter = new Grid_Adapter(this,friend_icons,friend_letters);
        }else if(roomInfo.getType().equals("G")){
            gridAdapter = new Grid_Adapter(this,group_icons,group_letters);
        }
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        memberID = "";
                        for (int i = 0; i < roomInfo.getMemberID().size(); i++) {
                            if (i == 0) {
                                memberID += roomInfo.getMemberID().get(i);
                            } else {
                                memberID += "," + roomInfo.getMemberID().get(i);
                            }
                        }
                        Toast.makeText(Chatroom.this, memberID, Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Intent invite_friend = new Intent(Chatroom.this, InviteFriend.class);
                        invite_friend.putExtra("code",roomInfo.getCode());
                        invite_friend.putParcelableArrayListExtra("friendlist", friendlist);
                        startActivity(invite_friend);
                        break;
                    default:
                        Toast.makeText(Chatroom.this, "Wrong", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        btn.setOnClickListener(this);
        slide_btn.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.btn_send :
                if (!et.getText().toString().equals("")) {
                    //發送聊天紀錄
                    String text = et.getText().toString().replace("\t","    ");
                    String msg = code + "\t" + id + "\t" + text;
                    Tabs.mqtt.SendMessage(msg);
                }
                et.setText("");
                break;
            case R.id.slide_btn:
                float deg = (slide_btn.getRotation() == 180F) ? 0F : 180F;
                slide_btn.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                slidingDrawer.animateToggle();
                if(slidingDrawer.isOpened()){
//                    Toast.makeText(Chatroom.this, "close", Toast.LENGTH_LONG).show();
                }else {
//                    Toast.makeText(Chatroom.this, "open", Toast.LENGTH_LONG).show();
                }
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

    @Override
    public void memberChange(String memberID) {
        roomInfo.getMemberID().clear();
        StringTokenizer split_member = new StringTokenizer(memberID,"-");
        while (split_member.hasMoreElements()){
            String member = split_member.nextToken();
            roomInfo.addMemberID(member);
        }
        toolbar.setTitle(roomInfo.getRoomName() + "(" + roomInfo.getMemberID().size() + ")");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}