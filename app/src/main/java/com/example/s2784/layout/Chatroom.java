package com.example.s2784.layout;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Chatroom extends AppCompatActivity implements View.OnClickListener, LinkModule.MListener {

    private String group_letters[] = {"群組成員", "邀請好友", "選擇圖片"};
    private int group_icons[] = {R.drawable.group_member, R.drawable.invite_friend, R.drawable.pic};
    private String friend_letters[] = {"群組成員", "選擇圖片"};
    private int friend_icons[] = {R.drawable.group_member, R.drawable.pic};
    private TestViewModel testViewModel;

    protected ImageButton btn;
    protected Button slide_btn;
    protected ListView lv;
    protected ArrayList<bubble> Bubble = new ArrayList<>();
    protected bubble_list Bubble_list;
    protected EditText et;
    protected RoomInfo roomInfo;
    protected ArrayList<RoomInfo> friendlist;
    protected android.support.v7.widget.Toolbar toolbar;
    protected SlidingDrawer slidingDrawer;
    protected Button handle_btn;
    protected GridView gridView;
    protected Grid_Adapter gridAdapter;



    protected String id;
    protected String code;

    protected String memberID;

    protected static final int REQUEST_CODE_CHOOSEPIC = 1;


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "Restart");
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
    protected void onStart() {
        super.onStart();
        FCM_MessageService.setVisibleRoomCode(code);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FCM_MessageService.setVisibleRoomCode("");
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        getWindow().setBackgroundDrawableResource(R.drawable.bg5);
        Log.d("TAG", "Create");


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        id = intent.getStringExtra("id");
        roomInfo = intent.getParcelableExtra("roomInfo");
        friendlist = getIntent().getParcelableArrayListExtra("friendlist");
        testViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        toolbar = findViewById(R.id.chat_toolbar);
        if (roomInfo.getType().equals("F")) {
            toolbar.setTitle(roomInfo.getRoomName());
        } else {
            toolbar.setTitle(roomInfo.getRoomName() + "(" + roomInfo.getMemberID().size() + ")");
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
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

        // bubble manipulation





        set_gridAdapter(); //must be override by child class
        gridView.setAdapter(gridAdapter);
        set_gridview_onItemClickListener(); //must be override by child class

        btn.setOnClickListener(this);
        slide_btn.setOnClickListener(this);
        //設定該class為callback function 實作方
        LinkModule.getInstance().setListener(this);

        //設定正在執行的chat room
        Tabs.mqtt.setProcessingCode(code);

        //拿到聊天紀錄
        Tabs.mqtt.GetRecord(code);

        //已讀
        SQLiteManager.badgeClear(code);

        Bubble_list = new bubble_list(Chatroom.this, Bubble);
        lv.setAdapter(Bubble_list);
//        bubble_left.setOnLongClickListener(this);
//        bubble_left_nodate.setOnLongClickListener(this);
//        bubble_right.setOnLongClickListener(this);
//        bubble_right_nodate.setOnLongClickListener(this);
    }

    //must be override by child class
    protected void set_gridAdapter() {
        if (roomInfo.getType().equals("F")) {
            gridAdapter = new Grid_Adapter(this, friend_icons, friend_letters);
        } else if (roomInfo.getType().equals("G")) {
            gridAdapter = new Grid_Adapter(this, group_icons, group_letters);
        }
    }

    //must be override by child class
    protected void set_gridview_onItemClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (roomInfo.getType().equals("G")) {
                    switch (position) {
                        case 0:
                            toastMembes();
                            break;
                        case 1:
                            inviteFriend();
                            break;
                        case 2:
                            choosePic();
                            break;
                        default:
                            Toast.makeText(Chatroom.this, "Wrong", Toast.LENGTH_LONG).show();
                            break;
                    }
                } else if (roomInfo.getType().equals("F")) {
                    switch (position) {
                        case 0:
                            toastMembes();
                            break;
                        case 1:
                            choosePic();
                            break;
                        default:
                            Toast.makeText(Chatroom.this, "Wrong", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (!et.getText().toString().trim().equals("")) {
                    //發送聊天紀錄
                    String text = et.getText().toString().replace("\t", "    ");
                    String msg = code + "\t" + id + "\t" + text;
                    Tabs.mqtt.SendMessage(msg);
                }
                et.setText("");
                break;
            case R.id.slide_btn:
                float deg = (slide_btn.getRotation() == 180F) ? 0F : 180F;
                slide_btn.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
                slidingDrawer.animateToggle();
                break;
        }

    }



    //callback function 實作
    @Override
    public void updateMsg(String sender, String text, String time) {
        if (sender.equals(id)) {
            Bubble.add(new bubble(1, 0, text, sender, time));
        } else {
            Bubble.add(new bubble(0, 0, text, sender, time, Tabs.mqtt.MapBitmap(sender)));
        }
        //更新一則訊息
        Bubble_list.notifyDataSetChanged(lv, Bubble_list.getCount());
        lv.setSelection(Bubble_list.getCount());
    }

    @Override
    public void updateImg(String sender, Bitmap image, String time) {
        if (sender.equals(id)) {
            Bubble.add(new bubble(1, 1, image, sender, time));
        } else {
            Bubble.add(new bubble(0, 1, image, sender, time, Tabs.mqtt.MapBitmap(sender)));
        }
        Bubble_list.notifyDataSetChanged(lv, Bubble_list.getCount());
        lv.setSelection(Bubble_list.getCount());
        if (image != null) {
            findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    @Override
    public void updateImg(Bitmap image, int pos) {
        Bubble.get(pos).setImage(image);
        //Bubble_list.notifyDataSetChanged(lv, pos);
    }

    @Override
    public void fetchRecord(String record) {
        new FetchRecord().execute(record);
    }

    @Override
    public void memberChange(String memberID) {
        roomInfo.getMemberID().clear();
        StringTokenizer split_member = new StringTokenizer(memberID, "-");
        while (split_member.hasMoreElements()) {
            String member = split_member.nextToken();
            roomInfo.addMemberID(member);
        }
        toolbar.setTitle(roomInfo.getRoomName() + "(" + roomInfo.getMemberID().size() + ")");
    }

    @Override
    public void refreshListView() {
        Bubble_list.notifyDataSetChanged();
        lv.setSelection(Bubble_list.getCount());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void inviteFriend() {
        Intent invite_friend = new Intent(Chatroom.this, InviteFriend.class);
        invite_friend.putExtra("code", roomInfo.getCode());
        invite_friend.putParcelableArrayListExtra("friendlist", friendlist);
        startActivity(invite_friend);
    }

    protected void toastMembes() {
        memberID = "";
        for (int i = 0; i < roomInfo.getMemberID().size(); i++) {
            if (i == 0) {
                memberID += roomInfo.getMemberID().get(i);
            } else {
                memberID += "," + roomInfo.getMemberID().get(i);
            }
        }
        Toast.makeText(Chatroom.this, memberID, Toast.LENGTH_LONG).show();
    }

    protected void choosePic() {
        /*
        Intent intent = new Intent(Chatroom.this, PicImageTest.class);
        startActivity(intent);
        */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_CHOOSEPIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            new SendingImg().execute(uri);
        }
    }


    protected class SendingImg extends AsyncTask<Uri, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar_img).bringToFront();
        }

        @Override
        protected Void doInBackground(Uri... params) {
            Uri uri = params[0];
            Tabs.mqtt.SendImg(uri, code);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            //findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    protected class FetchRecord extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar_img).bringToFront();
        }

        @Override
        protected Void doInBackground(String... params) {
            String record = params[0];
            StringTokenizer stringTokenizer = new StringTokenizer(record, "\r");
            int i = 0;
            while (stringTokenizer.hasMoreElements()) {
                String token = stringTokenizer.nextToken();
                String[] token_splitLine = token.split("\t");
                //if type == 'text'
                if (token_splitLine[3].equals("text")) {
                    updateMsg(token_splitLine[0], token_splitLine[1], token_splitLine[2]);
                } else if (token_splitLine[3].equals("img")) {
                    updateImg(token_splitLine[0], null, token_splitLine[2]);
                    Tabs.mqtt.RecordImgBack(token_splitLine[1], i);
                }
                ++i;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    @Override
    public void setAuth(int auth) {
    }

}

