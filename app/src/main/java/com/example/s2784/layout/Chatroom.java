package com.example.s2784.layout;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.example.s2784.layout.Tabs.mqtt;
import static com.example.s2784.layout.Tabs.sipData;

public class Chatroom extends AppCompatActivity implements View.OnClickListener, LinkModule.MListener {

    private String group_letters[] = {"群組成員", "邀請好友", "選擇圖片"};
    private int group_icons[] = {R.drawable.group_member, R.drawable.invite_friend, R.drawable.pic};
    private String friend_letters[] = {"群組成員", "選擇圖片", "撥打電話"};
    private int friend_icons[] = {R.drawable.group_member, R.drawable.pic, R.drawable.call};

    protected ImageButton btn;
    protected Button slide_btn;
    protected ListView lv;
    protected ArrayList<Bubble> msgList = new ArrayList<>();
    protected BubbleAdapter bubbleAdapter;
    protected EditText et;
    protected RoomInfo roomInfo;
    protected android.support.v7.widget.Toolbar toolbar;
    protected SlidingDrawer slidingDrawer;
    protected Button handle_btn;
    protected GridView gridView;
    protected Grid_Adapter gridAdapter;

    protected String id;
    protected String code;

    protected String memberID;

    protected boolean isLoading = false;
    protected int cap = 12;
    protected int record_cnt = 0;
    protected int last_pk = 0;

    protected static final int REQUEST_CODE_CHOOSEPIC = 1;
    protected static final int REQUEST_CODE_FORWARD = 2;

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
        mqtt.setProcessingCode("");
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

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        id = intent.getStringExtra("id");
        roomInfo = intent.getParcelableExtra("roomInfo");
        //設定正在執行的chat room
        mqtt.setProcessingCode(code);

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
        toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("__No Name__");
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



        btn.setOnClickListener(this);
        slide_btn.setOnClickListener(this);
        //設定該class為callback function 實作方
        LinkModule.getInstance().setListener(this);

        //已讀
        SQLiteManager.badgeClear(code);

        if(mqtt.isConnected()) {
            startCreate();
        }


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
        gridView.setAdapter(gridAdapter);
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
                        case 2:
                            ToCall();
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
                    mqtt.SendMessage(msg);
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

