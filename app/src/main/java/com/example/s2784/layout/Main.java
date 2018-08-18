package com.example.s2784.layout;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
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

//for searchview

import android.databinding.DataBindingUtil;
import android.support.v7.widget.SearchView;

import com.example.s2784.layout.databinding.ActivityMainBinding;


//for searchview

public class Main extends AppCompatActivity implements FriendLongClickDialogFragment.FLCMListener, GroupLongClickDialogFragment.GLCMListener,
        Tab1.OnFragmentInteractionListener,Tab2.OnFragmentInteractionListener,Tab3.OnFragmentInteractionListener{
    /*for search view*/

    ActivityMainBinding activityMainBinding;
    ListAdapter adapter;
    public static List<RoomInfo> arrayList= new ArrayList<>();
    private ListView listView_search;

    /*for search view*/

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,ArrayList<RoomInfo>> listHash;


    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;

    private  ArrayList<RoomInfo> group;
    private ArrayList<RoomInfo> friend;
    private String userID;

    public static ArrayList<RoomInfo> friendList = null;


    public static Mqtt_Client mqtt;

    private Context mCtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);




        Log.d("TAG","OnCreate!");

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //set main title
        Intent intentFromUpload = getIntent();
        userID = intentFromUpload.getStringExtra("userID");
        TextView mainTitle = (TextView)findViewById(R.id.mainTitle);
        mainTitle.setText(mainTitle.getText() + "     " + userID);

        mCtn = this.getApplicationContext();

        mqtt = new Mqtt_Client(this.getApplicationContext(),userID);

        initData();

        mqtt.Connect();


        //Add Friedn Button
        Button btn_addFrd = findViewById(R.id.btn_addFrd);
        btn_addFrd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_addFrd = new Intent(Main.this,AddFriend.class);
                startActivityForResult(intent_addFrd,REQUEST_CODE);
            }
        });

        //Join Group Button
        Button btn_joinGroup = findViewById(R.id.btn_joinGroup);
        btn_joinGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_joinGroup = new Intent(Main.this,JoinGroup.class);
                startActivityForResult(intent_joinGroup,REQUEST_CODE_JoinGroup);
            }
        });
        //Build Group Button
        Button btn_buildGroup = findViewById(R.id.btn_buildGroup);
        btn_buildGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                friendList = listHash.get(listDataHeader.get(1));
                Intent intent_buildGroup = new Intent(Main.this,BuildGroup.class);

                startActivityForResult(intent_buildGroup,REQUEST_CODE_BuildGroup);
            }
        });

        //Msg Bulletin Button
        Button btn_msgBulletin = findViewById(R.id.btn_msgBulletin);
        btn_msgBulletin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_msgBulletin = new Intent(Main.this,MsgBulletin.class);
                startActivityForResult(intent_msgBulletin,REQUEST_CODE_MsgBulletin);
            }
        });

        //Logout Button
        Button btn_logOut = findViewById(R.id.btn_logOut);
        btn_logOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Leave();
            }
        });


        listView = (ExpandableListView)findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RoomInfo roomInfo = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
                String code = roomInfo.getCode();

                Intent chat = new Intent(Main.this,Chatroom.class);
                chat.putExtra("code", code);
                chat.putExtra("id",userID);
                startActivity(chat);

                return false;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(position);
                    int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
                    int childPos = ExpandableListView.getPackedPositionChild(packedPos);


                    if (groupPos == 1) {
                        showFLCM(childPos);
                    } else if(groupPos == 0) {
                        showGLCM(childPos);
                    }
                }

                //必須回傳true,不然會和onClick搞混
                return true;
            }
        });

