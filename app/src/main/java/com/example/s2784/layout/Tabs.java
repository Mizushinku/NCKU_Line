package com.example.s2784.layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import q.rorbin.badgeview.QBadgeView;


public class Tabs extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener, Tab3.OnFragmentInteractionListener, Tab4.OnFragmentInteractionListener, FriendLongClickDialogFragment.FLCMListener, GroupLongClickDialogFragment.GLCMListener, LinkModule.RMSGListener{

    private ArrayList<RoomInfo> arrayList= new ArrayList<>(); /*for search view*/

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NetworkInfo mNetworkInfo;
    private ConnectivityManager mConnectivityManager;
    private QBadgeView qBadgeView;
    private MyBroadcastReceiver receiver;

//    private String tab_string[] = {"個人頁面","聊天","公佈欄","其他"};
//    private int tab_icon_light[] = {R.drawable.friend,R.drawable.chat,R.drawable.news,R.drawable.setting};
//    private int tab_icon_dark[] = {R.drawable.friend_inactive,R.drawable.chat_inactive,R.drawable.news_inactive,R.drawable.setting_inactive};

    private static final int REQUEST_CODE_AddFriend = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;
    private static final int REQUEST_CODE_Search = 5;
    private static final int REQUEST_CODE_BuildClass = 6;


    public static TestViewModel testViewModel;
    public static AnnocViewModel annocViewModel;

    public static String userID;
    public static Mqtt_Client mqtt;
    public static SipData sipData;
    public IncomingCallReceiver callReceiver;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tabs);

        // SQLite table create if not exist
        SQLiteManager.setContext(this);
        SQLiteManager.DBinit();

        initReceiver();
        arrayList.clear();

  //Change status color

        //Check network connection
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        //Check network connection

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        else{
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
   //Change status color

        testViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        annocViewModel = ViewModelProviders.of(this).get(AnnocViewModel.class);

        Intent intentFromUpload = getIntent();
        userID = intentFromUpload.getStringExtra("userID");

        //if userID != null  =>  intent from the StartInterface
        //if userID == null  =>  intent from a notification
        if(userID == null) {
            userID = intentFromUpload.getStringExtra("id");
            String code_from_noti = intentFromUpload.getStringExtra("code");
            String roomType = intentFromUpload.getStringExtra("roomType");
            Intent chat = null;
            switch (roomType) {
                case "F":
                case "G":
                    chat = new Intent(this, Chatroom.class);
                    break;

                case "C":
                    chat = new Intent(this, Classroom.class);
                    break;
            }
            chat.putExtra("code", code_from_noti);
            chat.putExtra("id", userID);
            startActivity(chat);
        }

        testViewModel.setUserID(userID);
        mqtt = new Mqtt_Client(this.getApplicationContext(), userID);

        //For toolbar

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);


        // For toolbar


        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friend).setText("個人頁面"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_inactive).setText("聊天"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.news_inactive).setText("公佈欄"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_inactive).setText("其他"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabs_pic_inactive), getResources().getColor(R.color.white));

        // tab1 add view
        View view = getLayoutInflater().inflate(R.layout.layout_tab,null);
        final TextView textView = view.findViewById(R.id.tab_TextView);
        ImageView imageView = view.findViewById(R.id.tab_imageView);
        textView.setText("聊天");
        textView.setTextColor(getResources().getColor(R.color.tabs_pic_inactive));
        imageView.setImageResource(R.drawable.chat_inactive);
        tabLayout.getTabAt(1).setCustomView(view);

        //badge test
        qBadgeView = new QBadgeView(getBaseContext());
        qBadgeView.bindTarget(tabLayout.getTabAt(1).getCustomView());
        qBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        qBadgeView.setGravityOffset(7.0f,-1.0f,true);
        qBadgeView.setBadgePadding(2.0f,true);
        qBadgeView.setBadgeNumber(SQLiteManager.getTotalUnread());

        viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        final TextView mainTitle = findViewById(R.id.mainTitle);
        mainTitle.setText("好友&群組");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View view_tmp;
                TextView textView_tmp;
                ImageView imageView_tmp;
                switch (position) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend);
                        view_tmp = tabLayout.getTabAt(1).getCustomView();
                        textView_tmp = view_tmp.findViewById(R.id.tab_TextView);
                        imageView_tmp = view_tmp.findViewById(R.id.tab_imageView);
                        textView_tmp.setTextColor(getResources().getColor(R.color.tabs_pic_inactive));
                        imageView_tmp.setImageResource(R.drawable.chat_inactive);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                        mainTitle.setText("好友&群組");
                        break;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                        view_tmp = tabLayout.getTabAt(1).getCustomView();
                        textView_tmp = view_tmp.findViewById(R.id.tab_TextView);
                        imageView_tmp = view_tmp.findViewById(R.id.tab_imageView);
                        textView_tmp.setTextColor(getResources().getColor(R.color.white));
                        imageView_tmp.setImageResource(R.drawable.chat);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                        mainTitle.setText("聊天室");
                        break;
                    case 2:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                        view_tmp = tabLayout.getTabAt(1).getCustomView();
                        textView_tmp = view_tmp.findViewById(R.id.tab_TextView);
                        imageView_tmp = view_tmp.findViewById(R.id.tab_imageView);
                        textView_tmp.setTextColor(getResources().getColor(R.color.tabs_pic_inactive));
                        imageView_tmp.setImageResource(R.drawable.chat_inactive);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                        mainTitle.setText("公佈欄");
                        break;
                    case 3:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                        view_tmp = tabLayout.getTabAt(1).getCustomView();
                        textView_tmp = view_tmp.findViewById(R.id.tab_TextView);
                        imageView_tmp = view_tmp.findViewById(R.id.tab_imageView);
                        textView_tmp.setTextColor(getResources().getColor(R.color.tabs_pic_inactive));
                        imageView_tmp.setImageResource(R.drawable.chat_inactive);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting);
                        mainTitle.setText("其他");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //實作Toolbar的動態更新
                invalidateOptionsMenu();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Log.d("TAG", "Tabs : onCreate()");
        LinkModule.getInstance().setRmsgListener(this);

        check_SIP_permission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "Tabs : onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.s2784.layout.action.badgeNum");
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver,intentFilter);

        qBadgeView.setBadgeNumber(SQLiteManager.getTotalUnread());
        viewPager.getAdapter().notifyDataSetChanged();
        Log.d("TAG", "Tabs : onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "Tabs : onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "Tabs : onStop()");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "Tabs : onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqtt.disconnect();

        if(sipData != null) {
            if (sipData.call != null) {
                sipData.call.close();
                sipData.call = null;
            }
            sipData.closeLocalProfile();
        }

        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
            netReceiver = null;
        }

        Log.d("TAG", "Tabs : onDestroy()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("TAG", "Tabs : onNewIntent");
    }

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.setting: //點了settings
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private android.support.v7.widget.Toolbar.OnMenuItemClickListener onMenuItemClick = new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.setting:
                    msg += "設定";
                    break;
                case R.id.searchview:
                    msg += "搜尋";
                    Intent intent_search = new Intent(Tabs.this, com.example.s2784.layout.SearchView.class);
                    //intent_search.putParcelableArrayListExtra("friend_and_group_list",arrayList);
                    startActivityForResult(intent_search, REQUEST_CODE_Search);
                    break;
                case R.id.build_group:
                    msg += "創建群組";
                    Intent intent_buildGroup = new Intent(Tabs.this, BuildGroup.class);
                    startActivityForResult(intent_buildGroup, REQUEST_CODE_BuildGroup);
                    break;
                case R.id.add_friend:
                    msg += "加入好友";
                    Intent intent_addFriend = new Intent(Tabs.this, AddFriend.class);
                    startActivityForResult(intent_addFriend, REQUEST_CODE_AddFriend);
                    break;
                case R.id.log_out:
                    msg += "登出";
                    Leave();
                    break;
                case R.id.network_check:
                    msg += "檢查網路";
                    mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                    networkCheck(mNetworkInfo);
                    break;
                case R.id.build_class:
                    msg += "新增課程";
                    Intent intent_buildClass = new Intent(Tabs.this, BuildClass.class);
                    intent_buildClass.putParcelableArrayListExtra("friendlist", testViewModel.getListHash().get("好友"));
                    startActivityForResult(intent_buildClass, REQUEST_CODE_BuildClass);
                    break;
            }