    @Override
    public void startCreate() {
        if(roomInfo == null) {
            roomInfo = Tabs.testViewModel.getRoomInfo(code);
        }
        if (roomInfo.getType().equals("F")) {
            toolbar.setTitle(roomInfo.getRoomName());
        } else {
            toolbar.setTitle(roomInfo.getRoomName() + "(" + roomInfo.getMemberID().size() + ")");
        }

        Log.d("TAG", "in startCreat : roomName = " + roomInfo.getRoomName());

        set_gridAdapter(); //must be override by child class
        set_gridview_onItemClickListener(); //must be override by child class


        //拿到聊天紀錄
        ++record_cnt;
        mqtt.GetRecord(code, record_cnt, last_pk);

        bubbleAdapter = new BubbleAdapter(Chatroom.this, msgList, roomInfo);
        lv.setAdapter(bubbleAdapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE) {
                    if(view != null && view.getFirstVisiblePosition() == 0 && view.getTop() == 0) {
                        Log.d("CHAT", "is top");
                        if(!isLoading) {
                            isLoading = true;
                            ++record_cnt;
                            mqtt.GetRecord(code, record_cnt, last_pk);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    //callback function 實作
    @Override
    public void updateMsg(String sender, String text, String time, int mod) {
        if (sender.equals(id)) {
            if(mod == 0) {
                msgList.add(0, new Bubble(1, 0, text, sender, time));
            }
            else {
                msgList.add(new Bubble(1, 0, text, sender, time));
            }
            //msgList.add(0, new Bubble(1, 0, text, sender, time));
        } else {
            if(mod == 0) {
                msgList.add(0, new Bubble(0, 0, text, sender, time, mqtt.MapBitmap(sender)));
            }
            else {
                msgList.add(new Bubble(0, 0, text, sender, time, mqtt.MapBitmap(sender)));
            }
        }
        //更新一則訊息
        bubbleAdapter.notifyDataSetChanged(lv, bubbleAdapter.getCount());
        if(mod == 0) {
            lv.setSelection(cap - 1);
        }
        else {
            lv.setSelection(bubbleAdapter.getCount());
        }
    }

    @Override
    public void updateImg(String sender, Bitmap image, String time, int mod) {
        if (sender.equals(id)) {
            if(mod == 0) {
                msgList.add(0, new Bubble(1, 1, image, sender, time));
            }
            else {
                msgList.add(new Bubble(1, 1, image, sender, time));
            }
        } else {
            if(mod == 0) {
                msgList.add(0, new Bubble(0, 1, image, sender, time, mqtt.MapBitmap(sender)));
            }
            else {
                msgList.add(new Bubble(0, 1, image, sender, time, mqtt.MapBitmap(sender)));
            }
        }
        bubbleAdapter.notifyDataSetChanged(lv, bubbleAdapter.getCount());
        if(mod == 0) {
            lv.setSelection(cap - 1);
            Log.d("CHAT", "set selecttion in mod 0 : " + (cap-1));
        }
        else {
            lv.setSelection(bubbleAdapter.getCount());
            Log.d("CHAT", "set selecttion in mod 1 : " + bubbleAdapter.getCount());
        }
        if (image != null) {
            findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    @Override
    public void updateImg(Bitmap image, int pos) {
        while(pos < this.getMsgListCount()) {
            if(msgList.get(pos).getData_t() == 1) {
                msgList.get(pos).setImage(image);
                break;
            }
            else {
                pos += cap;
            }
        }
        //bubbleAdapter.notifyDataSetChanged(lv, pos);
    }

    @Override
    public void fetchRecord(String record) {
        new FetchRecord(this).execute(record);
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
        bubbleAdapter.notifyDataSetChanged();
        //lv.setSelection(bubbleAdapter.getCount());
        try {
            lv.setSelection(cap - 1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    protected  void ToCall()
    {
        try {
            if (sipData.manager.isRegistered(sipData.me.getUriString())) {
                String id = roomInfo.getMemberID().get(1);
                Intent calloutActivity = new Intent(Chatroom.this, CallingOutActivity.class);
                calloutActivity.putExtra("sipUserName", mqtt.MapPhoneNum(id));
                calloutActivity.putExtra("Name", mqtt.MapAlias(id));
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                mqtt.MapBitmap(id).compress(Bitmap.CompressFormat.JPEG,100,bs);
                calloutActivity.putExtra("avatar", bs.toByteArray());

                startActivity(calloutActivity);
            }else{
                sipData.closeLocalProfile();
                sipData.initializeManager();
                Toast.makeText(this, "Unregister to sip server!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            Log.d("SIP", e.getMessage());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case RESULT_OK:
                Uri uri = data.getData();
                new SendingImg(this).execute(uri);
                break;
            case REQUEST_CODE_FORWARD:
                int index = data.getIntExtra("index",-1);
                String code_member = data.getStringExtra("member");
                if(msgList.get(index).getData_t() == 0) { // text
                    mqtt.forwardTXT(code_member, msgList.get(index).getTxtMsg());
                }else {
                    new ForwardIMG(this).execute(msgList.get(index).getImage(), code_member);
                }
                break;
        }
    }

    protected static class SendingImg extends AsyncTask<Uri, Void, Void> {

        private WeakReference<Chatroom> roomWeakReference;

        private SendingImg(Chatroom chatroom) {
            roomWeakReference = new WeakReference<>(chatroom);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            roomWeakReference.get().findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
            roomWeakReference.get().findViewById(R.id.progressBar_img).bringToFront();
        }

        @Override
        protected Void doInBackground(Uri... params) {
            Uri uri = params[0];
            mqtt.SendImg(uri, roomWeakReference.get().code, R.integer.SEND_IMG_M1);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(roomWeakReference.get() == null) {
                return;
            }
            //findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    protected static class FetchRecord extends AsyncTask<String, Void, Void> {

        private WeakReference<Chatroom> roomWeakReference;

        private FetchRecord(Chatroom chatroom) {
            roomWeakReference = new WeakReference<>(chatroom);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            roomWeakReference.get().findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
            roomWeakReference.get().findViewById(R.id.progressBar_img).bringToFront();
        }

        @Override
        protected Void doInBackground(String... params) {
            String record = params[0];
            StringTokenizer stringTokenizer = new StringTokenizer(record, "\r");
            int i = 0;
            while (stringTokenizer.hasMoreElements()) {
                String token = stringTokenizer.nextToken();
                if(i == 0)
                {
                    int last_pk = Integer.parseInt(token);
                    roomWeakReference.get().setLast_pk(last_pk);
                    ++i;
                    continue;
                }
                String[] token_splitLine = token.split("\t");
                //if type == 'text'
                if (token_splitLine[3].equals("text")) {
                    roomWeakReference.get().updateMsg(token_splitLine[0], token_splitLine[1], token_splitLine[2], 0);
                } else if (token_splitLine[3].equals("img")) {
                    roomWeakReference.get().updateImg(token_splitLine[0], null, token_splitLine[2], 0);
                    int pos = roomWeakReference.get().getCap() - i;
                    mqtt.RecordImgBack(token_splitLine[1], pos);
                }
                ++i;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(roomWeakReference.get() == null) {
                return;
            }
            roomWeakReference.get().findViewById(R.id.progressBar_img).setVisibility(View.GONE);
            roomWeakReference.get().setLoading(false);
        }
    }

    protected static class ForwardIMG extends AsyncTask<Object, Void, Void> {

        private WeakReference<Chatroom> roomWeakReference;

        private ForwardIMG(Chatroom chatroom) {
            roomWeakReference = new WeakReference<>(chatroom);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            roomWeakReference.get().findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
            roomWeakReference.get().findViewById(R.id.progressBar_img).bringToFront();
        }

        @Override
        protected Void doInBackground(Object... params) {
            Bitmap bitmap = (Bitmap) params[0];
            String codes = (String) params[1];
            mqtt.forwardIMG(codes,bitmap);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(roomWeakReference.get() == null) {
                return;
            }
            roomWeakReference.get().findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }

    @Override
    public void setAuth(int auth) {
    }

    public void forwardMSG(int index){
         Intent forward = new Intent(Chatroom.this, ForwardActivity.class);
         forward.putExtra("index",index);
         startActivityForResult(forward,REQUEST_CODE_FORWARD);
    }

    protected void setLoading(boolean loading) {
        isLoading = loading;
    }

    protected void setLast_pk(int pk) {
        last_pk = pk;
    }

    protected int getMsgListCount() {
        return this.msgList.size();
    }

    protected int getCap() {
        return cap;
    }

}

