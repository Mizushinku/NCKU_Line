package com.example.s2784.layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Browser;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.s2784.layout.databinding.ActivityTabsBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

//for searchview

import android.databinding.DataBindingUtil;
import android.support.v7.widget.SearchView;
import android.widget.ListView;

import com.example.s2784.layout.databinding.ActivityMainBinding;


//for searchview

public class Tabs extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener, Tab3.OnFragmentInteractionListener, Tab4.OnFragmentInteractionListener, FriendLongClickDialogFragment.FLCMListener, GroupLongClickDialogFragment.GLCMListener {

    /*for search view*/

    ActivityTabsBinding activityTabsBinding;
    ListAdapter adapter_ForSearch;
    //public static List<RoomInfo> arrayList= new ArrayList<>();
    private ListView listView_search;
    private ExpandableListView ExpListView_Tab1;

    /*for search view*/

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;
    private final static int CAMERA_RESULT = 0;

    public static TestViewModel testViewModel;

    public static String userID;
    public static Mqtt_Client mqtt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activityTabsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tabs);
        setContentView(R.layout.activity_tabs);


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


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friend).setText("個人頁面"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_inactive).setText("聊天"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.news_inactive).setText("公佈欄"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_inactive).setText("其他"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabs_pic_inactive), getResources().getColor(R.color.white));


        viewPager = (ViewPager) findViewById(R.id.pager);
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
                switch (position) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend);
                        tabLayout.getTabAt(1).setIcon(R.drawable.chat_inactive);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                        mainTitle.setText("好友&群組");
                        break;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                        tabLayout.getTabAt(1).setIcon(R.drawable.chat);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                        mainTitle.setText("聊天室");
                        break;
                    case 2:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                        tabLayout.getTabAt(1).setIcon(R.drawable.chat_inactive);
                        tabLayout.getTabAt(2).setIcon(R.drawable.news);
                        tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                        mainTitle.setText("公佈欄");
                        break;
                    case 3:
                        tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                        tabLayout.getTabAt(1).setIcon(R.drawable.chat_inactive);
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
                Log.d("item", "click settings~~~~");
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
                    startActivityForResult(intent_search, REQUEST_CODE);
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
                    startActivityForResult(intent_addFriend, REQUEST_CODE);
                    break;
                case R.id.log_out:
                    msg += "登出";
                    Leave();
                    break;
            }

            if (!msg.equals("")) {
                Toast.makeText(Tabs.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqtt.disconnect();
    }

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
                        StartInterface.logout();
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
        switch (which) {
            case 0:
                //指定哪個朋友要被刪除
                mqtt.setDeleteFriendPos(childPos);
                mqtt.DeleteFriend(ID, code);

                break;
        }
    }

    @Override
    public void GLCM_onItemClick(DialogFragment dialog, int which, int childPos) {
        String code = testViewModel.getGroup().get(childPos).getCode();
        switch (which) {
            case 0:
                //指定要退出哪個群組
                mqtt.setWithdrawGroupPos(childPos);
                mqtt.WithdrawFromGroup(code);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_CODE:
                String addFriendID = data.getStringExtra("StudentID");
                mqtt.AddFriend(addFriendID);
                break;
            case REQUEST_CODE_BuildGroup:
                String groupName = data.getStringExtra("groupName");
                ArrayList<String> groupMember = data.getStringArrayListExtra("memberList");

                String member_str = userID;
                if (groupMember != null) {
                    for (int i = 0; i < groupMember.size(); ++i) {
                        member_str += ("\t" + groupMember.get(i));
                    }
                }
                mqtt.AddGroup(groupName, member_str);
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
        }
    }

    public class Mqtt_Client {
        private static final String MQTT_HOST = "tcp://140.116.82.52:1883";
        private MqttAndroidClient client;
        private MqttConnectOptions options;

        private Context context;
        private String user;

        private int init_flag = 0;
        private String[] init_info = null;

        private int addFriend_flag = 0;
        private String[] addFriend_info = null;
        private boolean sdtDta_flag = false;

        private int deleteFriendPos = -1;
        private int withdrawGroupPos = -1;

        private String processingCode = "";
        private ArrayList<RoomInfo> roomInfoList = null; // 暫存用 並非更新至正確資料 正確版本存在Main底下的friend,group
        private HashMap<String, Bitmap> friendInfoMap = null;

        public Mqtt_Client(Context context, String user) {
            this.context = context;
            this.user = user;
            roomInfoList = new ArrayList<RoomInfo>();
            friendInfoMap = new HashMap<String, Bitmap>();
        }


        public void Connect() {
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
                        Initialize();
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
                    Log.d("TAG", "lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String[] idf = topic.split("/");
//                    Log.d("MSG","MSG: " + message.toString());
                    switch (idf[1]) {
                        case "Initialize":
//                            if(init_flag == 0) {
//                                init_info = (new String(message.getPayload())).split("\t");
//                                if(init_info[3].equals("F")) {
//                                    init_flag = 1;
//                                } else if(init_info[3].equals("G")) {
//                                    Initialize_re(init_info[0], init_info[1], init_info[2], init_info[3], null);
//                                }
//                            } else {
//                                Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(),0,message.getPayload().length);
//                                init_flag = 0;
//                                Initialize_re(init_info[0], init_info[1], init_info[2], init_info[3], b);
//                            }
                            StringTokenizer stringTokenizer = new StringTokenizer(new String(message.getPayload()), ",");
                            while (stringTokenizer.hasMoreElements()) {
                                String token = stringTokenizer.nextToken();
                                init_info = token.split("\t");
                                RoomInfo roomInfo = new RoomInfo();
                                roomInfo.setCode(init_info[0]);
                                roomInfo.setRoomName(init_info[1]);
                                roomInfo.setStudentID(init_info[2]);
                                roomInfo.setType(init_info[3]);
                                roomInfo.setrMsg(init_info[4]);
                                roomInfo.setrMsgDate(init_info[5]);
                                roomInfoList.add(roomInfo);
                                if (init_info[3].equals("F")) {
                                    GetFriendIcon("Init", init_info[2]);
                                } else if (init_info[3].equals("G")) {
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
                                roomInfo.setCode(addFriend_info[3]);
                                roomInfo.setType("F");
                                roomInfoList.add(roomInfo);
                                GetFriendIcon("Add", addFriend_info[2]);
                            } else if (addFriend_info[0].equals("false")) {
                                Toast.makeText(Tabs.this, "加入好友失敗", Toast.LENGTH_SHORT).show();
                            }
//                            if(addFriend_flag == 0) {
//                                addFriend_info = (new String(message.getPayload())).split("/");
//                                if(addFriend_info[0].equals("true")) {
//                                    addFriend_flag = 1;
//                                    sdtDta_flag = true;
//                                }
//                            } else {
//                                byte[] bit = message.getPayload();
//                                Bitmap b = BitmapFactory.decodeByteArray(bit,0,bit.length);
//
//                                if(sdtDta_flag) {
//                                    sdtDta_flag = false;
//                                    Intent studentData = new Intent(Main.this, StudentData.class);
//                                    studentData.putExtra("name", addFriend_info[1]);
//                                    studentData.putExtra("ID", addFriend_info[2]);
//                                    studentData.putExtra("image", bit);
//                                    startActivity(studentData);
//                                }
//                                AddFriend_re(addFriend_info[3],addFriend_info[2],addFriend_info[1],b);
//                                addFriend_flag = 0;
//                            }
                            break;
                        case "AddGroup":
                            String[] AG_msg = (new String(message.getPayload())).split("/");
                            if (AG_msg[0].equals("true")) {
                                AddGroup_re(AG_msg[1], AG_msg[2]);
                            } else {
                                Toast.makeText(context, "Error creating group" + AG_msg[0], Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "DeleteFriend":
                            String DF_msg = new String(message.getPayload());
                            if (DF_msg.equals("true")) {
                                DeleteFriend_re();
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
                            for (int i = 0; i < testViewModel.getGroup().size(); i++) {
                                if (testViewModel.getGroup().get(i).getCode().equals(SM_msg_splitLine[0])) {
                                    testViewModel.getGroup().get(i).setrMsg(SM_msg_splitLine[2]);
                                    testViewModel.getGroup().get(i).setrMsgDate(SM_msg_splitLine[3]);
                                    break;
                                }
                            }
                            for (int i = 0; i < testViewModel.getFriend().size(); i++) {
                                if (testViewModel.getFriend().get(i).getCode().equals(SM_msg_splitLine[0])) {
                                    testViewModel.getFriend().get(i).setrMsg(SM_msg_splitLine[2]);
                                    testViewModel.getFriend().get(i).setrMsgDate(SM_msg_splitLine[3]);
                                    break;
                                }
                            }
                            if (processingCode.equals(SM_msg_splitLine[0])) {
                                LinkModule.getInstance().callUpdateMsg(SM_msg_splitLine[1], SM_msg_splitLine[2], SM_msg_splitLine[3]);
                            }
                            break;
                        case "GetRecord":
                            LinkModule.getInstance().callFetchRecord(new String(message.getPayload()));
                            break;
                        case "AddFriendNotification":
                            AddFriendNotification_re();
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
                                    ;
                                    String[] topicSplitLine = idf[1].split(":");
                                    Log.d("Create", topicSplitLine[1]);
                                    Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                    friendInfoMap.put(topicSplitLine[1], b);
                                    for (int i = 0; i < roomInfoList.size(); i++) {
                                        if (roomInfoList.get(i).getStudentID().equals(topicSplitLine[1])) {
                                            roomInfoList.get(i).setIcon_data(message.getPayload());
                                            AddFriend_re(roomInfoList.get(i));
                                            roomInfoList.remove(i);
                                            break;
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

        public void disconnect() {
            if (client != null && client.isConnected()) {
                try {
                    client.unsubscribe("IDF/+/" + user + "/Re");
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
                String topic = "IDF/+/" + user + "/Re";
                client.subscribe(topic, 2);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Initialize() {
            String topic = "IDF/Initialize/" + user;
            String MSG = "";
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Initialize_re(RoomInfo roomInfo) {
            if (roomInfo.getType().equals("F")) {
                testViewModel.addInFriend(roomInfo);
                com.example.s2784.layout.SearchView.arrayList.add(roomInfo);
                testViewModel.putListHash("好友", testViewModel.getFriend());
            } else if (roomInfo.getType().equals("G")) {
                Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_out);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                roomInfo.setIcon_data(bytes);
                testViewModel.addInGroup(roomInfo);
                com.example.s2784.layout.SearchView.arrayList.add(roomInfo);
                testViewModel.putListHash("群組", testViewModel.getGroup());
            }
        }

        public void AddFriend(String friendID) {
            String topic = "IDF/AddFriend/" + user;
            String MSG = friendID;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void AddFriend_re(RoomInfo roomInfo) {
//            RoomInfo roomInfo = new RoomInfo();
//            roomInfo.setCode(code);
//            roomInfo.setStudentID(ID);
//            roomInfo.setRoomName(roomName);
//            roomInfo.setIcon(b);
//            Log.d("Create",roomInfo.getCode()+","+roomInfo.getFriendName()+","+roomInfo.getStudentID());
            testViewModel.addInFriend(roomInfo);
            testViewModel.putListHash("好友", testViewModel.getFriend());

            String topic = "IDF/AddFriendNotification/" + roomInfo.getStudentID();
            String MSG = "";
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
        }

        public void AddGroup(String groupName, String member_str) {
            String topic = "IDF/AddGroup/" + user;
            String MSG = groupName + "\t" + member_str;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void AddGroup_re(String code, String groupName) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setCode(code);
            roomInfo.setRoomName(groupName);
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble_out);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            roomInfo.setIcon_data(bytes);
            testViewModel.addInGroup(roomInfo);
            testViewModel.putListHash("群組", testViewModel.getGroup());
        }

        public void DeleteFriend(String friendID, String code) {
            String topic = "IDF/DeleteFriend/" + user;
            String MSG = friendID + "/" + code;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void setDeleteFriendPos(int deleteFriendPos) {
            this.deleteFriendPos = deleteFriendPos;
        }

        public void DeleteFriend_re() {
            testViewModel.removeFromFriend(deleteFriendPos);
            testViewModel.putListHash("好友", testViewModel.getFriend());
            deleteFriendPos = -1;
        }

        public void WithdrawFromGroup(String code) {
            String topic = "IDF/WithdrawFromGroup/" + user;
            String MSG = code;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void setWithdrawGroupPos(int withdrawGroupPos) {
            this.withdrawGroupPos = withdrawGroupPos;
        }

        public void WithdrawFromGroup_re() {
            testViewModel.removeFromGroup(withdrawGroupPos);
            testViewModel.putListHash("群組", testViewModel.getGroup());
            withdrawGroupPos = -1;
        }

        public void setProcessingCode(String processingCode) {
            this.processingCode = processingCode;
        }

        public void SendMessage(String str) {
            String topic = "IDF/SendMessage/" + user;
            String MSG = str;
            try {
                client.publish(topic, MSG.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void GetRecord(String code) {
            String topic = "IDF/GetRecord/" + user;
            String MSG = code;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void GetFriendIcon(String action, String friendID) {
            String topic = "IDF/FriendIcon/" + user;
            String MSG = action + ":" + friendID;
            try {
                client.publish(topic, MSG.getBytes(), 2, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void AddFriendNotification_re() {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.setting);
            builder.setContentTitle("Title : Friend request");
            builder.setContentText("Hello World!");

            //Intent intent = new Intent(context, Tabs.class);
            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //stackBuilder.addParentStack(Tabs.class);
            //stackBuilder.addNextIntent(intent);
            //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            //builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = 0;
            notificationManager.notify(notificationId,builder.build());

            Toast.makeText(context,"notification!",Toast.LENGTH_LONG).show();
        }

        public Bitmap MapBitmap(String id) {
            return friendInfoMap.get(id);
        }
    }
    ////////////////////////////////////////////////////////////////////////


}
