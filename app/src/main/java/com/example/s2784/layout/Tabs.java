package com.example.s2784.layout;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class Tabs extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener, Tab3.OnFragmentInteractionListener, Tab4.OnFragmentInteractionListener, FriendLongClickDialogFragment.FLCMListener, GroupLongClickDialogFragment.GLCMListener{

    private ArrayList<RoomInfo> arrayList= new ArrayList<>(); /*for search view*/

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NetworkInfo mNetworkInfo;
    private ConnectivityManager mConnectivityManager;
    private QBadgeView qBadgeView;
//    private String tab_string[] = {"個人頁面","聊天","公佈欄","其他"};
//    private int tab_icon_light[] = {R.drawable.friend,R.drawable.chat,R.drawable.news,R.drawable.setting};
//    private int tab_icon_dark[] = {R.drawable.friend_inactive,R.drawable.chat_inactive,R.drawable.news_inactive,R.drawable.setting_inactive};

    private static final int REQUEST_CODE_AddFriend = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;
    private static final int REQUEST_CODE_Search = 5;


    public static TestViewModel testViewModel;

    public static String userID;
    public static Mqtt_Client mqtt;


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

        Intent intentFromUpload = getIntent();
        userID = intentFromUpload.getStringExtra("userID");
        testViewModel.setUserID(userID);
        mqtt = new Mqtt_Client(this.getApplicationContext(), userID);
        mqtt.Connect();

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
//        /*for search view*/
//
//        //adapter_ForSearch = new ListAdapter(arrayList,this,Tab1.listDataHeader,testViewModel.getListHash());
//        //adapter= new ListAdapter(arrayList);
//        activityTabsBinding.listView.setAdapter(adapter_ForSearch);
//
//        activityTabsBinding.search.setActivated(true);
//        activityTabsBinding.search.setQueryHint("Type your keyword here");
//        activityTabsBinding.search.onActionViewExpanded();
//        activityTabsBinding.search.setIconified(false);
//        activityTabsBinding.search.clearFocus();
//        //click to chatroom
//        listView_search = (ListView)findViewById(R.id.list_view);
//        //listView_search.setAdapter(adapter);
//        listView_search.setOnItemClickListener(new ListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RoomInfo roomInfo = (RoomInfo) adapter_ForSearch.getChild(position);
//                String code = roomInfo.getCode();
//                Intent chat = new Intent(Tabs.this,Chatroom.class);
//                chat.putExtra("code", code);
//                chat.putExtra("id",userID);
//                startActivity(chat);
//            }
//        });
//        //click to chatroom
//
//        activityTabsBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                adapter_ForSearch.getFilter().filter(newText);
//
//                return false;
//            }
//        });
//
//        listView_search.setOnScrollListener(new ListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    case SCROLL_STATE_IDLE:
//                        //scroll was stopped, let's show search bar again
//                        break;
//                    case SCROLL_STATE_TOUCH_SCROLL:
//                        //user is scrolling, let's hide search bar
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem > 0) {
//                    //user scrolled down, first element is hidden
//                }
//            }
//
//        });
//
//
//        /*for search view*/


        Log.d("TAG", "Tabs : onCtrate()");

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
        MyBroadcastReceiver receiver = new MyBroadcastReceiver();
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

        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
            netReceiver = null;
        }

        Log.d("TAG", "Tabs : onDestroy()");
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
                    intent_search.putParcelableArrayListExtra("friend_and_group_list",arrayList);
                    startActivityForResult(intent_search, REQUEST_CODE_Search);
                    break;
                case R.id.build_group:
                    msg += "創建群組";
                    Intent intent_buildGroup = new Intent(Tabs.this, BuildGroup.class);
                    intent_buildGroup.putParcelableArrayListExtra("friendlist", testViewModel.getListHash().get("好友"));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("是否要登出?")
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteManager.deleteAllUser();
                        SQLiteManager.deleteAllBadge();
                        Intent intent_Login = new Intent(Tabs.this, LogIn.class);
                        startActivity(intent_Login);
                        finish();
                    }
                });
        AlertDialog about_dialog = builder.create();
        about_dialog.show();
    }

    @Override
    public void FLCM_onItemClick(DialogFragment dialog, int which, int childPos) {
        String ID = testViewModel.getFriend().get(childPos).getStudentID();
        String code = testViewModel.getFriend().get(childPos).getCode();
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setCode(code);
        roomInfo.setStudentID(ID);
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
        }
    }

    @Override
    public void GLCM_onItemClick(DialogFragment dialog, int which, int childPos) {
        String ID = testViewModel.getGroup().get(childPos).getRoomName();
        String code = testViewModel.getGroup().get(childPos).getCode();
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setCode(code);
        roomInfo.setRoomName(ID);
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

        private Mqtt_Client(Context context, String user) {
            this.context = context;
            this.user = user;
            roomInfoList = new ArrayList<>();
            friendInfoMap = new HashMap<>();
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
                                StringTokenizer stringTokenizer = new StringTokenizer(new String(message.getPayload()), ",");
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
                                                break;
                                            }
                                        }
                                    }else{
                                        roomInfo.setStudentID("");
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
                                    GetFriendIcon("Add", addFriend_info[2], addFriend_info[4]);
                                } else if (addFriend_info[0].equals("false")) {
                                    Toast.makeText(Tabs.this, "加入好友失敗", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "GetUserData":
                                String name = new String(message.getPayload());
                                GetUserData_re(name);
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
                            case "SendNewChatroom":
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

                                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_out);
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
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_out);
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
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.book);
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

        private void GetUserData_re(String name){
            testViewModel.setUserName(name);
            viewPager.getAdapter().notifyDataSetChanged();
            GetUserIcon();
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
                studentData.putExtra("image", roomInfo.getIcon_data());
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
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_out);
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

        public void SendImg(Uri uri, String code) {
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
                String topic = "IDF/SendImg/" + user + "/" + code;
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

        private void SubmitFCMToken() {
            String token = getSharedPreferences("FCM_Token", MODE_PRIVATE).getString("token", "empty");
            if(token.equals("empty")) {
                Toast.makeText(Tabs.this,"FCM token error. 請重新安裝或清除資料再啟動", Toast.LENGTH_LONG).show();
                System.exit(0);
            }
            String topic = "IDF/SubmitFCMToken/" + user;
            try {
                client.publish(topic, token.getBytes(), 2, false);
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
            Toast.makeText(Tabs.this, "網路未連線，請檢查網路狀態", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Tabs.this, "網路已連線", Toast.LENGTH_SHORT).show();
                            break;
                        case 1: //wifi网络
                            Log.d("Feeee", "wifi");
                            Toast.makeText(Tabs.this, "網路已連線", Toast.LENGTH_SHORT).show();
                            break;

                        case 9:  //网线连接
                            Log.d("Feeee", "有網路");
                            break;
                    }
                } else {// 无网络
                    Log.d("Feeee", "沒有網路");
                    Toast.makeText(Tabs.this, "網路未連線，請檢查網路狀態", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };



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
}