//        /*for search view*/
//
////        arrayList.add("January");  //for search view test
////        arrayList.add("February");
//        //adapter = new ListAdapter(arrayList,this,listDataHeader,listHash);
//        //adapter= new ListAdapter(arrayList);
//        activityMainBinding.listView.setAdapter(adapter);
//
//        activityMainBinding.search.setActivated(true);
//        activityMainBinding.search.setQueryHint("Type your keyword here");
//        activityMainBinding.search.onActionViewExpanded();
//        activityMainBinding.search.setIconified(false);
//        activityMainBinding.search.clearFocus();
//
//        //click to chatroom
//        listView_search = (ListView)findViewById(R.id.list_view);
//        //listView_search.setAdapter(adapter);
//        listView_search.setOnItemClickListener(new ListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RoomInfo roomInfo = (RoomInfo) adapter.getChild(position);
//                String code = roomInfo.getCode();
//                Intent chat = new Intent(Main.this,Chatroom.class);
//                chat.putExtra("code", code);
//                chat.putExtra("id",userID);
//                startActivity(chat);
//            }
//        });
//        //click to chatroom
//
//
//        activityMainBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                adapter.getFilter().filter(newText);
//
//                return false;
//            }
//        });
//
//
//        /*for search view*/



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG","onDes!");
        mqtt.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG","onStop!");
    }

    public String getUserID(){
        return userID;
    }

    private void Leave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("是否要登出?")
                .setPositiveButton("否", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StartInterface.logout();
                        Intent intent_Login = new Intent(Main.this,LogIn.class);
                        startActivity(intent_Login);
                        finish();
                    }
                });
        AlertDialog about_dialog = builder.create();
        about_dialog.show();
    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        listDataHeader.add("群組");
        listDataHeader.add("好友");

        group = new ArrayList<>();
        friend = new ArrayList<>();

        listHash.put(listDataHeader.get(0),group);
        listHash.put(listDataHeader.get(1),friend);


    }

    private void showFLCM(int childPos) {
        DialogFragment dialogFragment = new FriendLongClickDialogFragment();
        //用Bundle傳遞參數
        Bundle bundle = new Bundle();
        bundle.putInt("childPos",childPos);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "FLCM");

    }
    @Override
    public void FLCM_onItemClick(DialogFragment dialog, int which, int childPos) {
        String ID = friend.get(childPos).getStudentID();
        String code = friend.get(childPos).getCode();
        switch (which) {
            case 0:
                //指定哪個朋友要被刪除
                mqtt.setDeleteFriendPos(childPos);
                mqtt.DeleteFriend(ID, code);
                break;
        }
    }

    private void showGLCM(int childPos) {
        DialogFragment dialogFragment = new GroupLongClickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("childPos",childPos);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "GLCM");
    }
    @Override
    public void GLCM_onItemClick(DialogFragment dialogFragment, int which, int childPos) {
        String code = group.get(childPos).getCode();
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
            case REQUEST_CODE :
                String addFriendID = data.getStringExtra("StudentID");
                mqtt.AddFriend(addFriendID);
                break;
            case REQUEST_CODE_BuildGroup :
                String groupName = data.getStringExtra("groupName");
                ArrayList<String> groupMember = data.getStringArrayListExtra("memberList");

                String member_str = userID;
                if(groupMember != null) {
                    for (int i = 0; i < groupMember.size(); ++i) {
                        member_str += ("\t" + groupMember.get(i));
                    }
                }
                mqtt.AddGroup(groupName,member_str);
                friendList.clear();
                break;
            case REQUEST_CODE_JoinGroup:
//                RoomInfo newGroup = new RoomInfo();
//                String groupName = data.getStringExtra("groupName");
//                newGroup.setRoomName(groupName);
//                newGroup.setIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.bubble_out));
//                group.add(newGroup);
//                listHash.put(listDataHeader.get(0),group);
                break;
            case REQUEST_CODE_MsgBulletin :

                break;
        }
    }

    ////////////////////////////////////////////////////////

    public void refreshExpListView() {
        listView.getFirstVisiblePosition();
    }

    @Override
    //////////////////  ViewPager  /////////////////////
    public void onFragmentInteraction(Uri uri) {

    }


    ////////////////////////////////////////////////////////
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
        private HashMap<String,Bitmap> friendInfoMap = null;

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

            client.setCallback(new MqttCallbackExtended()  {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if(reconnect){
                        mqttSub();
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("TAG","lost");
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
                            StringTokenizer stringTokenizer = new StringTokenizer(new String(message.getPayload()) ,",");
                            while(stringTokenizer.hasMoreElements()){
                                String token = stringTokenizer.nextToken();
                                init_info = token.split("\t");
                                RoomInfo roomInfo = new RoomInfo();
                                roomInfo.setCode(init_info[0]);
                                roomInfo.setRoomName(init_info[1]);
                                roomInfo.setStudentID(init_info[2]);
                                roomInfo.setType(init_info[3]);
                                roomInfoList.add(roomInfo);
                                if(init_info[3].equals("F")){
                                    GetFriendIcon("Init", init_info[2]);
                                }else if(init_info[3].equals("G")){
                                    Initialize_re(roomInfo);
                                }
                            }
                            break;
                        case "AddFriend" :
                            addFriend_info = new String(message.getPayload()).split("/");
                            if(addFriend_info[0].equals("true")){
                                RoomInfo roomInfo = new RoomInfo();
                                roomInfo.setFriendName(addFriend_info[1]);
                                roomInfo.setRoomName(addFriend_info[1]);
                                roomInfo.setStudentID(addFriend_info[2]);
                                roomInfo.setCode(addFriend_info[3]);
                                roomInfo.setType("F");
                                roomInfoList.add(roomInfo);
                                GetFriendIcon("Add",addFriend_info[2]);
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
                        case "AddGroup" :
                            String[] AG_msg = (new String(message.getPayload())).split("/");
                            if(AG_msg[0].equals("true")) {
                                AddGroup_re(AG_msg[1],AG_msg[2]);
                            } else {
                                Toast.makeText(context,"Error creating group", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "DeleteFriend" :
                            String DF_msg = new String(message.getPayload());
                            if(DF_msg.equals("true")) {
                                DeleteFriend_re();
                            } else {
                                deleteFriendPos = -1;
                            }
                            break;
                        case "WithdrawFromGroup" :
                            String WG_msg = new String(message.getPayload());
                            if(WG_msg.equals("true")) {
                                WithdrawFromGroup_re();
                            } else {
                                withdrawGroupPos = -1;
                            }
                            break;
                        case "SendMessage" :
                            String SM_msg = new String(message.getPayload());
                            String[] SM_msg_splitLine = SM_msg.split("\t");
                            if(processingCode.equals(SM_msg_splitLine[0])){
                                LinkModule.getInstance().callUpdateMsg(SM_msg_splitLine[1],SM_msg_splitLine[2],SM_msg_splitLine[3]);
                            }
                            break;
                        case "GetRecord" :
                            LinkModule.getInstance().callFetchRecord(new String(message.getPayload()));
                            break;
                        default:
                            if(idf[1].contains("FriendIcon")){
                                if(idf[1].contains("Init")) {
                                    String[] topicSplitLine = idf[1].split(":");
                                    Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                    friendInfoMap.put(topicSplitLine[1], b);
                                    for (int i = 0; i < roomInfoList.size(); i++) {
                                        if (roomInfoList.get(i).getStudentID().equals(topicSplitLine[1])) {
                                            roomInfoList.get(i).setIcon(b);
                                            Initialize_re(roomInfoList.get(i));
                                            roomInfoList.remove(i);
                                            break;
                                        }
                                    }
                                }else if(idf[1].contains("Add")){ ;
                                    String[] topicSplitLine = idf[1].split(":");
                                    Log.d("Create", topicSplitLine[1]);
                                    Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                                    friendInfoMap.put(topicSplitLine[1], b);
                                    for (int i = 0; i < roomInfoList.size(); i++) {
                                        if (roomInfoList.get(i).getStudentID().equals(topicSplitLine[1])) {
                                            roomInfoList.get(i).setIcon(b);
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
            if(client != null && client.isConnected()) {
                try {
                    client.unsubscribe("IDF/+/" + user + "/Re");
                    client.disconnect();
                    client.unregisterResources();
                    client = null;
                    Log.d("TAG","Try Disconnect");
                } catch (MqttException e) {
                    Log.d("TAG","Disconnect Error");
                    e.printStackTrace();
                }
            }
        }


        private void mqttSub() {
            try {
                String topic = "IDF/+/" + user + "/Re";
                client.subscribe(topic,2);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Initialize() {
            String topic = "IDF/Initialize/" + user;
            String MSG = "";
            try {
                client.publish(topic,MSG.getBytes(),2,false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Initialize_re(RoomInfo roomInfo) {
            if(roomInfo.getType().equals("F")) {
                friend.add(roomInfo);
                arrayList.add(roomInfo);
                listHash.put(listDataHeader.get(1),friend);
            } else if(roomInfo.getType().equals("G")) {
                roomInfo.setIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.bubble_out));
                group.add(roomInfo);
                arrayList.add(roomInfo);
                listHash.put(listDataHeader.get(0),group);
            }
        }

        public void AddFriend(String friendID) {
            String topic = "IDF/AddFriend/" + user;
            String MSG = friendID;
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            }catch (MqttException e) {
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
            friend.add(roomInfo);
            listHash.put(listDataHeader.get(1),friend);
            Intent studentData = new Intent(Main.this, StudentData.class);
            studentData.putExtra("name", roomInfo.getFriendName());
            studentData.putExtra("ID", roomInfo.getStudentID());
            studentData.putExtra("image", roomInfo.getIcon_data());
            startActivity(studentData);
        }

        public void AddGroup(String groupName, String member_str) {
            String topic  = "IDF/AddGroup/" + user;
            String MSG = groupName + "\t" + member_str;
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void AddGroup_re(String code, String groupName) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setCode(code);
            roomInfo.setRoomName(groupName);
            roomInfo.setIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.bubble_out));
            group.add(roomInfo);
            listHash.put(listDataHeader.get(0),group);
        }

        public void DeleteFriend(String friendID, String code) {
            String topic = "IDF/DeleteFriend/" + user;
            String MSG = friendID + "/" + code;
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void setDeleteFriendPos(int deleteFriendPos) {
            this.deleteFriendPos = deleteFriendPos;
        }

        public void DeleteFriend_re() {
            friend.remove(deleteFriendPos);
            listHash.put(listDataHeader.get(1),friend);
            deleteFriendPos = -1;
        }

        public void WithdrawFromGroup(String code) {
            String topic = "IDF/WithdrawFromGroup/" + user;
            String MSG = code;
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void setWithdrawGroupPos(int withdrawGroupPos) {
            this.withdrawGroupPos = withdrawGroupPos;
        }

        public void WithdrawFromGroup_re() {
            group.remove(withdrawGroupPos);
            listHash.put(listDataHeader.get(0),group);
            withdrawGroupPos = -1;
        }

        public void setProcessingCode(String processingCode) {
            this.processingCode = processingCode;
        }

        public void SendMessage(String str){
            String topic = "IDF/SendMessage/" + user;
            String MSG = str;
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void GetRecord(String code) {
            String topic = "IDF/GetRecord/" + user;
            String MSG = code;
            try {
                client.publish(topic,MSG.getBytes(),2,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void GetFriendIcon(String action, String friendID){
            String topic = "IDF/FriendIcon/" + user;
            String MSG = action + ":" + friendID;
            try {
                client.publish(topic,MSG.getBytes(),2,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public Bitmap MapBitmap(String id){
            return friendInfoMap.get(id);
        }
    }
    ////////////////////////////////////////////////////////////////////////

}