//            if (!msg.equals("")) {
//                Toast.makeText(Tabs.this, msg, Toast.LENGTH_SHORT).show();
//            }
            return true;
        }
    };



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (viewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.searchview).setVisible(true);
                menu.findItem(R.id.build_group).setVisible(true);
                menu.findItem(R.id.add_friend).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.searchview).setVisible(true);
                menu.findItem(R.id.build_group).setVisible(true);
                menu.findItem(R.id.add_friend).setVisible(true);
                break;
            case 2:
                menu.findItem(R.id.searchview).setVisible(false);
                menu.findItem(R.id.build_group).setVisible(false);
                menu.findItem(R.id.add_friend).setVisible(false);
                break;
            case 3:
                menu.findItem(R.id.searchview).setVisible(false);
                menu.findItem(R.id.build_group).setVisible(false);
                menu.findItem(R.id.add_friend).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void Leave() {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(Tabs.this, "確定要登出嗎?");
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setMessage("是否要登出?")
//                .setPositiveButton("否", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
//                .setNegativeButton("是", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        SQLiteManager.deleteAllUser();
//                        SQLiteManager.deleteAllBadge();
//                        Intent intent_Login = new Intent(Tabs.this, LogIn.class);
//                        startActivity(intent_Login);
//                        finish();
//                    }
//                });
//        AlertDialog about_dialog = builder.create();
//        about_dialog.show();
    }

    @Override
    public void FLCM_onItemClick(DialogFragment dialog, int which, int childPos) {
        String ID = testViewModel.getFriend().get(childPos).getStudentID();
        String code = testViewModel.getFriend().get(childPos).getCode();
        switch (which) {
            case 0:
                //指定哪個朋友要被刪除
                mqtt.setDeleteFriendPos(childPos);
                mqtt.DeleteFriend(ID, code);
                for(int i = 0; i < arrayList.size(); ++i) {
                    if(arrayList.get(i).getStudentID().equals(ID)) {
                        arrayList.remove(i);
                        break;
                    }
                }
                break;
            case 1:
                //撥打電話
//                Toast.makeText(this, mqtt.MapPhoneNum(testViewModel.getFriend().get(childPos).getStudentID()), Toast.LENGTH_LONG).show();
                try {
                    if (sipData.manager.isRegistered(sipData.me.getUriString())) {
                        String id = testViewModel.getFriend().get(childPos).getStudentID();
                        Intent calloutActivity = new Intent(Tabs.this, CallingOutActivity.class);
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
                break;
        }
    }

    @Override
    public void GLCM_onItemClick(DialogFragment dialog, int which, int childPos) {
        String ID = testViewModel.getGroup().get(childPos).getRoomName();
        String code = testViewModel.getGroup().get(childPos).getCode();
        switch (which) {
            case 0:
                //指定要退出哪個群組
                mqtt.setWithdrawGroupPos(childPos);
                mqtt.WithdrawFromGroup(code);
                for(int i = 0; i < arrayList.size(); ++i) {
                    if(arrayList.get(i).getRoomName().equals(ID)) {
                        arrayList.remove(i);
                        break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUEST_CODE_AddFriend:
                String addFriendID = data.getStringExtra("StudentID");
                mqtt.AddFriend(addFriendID);
                break;
            case REQUEST_CODE_BuildGroup:
                String groupName = data.getStringExtra("groupName");
                ArrayList<String> groupMember = data.getStringArrayListExtra("memberList");

                StringBuilder sb = new StringBuilder(userID);
                if (groupMember != null) {
                    for (int i = 0; i < groupMember.size(); ++i) {
                        //member_str += ("\t" + groupMember.get(i));
                        sb.append("\t");
                        sb.append(groupMember.get(i));
                    }
                }
                mqtt.AddGroup(groupName, sb.toString());
                break;
            case REQUEST_CODE_BuildClass:
                String className = data.getStringExtra("className");
                ArrayList<String> Student = data.getStringArrayListExtra("memberList");

                StringBuilder sb2 = new StringBuilder(userID);
                if (Student != null) {
                    for (int i = 0; i < Student.size(); ++i) {
                        //member_str += ("\t" + groupMember.get(i));
                        sb2.append("\t");
                        sb2.append(Student.get(i));
                    }
                }
                mqtt.AddGroup(className, sb2.toString());
                break;
            case REQUEST_CODE_JoinGroup:
//                RoomInfo newGroup = new RoomInfo();
//                String groupName = data.getStringExtra("groupName");
//                newGroup.setRoomName(groupName);
//                newGroup.setIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.bubble_out));
//                group.add(newGroup);
//                listHash.put(listDataHeader.get(0),group);
                break;
            case REQUEST_CODE_MsgBulletin:
                break;
            case REQUEST_CODE_Search:
                break;
        }
    }

    @Override
    public void updateRMSG(String code, String rMSG, String rMSGDate) {
        boolean flag = false;
        for (int i = 0; i < testViewModel.getGroup().size(); i++) {
            if (testViewModel.getGroup().get(i).getCode().equals(code)) {
                testViewModel.getGroup().get(i).setrMsg(rMSG);
                testViewModel.getGroup().get(i).setrMsgDate(rMSGDate);
                flag = true;
                break;
            }
        }
        if(!flag) {
            for (int i = 0; i < testViewModel.getFriend().size(); i++) {
                if (testViewModel.getFriend().get(i).getCode().equals(code)){
                    testViewModel.getFriend().get(i).setrMsg(rMSG);
                    testViewModel.getFriend().get(i).setrMsgDate(rMSGDate);
                    break;
                }
            }
        }
        if(!flag) {
            for (int i = 0; i < testViewModel.getCourse().size(); i++) {
                if (testViewModel.getCourse().get(i).getCode().equals(code)) {
                    testViewModel.getCourse().get(i).setrMsg(rMSG);
                    testViewModel.getCourse().get(i).setrMsgDate(rMSGDate);
                    break;
                }
            }
        }
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void addAnnoc(String text) {
        annocViewModel.add_annoc(text);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String code = extras.getString("code_for_badge");
            updateBadge(code);
        }
    }

    public class Mqtt_Client {
        private static final String MQTT_HOST = "tcp://140.116.82.52:1883";
        private MqttAndroidClient client;
        private MqttConnectOptions options;

        private Context context;
        private String user;

        private String[] init_info = null;

        private String[] addFriend_info = null;

        private int deleteFriendPos = -1;
        private int withdrawGroupPos = -1;

        private String processingCode = "";
        private ArrayList<RoomInfo> roomInfoList; // 暫存用 並非更新至正確資料 正確版本存在Main底下的friend,group
        private HashMap<String, Bitmap> friendInfoMap;
        private HashMap<String, String> friendIntroMap;
        private HashMap<String, String> aliasMap;
        private HashMap<String, String> phoneMap;

        private Mqtt_Client(Context context, String user) {
            this.context = context;
            this.user = user;
            roomInfoList = new ArrayList<>();
            friendInfoMap = new HashMap<>();
            friendIntroMap = new HashMap<>();
            aliasMap = new HashMap<>();
            phoneMap = new HashMap<>();
        }


        private void Connect() {
            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(context, MQTT_HOST, clientId);

            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        mqttSub();
                        GetUserData();
                        Initialize();
                        SubmitFCMToken();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        mqttSub();
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("TAG", "mqtt connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String[] idf = topic.split("/");
                    if(idf[0].equals("IDF")) {
                        boolean isFound;
                        switch (idf[1]) {
                            case "Initialize":
                                StringTokenizer stringTokenizer = new StringTokenizer(new String(message.getPayload()), "\r");
                                while (stringTokenizer.hasMoreElements()) {
                                    String token = stringTokenizer.nextToken();
                                    init_info = token.split("\t");
                                    //Log.d("MEM",init_info[2]);
                                    RoomInfo roomInfo = new RoomInfo();
                                    roomInfo.setCode(init_info[0]);
                                    roomInfo.setRoomName(init_info[1]);
                                    StringTokenizer split_member = new StringTokenizer(init_info[2],"-");
                                    while (split_member.hasMoreElements()){
                                        String memberID = split_member.nextToken();
                                        roomInfo.addMemberID(memberID);
                                    }
                                    if(init_info[3].equals("F")) { //maintain studentID, type F has value, type "G" has not
                                        for (int i = 0; i < roomInfo.getMemberID().size(); i++) {
                                            if (!roomInfo.getMemberID().get(i).equals(userID)) {
                                                roomInfo.setStudentID(roomInfo.getMemberID().get(i));
                                                roomInfo.setIntro(init_info[6]);
                                                aliasMap.put(roomInfo.getStudentID(),roomInfo.getRoomName());
                                                friendIntroMap.put(roomInfo.getStudentID(), init_info[6]);
                                                break;
                                            }
                                        }
                                    }else{
                                        roomInfo.setStudentID("");
                                        roomInfo.setIntro("");
                                    }
                                    roomInfo.setType(init_info[3]);
                                    roomInfo.setrMsg(init_info[4]);
                                    roomInfo.setrMsgDate(init_info[5]);
                                    roomInfoList.add(roomInfo);
                                    if (init_info[3].equals("F")) {
                                        GetFriendIcon("Init", roomInfo.getStudentID(), "0");
                                    } else if (init_info[3].equals("G")) {
                                        Initialize_re(roomInfo);
                                    } else if (init_info[3].equals("C")) {
                                        Initialize_re(roomInfo);
                                    }
                                }
                                break;
                            case "AddFriend":
                                addFriend_info = new String(message.getPayload()).split("/");
                                if (addFriend_info[0].equals("true")) {
                                    RoomInfo roomInfo = new RoomInfo();
                                    roomInfo.setFriendName(addFriend_info[1]);
                                    roomInfo.setRoomName(addFriend_info[1]);
                                    roomInfo.setStudentID(addFriend_info[2]);
                                    roomInfo.addMemberID(user);
                                    roomInfo.addMemberID(addFriend_info[2]);
                                    roomInfo.setCode(addFriend_info[3]);
                                    roomInfo.setrMsg("No History");
                                    roomInfo.setrMsgDate("XXXX-XX-XX XX:XX");
                                    roomInfo.setType("F");
                                    roomInfoList.add(roomInfo);
                                    aliasMap.put(roomInfo.getStudentID(),roomInfo.getRoomName());
                                    GetFriendIcon("Add", addFriend_info[2], addFriend_info[4]);
                                } else if (addFriend_info[0].equals("false")) {
                                    Toast.makeText(Tabs.this, "加入好友失敗", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "GetUserData":
                                String userdata_str = new String(message.getPayload()); // name and friend phone numbers
                                GetUserData_re(userdata_str);
                                break;
                            case "GetUserIcon":
                                Bitmap userIcon = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                GetUserIcon_re(userIcon);
                                break;
                            case "AddGroup":
                                String[] AG_msg = (new String(message.getPayload())).split("/");
                                if (AG_msg[0].equals("true")) {
                                    AddGroup_re(AG_msg[1], AG_msg[2], AG_msg[3], AG_msg[4]);
                                } else {
                                    Toast.makeText(context, "Error creating group " + AG_msg[0], Toast.LENGTH_LONG).show();
                                }
                                break;
                            case "DeleteFriend":
                                String[] DF_msg = (new String(message.getPayload())).split("/");
                                if (DF_msg[0].equals("true")) {
                                    if (DF_msg[1].equals("1")) {
                                        DeleteFriend_re("");
                                    } else {
                                        DeleteFriend_re(DF_msg[2]);
                                    }
                                } else {
                                    deleteFriendPos = -1;
                                }
                                break;
                            case "WithdrawFromGroup":
                                String WG_msg = new String(message.getPayload());
                                if (WG_msg.equals("true")) {
                                    WithdrawFromGroup_re();
                                } else {
                                    withdrawGroupPos = -1;
                                }
                                break;
                            case "SendMessage":
                                String SM_msg = new String(message.getPayload());
                                String[] SM_msg_splitLine = SM_msg.split("\t");
                                isFound = false;
                                for (int i = 0; i < testViewModel.getGroup().size(); i++) {
                                    if (testViewModel.getGroup().get(i).getCode().equals(SM_msg_splitLine[0])) {
                                        testViewModel.getGroup().get(i).setrMsg(SM_msg_splitLine[2]);
                                        testViewModel.getGroup().get(i).setrMsgDate(SM_msg_splitLine[3]);
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(!isFound) {
                                    for (int i = 0; i < testViewModel.getFriend().size(); i++) {
                                        if (testViewModel.getFriend().get(i).getCode().equals(SM_msg_splitLine[0])) {
                                            testViewModel.getFriend().get(i).setrMsg(SM_msg_splitLine[2]);
                                            testViewModel.getFriend().get(i).setrMsgDate(SM_msg_splitLine[3]);
                                            break;
                                        }
                                    }
                                }
                                if(!isFound) {
                                    for (int i = 0; i < testViewModel.getCourse().size(); i++) {
                                        if (testViewModel.getCourse().get(i).getCode().equals(SM_msg_splitLine[0])) {
                                            testViewModel.getCourse().get(i).setrMsg(SM_msg_splitLine[2]);
                                            testViewModel.getCourse().get(i).setrMsgDate(SM_msg_splitLine[3]);
                                            break;
                                        }
                                    }
                                }
                                if (processingCode.equals(SM_msg_splitLine[0])) {
                                    LinkModule.getInstance().callUpdateMsg(SM_msg_splitLine[1], SM_msg_splitLine[2], SM_msg_splitLine[3]);
                                }
                                viewPager.getAdapter().notifyDataSetChanged();
                                break;
                            case "SendImg":
                                Log.d("imgd","on mqtt SendImg callback");
                                Bitmap SI_img = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                String[] tsl = topic.split("/"); // IDF/SendImg/user/sender/code/time/Re
                                isFound = false;
                                for (int i = 0; i < testViewModel.getGroup().size(); i++) {
                                    if (testViewModel.getGroup().get(i).getCode().equals(tsl[4])) {
                                        testViewModel.getGroup().get(i).setrMsg("a new image");
                                        testViewModel.getGroup().get(i).setrMsgDate(tsl[5]);
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(!isFound) {
                                    for (int i = 0; i < testViewModel.getFriend().size(); i++) {
                                        if (testViewModel.getFriend().get(i).getCode().equals(tsl[4])) {
                                            testViewModel.getFriend().get(i).setrMsg("a new image");
                                            testViewModel.getFriend().get(i).setrMsgDate(tsl[5]);
                                            break;
                                        }
                                    }
                                }
                                if(!isFound) {
                                    for (int i = 0; i < testViewModel.getCourse().size(); i++) {
                                        if (testViewModel.getCourse().get(i).getCode().equals(tsl[4])) {
                                            testViewModel.getCourse().get(i).setrMsg("a new image");
                                            testViewModel.getCourse().get(i).setrMsgDate(tsl[5]);
                                            break;
                                        }
                                    }
                                }
                                if (processingCode.equals(tsl[4])) {
                                    LinkModule.getInstance().callUpdateImg(tsl[3], SI_img, tsl[5]);
                                }
                                viewPager.getAdapter().notifyDataSetChanged();
                                break;
                            case "GetRecord":
                                LinkModule.getInstance().callFetchRecord(new String(message.getPayload()));
                                break;
                            case "RecordImgBack":
                                int pos = Integer.parseInt(topic.split("/")[3]);
                                Bitmap image = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                LinkModule.getInstance().callUpdateImg(image, pos);
                                LinkModule.getInstance().callRefreshListView();
                                break;
                            case "MemberChange":
                                String MC_msg = new String(message.getPayload());
//                                Log.d("MEM",MC_msg);
                                String[] MC_msg_splitLine = MC_msg.split("\t");
                                String code = MC_msg_splitLine[0];
                                String memberID = MC_msg_splitLine[1];
                                for(int i = 0;i<testViewModel.getGroup().size();i++){
                                    if(testViewModel.getGroup().get(i).getCode().equals(code)){
                                        testViewModel.getGroup().get(i).getMemberID().clear();
                                        StringTokenizer split_member = new StringTokenizer(memberID,"-");
                                        while (split_member.hasMoreElements()){
                                            String member = split_member.nextToken();
                                            testViewModel.getGroup().get(i).addMemberID(member);
                                        }
                                        LinkModule.getInstance().callMemberChange(memberID);
                                        break;
                                    }
                                }
                                break;
                            case "SendNewChatroom": //invite
                                String SNC_msg = new String(message.getPayload());
                                String[] SNC_msg_splitLine = SNC_msg.split("\t");
                                RoomInfo roomInfo = new RoomInfo();
                                roomInfo.setCode(SNC_msg_splitLine[0]);
                                roomInfo.setRoomName(SNC_msg_splitLine[1]);
                                StringTokenizer split_member = new StringTokenizer(SNC_msg_splitLine[2],"-");
                                while (split_member.hasMoreElements()){
                                    String str = split_member.nextToken();
                                    roomInfo.addMemberID(str);
                                }
                                roomInfo.setType(SNC_msg_splitLine[3]);
                                roomInfo.setrMsg(SNC_msg_splitLine[4]);
                                roomInfo.setrMsgDate(SNC_msg_splitLine[5]);

                                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_group);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] bytes = stream.toByteArray();
                                roomInfo.setIcon_data(bytes);
                                roomInfo.setUnReadNum(SQLiteManager.querySingleRoomBadge(roomInfo.getCode()));
                                testViewModel.addInGroup(roomInfo);
                                testViewModel.putListHash("群組", testViewModel.getGroup());
                                viewPager.getAdapter().notifyDataSetChanged();
                                Tab1_CM.getInstance().refreshExplv(0);
                                break;
                            case "GetAuth":
                                String auth = new String(message.getPayload());
                                LinkModule.getInstance().callSetAuth(Integer.parseInt(auth));
                                break;
                            case "GetPoster":
                                String record_poster = new String(message.getPayload());
                                LinkModule.getInstance().callFetchPoster(record_poster);
                                break;
                            case "GetPosterReply":
                                String record_reply = new String(message.getPayload());
                                LinkModule.getInstance().callFetchPosterReply(record_reply);
                                break;
                            case "AddPoster":
                                String new_record = new String(message.getPayload());
                                String[] splitLine = new_record.split("\t");
                                if(splitLine[3].equals("post")) {
                                    LinkModule.getInstance().callUpdatePoster(splitLine[0], splitLine[1], splitLine[2], splitLine[3], splitLine[4], splitLine[5]);
                                }else if(splitLine[3].equals("reply")){
                                    LinkModule.getInstance().callUpdatePosterReply(splitLine[0], splitLine[1], splitLine[2], splitLine[3], splitLine[4], splitLine[5]);
                                }
                                break;
                            case "ChangeUserIcon":
                                Bitmap CUI_img = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                testViewModel.setUserIcon(CUI_img);
                                viewPager.getAdapter().notifyDataSetChanged();
                                Toast.makeText(context, R.string.change_icon_succeed, Toast.LENGTH_SHORT).show();
                                break;

                            case "ChangeUserName":
                                String CUN_MSG = new String(message.getPayload());
                                if(CUN_MSG.split("\t")[0].equals("OK")) {
                                    String newName = CUN_MSG.split("\t")[1];
                                    testViewModel.setUserName(newName);
                                    viewPager.getAdapter().notifyDataSetChanged();
                                    Toast.makeText(context, R.string.change_name_succeed, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, R.string.change_name_fail, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case "ChangeUserIntro":
                                String CUI_MSG = new String(message.getPayload());
                                if(CUI_MSG.split("\t")[0].equals("OK")) {
                                    SQLiteManager.setIntro(CUI_MSG.split("\t")[1]);
                                    Toast.makeText(context, R.string.change_intro_succeed, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, R.string.change_intro_fail, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            default:
                                if (idf[1].contains("FriendIcon")) {
                                    if (idf[1].contains("Init")) {
                                        String[] topicSplitLine = idf[1].split(":");
                                        Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                        friendInfoMap.put(topicSplitLine[1], b);
                                        for (int i = 0; i < roomInfoList.size(); i++) {
                                            if (roomInfoList.get(i).getStudentID().equals(topicSplitLine[1])) {
                                                roomInfoList.get(i).setIcon_data(message.getPayload());
                                                Initialize_re(roomInfoList.get(i));
                                                roomInfoList.remove(i);
                                                break;
                                            }
                                        }
                                    } else if (idf[1].contains("Add")) {
                                        String[] topicSplitLine = idf[1].split(":");
                                        Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                        friendInfoMap.put(topicSplitLine[1], b);
                                        for (int i = 0; i < roomInfoList.size(); i++) {
                                            if (roomInfoList.get(i).getStudentID().equals(topicSplitLine[1])) {
                                                roomInfoList.get(i).setIcon_data(message.getPayload());
                                                AddFriend_re(roomInfoList.get(i), topicSplitLine[2]);
                                                roomInfoList.remove(i);
                                                break;
                                            }
                                        }

                                    }
                                }
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }

        private void disconnect() {
            if (client != null && client.isConnected()) {
                try {
                    client.unsubscribe("IDF/+/" + user + "/Re");
                    client.unsubscribe("IDF/SendImg/" + user + "/+/+/+/Re");
                    client.unsubscribe("IDF/RecordImgBack/" + user + "/+/Re");
                    client.disconnect();
                    client.unregisterResources();
                    client = null;
                    Log.d("TAG", "Try Disconnect");
                } catch (MqttException e) {
                    Log.d("TAG", "Disconnect Error");
                    e.printStackTrace();
                }
            }
        }

        public boolean isConnected() {
            if(client == null) {
                return false;
            }
            return client.isConnected();
        }


        private void mqttSub() {
            try {
                /*
                * IDF/      +      /"user"/  Re
                * IDF/   SendImg   /"user"/  +  /  +   /Re
                * IDF/RecordImgBack/"user"/  +  /  Re
                */
                String topic = "IDF/+/" + user + "/Re";
                client.subscribe(topic, 2);
                topic = "IDF/SendImg/" + user + "/+/+/+/Re";
                client.subscribe(topic, 2);
                topic = "IDF/RecordImgBack/" + user + "/+/Re";
                client.subscribe(topic, 2);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void Initialize() {
            String topic = "IDF/Initialize/" + user;
            String MSG = "";
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void Initialize_re(RoomInfo roomInfo) {
            if (roomInfo.getType().equals("F")) {
                roomInfo.setUnReadNum(SQLiteManager.querySingleRoomBadge(roomInfo.getCode()));
                testViewModel.addInFriend(roomInfo);
                arrayList.add(roomInfo);
                testViewModel.putListHash("好友", testViewModel.getFriend());
                viewPager.getAdapter().notifyDataSetChanged();
            } else if (roomInfo.getType().equals("G")) {
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_group);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                roomInfo.setIcon_data(bytes);
                roomInfo.setUnReadNum(SQLiteManager.querySingleRoomBadge(roomInfo.getCode()));
                testViewModel.addInGroup(roomInfo);
                arrayList.add(roomInfo);
                testViewModel.putListHash("群組", testViewModel.getGroup());
                viewPager.getAdapter().notifyDataSetChanged();
            } else if (roomInfo.getType().equals("C")) {
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.course);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                roomInfo.setIcon_data(bytes);
                roomInfo.setUnReadNum(SQLiteManager.querySingleRoomBadge(roomInfo.getCode()));
                testViewModel.addInCourse(roomInfo);
                arrayList.add(roomInfo);
                testViewModel.putListHash("課程", testViewModel.getCourse());
                viewPager.getAdapter().notifyDataSetChanged();
            }

            if(roomInfo.getCode().equals(processingCode)) {
                String s = String.format("in Init/Re : roomName = %s", roomInfo.getRoomName());
                Log.d("TAG", s);
                LinkModule.getInstance().callStartCreat();
            }
        }

        private void GetUserData(){
            String topic = "IDF/GetUserData/" + user;
            String MSG = "";
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void GetUserData_re(String str){
            String name = str.split("\r")[0];
            testViewModel.setUserName(name);
            viewPager.getAdapter().notifyDataSetChanged();
            aliasMap.put(user,name);

            String phone_str = str.split("\r")[1];
            StringTokenizer stringTokenizer = new StringTokenizer(phone_str, ",");
            while(stringTokenizer.hasMoreElements()){
                String token = stringTokenizer.nextToken();
                String id = token.split("\t")[0];
                String phone = token.split("\t")[1];
                phoneMap.put(id,phone);
//                Log.d("TEST", id + ":" + phone);
            }
            GetUserIcon();
            init_SIP();
        }

        private void GetUserIcon(){
            String topic = "IDF/GetUserIcon/" + user;
            String MSG = "";
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void GetUserIcon_re(Bitmap b){
            testViewModel.setUserIcon(b);
            viewPager.getAdapter().notifyDataSetChanged();
        }

        private void AddFriend(String friendID) {
            String topic = "IDF/AddFriend/" + user;
            try {
                client.publish(topic, friendID.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void AddFriend_re(RoomInfo roomInfo, String tachiba) {
            roomInfo.setUnReadNum(SQLiteManager.querySingleRoomBadge(roomInfo.getCode()));
            testViewModel.addInFriend(roomInfo);
            testViewModel.putListHash("好友", testViewModel.getFriend());
            viewPager.getAdapter().notifyDataSetChanged();

            if(tachiba.equals("1")) {
                String topic = "Service/AddFriendNotification/" + roomInfo.getStudentID();
                String MSG = roomInfo.getRoomName();
                try {
                    client.publish(topic, MSG.getBytes(), 2, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Intent studentData = new Intent(Tabs.this, StudentData.class);
                studentData.putExtra("name", roomInfo.getFriendName());
                studentData.putExtra("ID", roomInfo.getStudentID());
                //studentData.putExtra("image", roomInfo.getIcon_data());
                studentData.putExtra("MeOrNot", "0"); //friend

                startActivity(studentData);
            } else if(tachiba.equals("2")) {
                AddFriendNotification(roomInfo.getRoomName());
            }

            Tab1_CM.getInstance().refreshExplv(1);


            arrayList.add(roomInfo);
        }

        private void AddGroup(String groupName, String member_str) {
            String topic = "IDF/AddGroup/" + user;
            String MSG = groupName + "\t" + member_str;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void AddGroup_re(String code, String groupName, String memberID, String tachiba) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setCode(code);
            roomInfo.setRoomName(groupName);
            roomInfo.setStudentID("");
            roomInfo.setType("G");
            StringTokenizer split_member = new StringTokenizer(memberID,"-");
            while (split_member.hasMoreElements()){
                String member = split_member.nextToken();
                roomInfo.addMemberID(member);
            }
            roomInfo.setrMsg("No History");
            roomInfo.setrMsgDate("XXXX-XX-XX XX:XX");
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_group);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            roomInfo.setIcon_data(bytes);
            roomInfo.setUnReadNum(SQLiteManager.querySingleRoomBadge(roomInfo.getCode()));
            testViewModel.addInGroup(roomInfo);
            testViewModel.putListHash("群組", testViewModel.getGroup());
            viewPager.getAdapter().notifyDataSetChanged();
            arrayList.add(roomInfo);

            if(tachiba.equals("2")) {
                AddGroupNotification(roomInfo.getRoomName());
            }

            Tab1_CM.getInstance().refreshExplv(0);

        }

        private void DeleteFriend(String friendID, String code) {
            String topic = "IDF/DeleteFriend/" + user;
            String MSG = friendID + "/" + code;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void setDeleteFriendPos(int deleteFriendPos) {
            this.deleteFriendPos = deleteFriendPos;
        }

        private void DeleteFriend_re(String ID) {
            if(ID.equals("")) {
                testViewModel.removeFromFriend(deleteFriendPos);
                deleteFriendPos = -1;
            }else{
                ArrayList<RoomInfo> DF_tmpArray = testViewModel.getFriend();
                for(int i = 0; i < DF_tmpArray.size(); ++i) {
                    if(DF_tmpArray.get(i).getStudentID().equals(ID)) {
                        testViewModel.removeFromFriend(i);
                        break;
                    }
                }
            }
            testViewModel.putListHash("好友", testViewModel.getFriend());
            viewPager.getAdapter().notifyDataSetChanged();

            Tab1_CM.getInstance().refreshExplv(1);
        }

        private void WithdrawFromGroup(String code) {
            String topic = "IDF/WithdrawFromGroup/" + user;
            try {
                client.publish(topic, code.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void setWithdrawGroupPos(int withdrawGroupPos) {
            this.withdrawGroupPos = withdrawGroupPos;
        }

        private void WithdrawFromGroup_re() {
            testViewModel.removeFromGroup(withdrawGroupPos);
            testViewModel.putListHash("群組", testViewModel.getGroup());
            viewPager.getAdapter().notifyDataSetChanged();
            withdrawGroupPos = -1;
            Tab1_CM.getInstance().refreshExplv(0);
        }

        public void setProcessingCode(String processingCode) {
            this.processingCode = processingCode;
        }

        public void SendMessage(String str) {
            String topic = "IDF/SendMessage/" + user;
            try {
                client.publish(topic, str.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void DeleteMessage(String code, String time) {
            String topic = "IDF/DeleteMessage/" + user;
            String MSG = code + "\t" + time;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void SendImg(Uri uri, String code, int whichTopic) {
            ContentResolver cr = context.getContentResolver();
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(cr.openInputStream(uri), null,options);
                int reqWidth = 688, reqHeight = 387;
                int inSampleSize = 1;
                if(options.outWidth > reqWidth || options.outHeight > reqHeight) {
                    final int heightRatio = Math.round((float)options.outHeight / (float)reqHeight);
                    final int widthRatio = Math.round((float)options.outWidth / (float)reqWidth);
                    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                }
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;

                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

                String topic = "";
                if(whichTopic == R.integer.SEND_IMG_M1) {
                    topic = "IDF/SendImg/" + user + "/" + code;
                }else if(whichTopic == R.integer.SEND_IMG_M2) {
                    topic = "IDF/ChangeUserIcon/" + user;
                }
                client.publish(topic, baos.toByteArray(), 2, false);
            } catch (FileNotFoundException e) {
                Log.d("imgt", e.getMessage(), e);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void GetRecord(String code) {
            String topic = "IDF/GetRecord/" + user;
            try {
                client.publish(topic, code.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void RecordImgBack(String path, int pos) {
            String topic = "IDF/RecordImgBack/" + user + "/" + pos;
            try {
                client.publish(topic, path.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void GetFriendIcon(String action, String friendID, String tachiba) {
            String topic = "IDF/FriendIcon/" + user;
            String MSG = "";
            switch (tachiba) {
                case "0":
                    MSG = action + ":" + friendID;
                    break;
                case "1":
                    MSG = action + ":" + friendID + ":1";
                    break;
                case "2":
                    MSG = action + ":" + friendID + ":2";
                    break;
                default:
                    break;
            }
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void InviteFriend(String code, String member){
            String topic = "IDF/InviteFriend/" + user;
            String MSG = code + "\t" + member;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }


        private void AddFriendNotification(String friendName) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,getString(R.string.CHANNEL_ID_AddFriend));
            builder.setSmallIcon(R.mipmap.ncku_line2);
            builder.setContentTitle("Title : Friend request");
            builder.setContentText(friendName);

//            Intent intent = new Intent(context, Tabs.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addParentStack(Tabs.class);
//            stackBuilder.addNextIntent(intent);
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//
//            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.CHANNEL_ID_AddFriend),getString(R.string.CHANNEL_NAME_AddFriend),NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            int notiID = Integer.parseInt(getString(R.string.CHANNEL_ID_AddFriend));
            notificationManager.notify(notiID,builder.build());

        }

        private void AddGroupNotification(String groupName) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,getString(R.string.CHANNEL_ID_AddGroup));
            builder.setSmallIcon(R.mipmap.ncku_line2);
            builder.setContentTitle("Title : Group request");
            builder.setContentText(groupName);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.CHANNEL_ID_AddGroup),getString(R.string.CHANNEL_NAME_AddGroup),NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            int notiID = Integer.parseInt(getString(R.string.CHANNEL_ID_AddGroup));
            notificationManager.notify(notiID,builder.build());
        }

        public Bitmap MapBitmap(String id) {
            return friendInfoMap.get(id);
        }

        public String MapIntro(String id) { return friendIntroMap.get(id); }

        public String MapAlias(String id){ return aliasMap.get(id); }

        public String MapPhoneNum(String id) { return phoneMap.get(id);}

        private void SubmitFCMToken() {
            String token = getSharedPreferences("FCM_Token", MODE_PRIVATE).getString("token", "empty");
            if(token.equals("empty")) {
                Toast.makeText(Tabs.this,"FCM token error. 請重新安裝或清除資料再啟動", Toast.LENGTH_LONG).show();
                System.exit(99);
            }
            String topic = "IDF/SubmitFCMToken/" + user;
            try {
                client.publish(topic, token.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void getAuth(String code) {
            String topic = "IDF/GetAuth/" + user;
            String MSG = code;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void addPoster(String code, String theme, String content, String type_t){
            String topic = "IDF/AddPoster/" + user;
            String MSG = code + "\t" + theme + "\t" + content + "\t" + type_t;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void getPoster(String code){
            String topic = "IDF/GetPoster/" + user;
            String MSG = code;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void getPosterReply(String code, String theme) {
            String topic = "IDF/GetPosterReply/" + user;
            String MSG = code + "\t" + theme;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void deletePost(String code, String theme){
            String topic = "IDF/DeletePoster/" + user;
            String MSG = code + "\t" + theme;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void deletePostReply(String code, String theme, String content){
            String topic = "IDF/DeletePosterReply/" + user;
            String MSG = code + "\t" + theme + "\t" + content;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void changeUserIcon(Uri uri){
            SendImg(uri, "", R.integer.SEND_IMG_M2);
        }

        public void changeUserName(String newName) {
            String topic = "IDF/ChangeUserName/" + user;
            String MSG = newName;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void changeUserIntro(String newIntro) {
            String topic = "IDF/ChangeUserIntro/" + user;
            String MSG = newIntro;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void forwardTXT(String codes, String MSG) {
            String topic = "IDF/ForwardTXT/" + user + "/" + codes;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void forwardIMG(String codes, Bitmap bitmap){
            String topic = "IDF/ForwardIMG/" + user + "/" + codes;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            try {
                client.publish(topic, baos.toByteArray(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////
    private void networkCheck(NetworkInfo mNetworkInfo){

        if(mNetworkInfo != null)
        {
            if(mNetworkInfo.isConnected())
            {
                Toast.makeText(Tabs.this, "網路已連線", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
//            Toast.makeText(Tabs.this, "網路未連線，請檢查網路狀態", Toast.LENGTH_SHORT).show();
//            LayoutInflater inflater = getLayoutInflater();
//            View layout = inflater.inflate(R.layout.warning_toast,
//                    (ViewGroup) findViewById(R.id.toast_layout_root));
//
//            ImageView image = (ImageView) layout.findViewById(R.id.image);
//            image.setImageResource(R.drawable.warning);
//            TextView text = (TextView) layout.findViewById(R.id.text);
//            text.setText("Hello! This is a custom toast!");
//
//            Toast toast = new Toast(getApplicationContext());
//            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//            toast.setDuration(Toast.LENGTH_LONG);
//            toast.setView(layout);
//            toast.show();
        }

    }


    private void initReceiver() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        timeFilter.addAction("android.net.ethernet.STATE_CHANGE");
        timeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        timeFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        timeFilter.addAction("android.net.wifi.STATE_CHANGE");
        timeFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netReceiver, timeFilter);
    }


    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    int type2 = networkInfo.getType();
                    String typeName = networkInfo.getTypeName();

                    switch (type2) {
                        case 0://移动 网络    2G 3G 4G 都是一样的 实测 mix2s 联通卡
                            Log.d("Feeee", "有網路");
//                            Toast.makeText(Tabs.this, "網路已連線", Toast.LENGTH_SHORT).show();
                            testViewModel.clearAll();
                            mqtt.disconnect();
                            mqtt.Connect();
                            break;
                        case 1: //wifi网络
                            Log.d("Feeee", "wifi");
//                            Toast.makeText(Tabs.this, "網路已連線", Toast.LENGTH_SHORT).show();
                            testViewModel.clearAll();
                            mqtt.disconnect();
                            mqtt.Connect();
                            break;

                        case 9:  //网线连接
                            Log.d("Feeee", "有網路");
                            break;
                    }
                } else {// 无网络
                    Log.d("Feeee", "沒有網路");
//                    Toast.makeText(Tabs.this, "網路未連線，請檢查網路狀態", Toast.LENGTH_SHORT).show();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.warning_toast,
                            (ViewGroup) findViewById(R.id.toast_layout_root));

                    ImageView image = (ImageView) layout.findViewById(R.id.image);
                    image.setImageResource(R.drawable.warning);
                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("網路未連線，請檢查網路狀態!");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setView(layout);
                    toast.show();
                }
            }
        }
    };

    public class ViewDialog {

        public void showDialog(Activity activity, String msg){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button NegativeButton = (Button) dialog.findViewById(R.id.btn_dialog);
            Button PositiveButton = (Button) dialog.findViewById(R.id.btn_dialog2);
            NegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            PositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                        SQLiteManager.deleteAllUser();
                        SQLiteManager.deleteAllBadge();
                        SQLiteManager.deleteAllIntro();
                        Intent intent_Login = new Intent(Tabs.this, LogIn.class);
                        startActivity(intent_Login);
                        finish();
                    }
                });

            dialog.show();

        }
    }

    private void updateBadge(String code){
        for(int i=0;i<testViewModel.getFriend().size();i++){
            if(code.equals(testViewModel.getFriend().get(i).getCode())){
                testViewModel.getFriend().get(i).setUnReadNum(SQLiteManager.querySingleRoomBadge(testViewModel.getFriend().get(i).getCode()));
                break;
            }
        }
        for(int i =0;i<testViewModel.getGroup().size();i++){
            if(code.equals(testViewModel.getGroup().get(i).getCode())){
                testViewModel.getGroup().get(i).setUnReadNum(SQLiteManager.querySingleRoomBadge(testViewModel.getGroup().get(i).getCode()));
                break;
            }
        }

        viewPager.getAdapter().notifyDataSetChanged();
        qBadgeView.setBadgeNumber(SQLiteManager.getTotalUnread());
    }

    private void check_SIP_permission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.USE_SIP ,Manifest.permission.RECORD_AUDIO},0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0 :
                if(grantResults.length > 0){
                    boolean use_sip_permit = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean record_audio_permit = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(use_sip_permit && record_audio_permit){
//                        init_SIP();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void init_SIP(){
//        Toast.makeText(this,"INIT",Toast.LENGTH_LONG).show();
        sipData = new SipData(this);
        sipData.initializeManager();

        IntentFilter filter = new IntentFilter();
        filter.addAction(sipData.intentAction);
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);
    }
}

