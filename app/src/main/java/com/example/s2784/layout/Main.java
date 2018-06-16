package com.example.s2784.layout;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Main extends AppCompatActivity {
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

    public Mqtt_Client mqtt;

    private Context mCtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        btn_addFrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_addFrd = new Intent(Main.this,AddFriend.class);
                startActivityForResult(intent_addFrd,REQUEST_CODE);
            }
        });

        //Join Group Button
        Button btn_joinGroup = findViewById(R.id.btn_joinGroup);
        btn_joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_joinGroup = new Intent(Main.this,JoinGroup.class);
                startActivityForResult(intent_joinGroup,REQUEST_CODE_JoinGroup);
            }
        });
        //Build Group Button
        Button btn_buildGroup = findViewById(R.id.btn_buildGroup);
        btn_buildGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendList = listHash.get(listDataHeader.get(1));
                Intent intent_buildGroup = new Intent(Main.this,BuildGroup.class);
                /*
                Bundle bundle = new Bundle();
                bundle.putSerializable("A",(Serializable)friendList);
                intent_buildGroup.putExtra("BUNDLE",bundle);
                startActivity(intent_buildGroup);
                */
                //intent_buildGroup.putExtra("friendList", (Serializable) friendList);
                startActivityForResult(intent_buildGroup,REQUEST_CODE_BuildGroup);
            }
        });

        //Msg Bulletin Button
        Button btn_msgBulletin = findViewById(R.id.btn_msgBulletin);
        btn_msgBulletin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_msgBulletin = new Intent(Main.this,MsgBulletin.class);
                startActivityForResult(intent_msgBulletin,REQUEST_CODE_MsgBulletin);
            }
        });

        //Logout Button
        Button btn_logOut = findViewById(R.id.btn_logOut);
        btn_logOut.setOnClickListener(new View.OnClickListener() {
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
                int group_class = (int)listAdapter.getGroupId(groupPosition);
                if(group_class == 1){
                    //Log.d("Tag","好友click");
//                    RoomInfo tmp = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
//                    String friendID = tmp.getStudentID();
//                    String chatName = tmp.getRoomName();
//
//                    Intent chat = new Intent(Main.this,Chatroom.class);
//                    chat.putExtra("id",userID);
//                    chat.putExtra("friend_id",friendID);
//                    chat.putExtra("chatName", chatName);
//                    startActivity(chat);
                    Toast.makeText(mCtn,"(" + groupPosition + "," + childPosition + ") click",Toast.LENGTH_SHORT).show();

                } else if(group_class == 0) {
//                    groupPosition=1;
//                    RoomInfo tmp = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
//                    String chatName = tmp.getRoomName();
//                    String roomID = tmp.getStudentID();
//
//                    Intent chat = new Intent(Main.this,Chatroom.class);
//                    chat.putExtra("id",userID);
//                    chat.putExtra("friend_id",roomID);
//                    chat.putExtra("chatName", chatName);
//                    startActivity(chat);
                    Toast.makeText(mCtn,"(" + groupPosition + "," + childPosition + ") click",Toast.LENGTH_SHORT).show();
                }

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

                    Toast.makeText(mCtn,"(" + groupPos + "," + childPos + ") long click",Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }

    private void Leave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("是否要登出?")
                .setPositiveButton("否", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StartInterface.logout();
                        Intent intent_StartInterface = new Intent(Main.this,StartInterface.class);
                        startActivity(intent_StartInterface);
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
    private class Mqtt_Client {
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

        public Mqtt_Client(Context context, String user) {
            this.context = context;
            this.user = user;
        }



        public void Connect() {
            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(context, MQTT_HOST, clientId);

            options = new MqttConnectOptions();
            options.setCleanSession(true);

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

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String[] idf = topic.split("/");
                    switch (idf[1]) {
                        case "Initialize":
                            if(init_flag == 0) {
                                init_info = (new String(message.getPayload())).split("\t");
                                if(init_info[3].equals("F")) {
                                    init_flag = 1;
                                } else if(init_info[3].equals("G")) {
                                    Initialize_re(init_info[0], init_info[1], init_info[2], init_info[3], null);
                                }
                            } else {
                                Bitmap b = BitmapFactory.decodeByteArray(message.getPayload(),0,message.getPayload().length);
                                init_flag = 0;
                                Initialize_re(init_info[0], init_info[1], init_info[2], init_info[3], b);
                            }
                            break;
                        case "AddFriend" :
                            if(addFriend_flag == 0) {
                                addFriend_info = (new String(message.getPayload())).split("/");
                                if(addFriend_info[0].equals("true")) {
                                    addFriend_flag = 1;
                                    sdtDta_flag = true;
                                }
                            } else {
                                byte[] bit = message.getPayload();
                                Bitmap b = BitmapFactory.decodeByteArray(bit,0,bit.length);

                                if(sdtDta_flag) {
                                    sdtDta_flag = false;
                                    Intent studentData = new Intent(Main.this, StudentData.class);
                                    studentData.putExtra("name", addFriend_info[1]);
                                    studentData.putExtra("ID", addFriend_info[2]);
                                    studentData.putExtra("image", bit);
                                    startActivity(studentData);
                                }
                                AddFriend_re(addFriend_info[3],addFriend_info[2],addFriend_info[1],b);
                                addFriend_flag = 0;
                            }
                            break;
                        case "AddGroup" :
                            String[] msg = (new String(message.getPayload())).split("/");
                            if(msg[0].equals("true")) {
                                AddGroup_re(msg[1],msg[2]);
                            } else {
                                Toast.makeText(context,"Error creating group", Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }


        private void mqttSub() {
            try {
                String topic = "IDF/+/" + user + "/Re";
                client.subscribe(topic,0);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Initialize() {
            String topic = "IDF/Initialize/" + user;
            String MSG = "";
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Initialize_re(String code, String roomName, String ID, String type, Bitmap b) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setCode(code);
            roomInfo.setRoomName(roomName);
            if(type.equals("F")) {
                roomInfo.setStudentID(ID);
                roomInfo.setIcon(b);
                friend.add(roomInfo);
                listHash.put(listDataHeader.get(1),friend);
            } else if(type.equals("G")) {
                roomInfo.setIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.bubble_out));
                group.add(roomInfo);
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

        public void AddFriend_re(String code, String ID, String roomName, Bitmap b) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setCode(code);
            roomInfo.setStudentID(ID);
            roomInfo.setRoomName(roomName);
            roomInfo.setIcon(b);
            friend.add(roomInfo);
            listHash.put(listDataHeader.get(1),friend);
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

    }
    ////////////////////////////////////////////////////////////////////////
}

