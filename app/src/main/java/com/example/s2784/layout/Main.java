package com.example.s2784.layout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends AppCompatActivity {
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,ArrayList<RoomInfo>> listHash;


    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;

    //private String addFriendID;
    private  ArrayList<RoomInfo> group;
    private ArrayList<RoomInfo> friend;
    private String userID;

    public static ArrayList<RoomInfo> friendList = null;


    //Barcode part
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set main title
        Intent intentFromUpload = getIntent();
        userID = intentFromUpload.getStringExtra("userID");
        TextView mainTitle = (TextView)findViewById(R.id.mainTitle);
        mainTitle.setText(mainTitle.getText() + "     " + userID);

        initData();



        //Add Friedn Button
        Button btn_addFrd = findViewById(R.id.btn_addFrd);
        btn_addFrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_addFrd = new Intent(Main.this,AddFriend.class);
                //startActivity(intent_addFrd);
                startActivityForResult(intent_addFrd,REQUEST_CODE);
            }
        });

        //Join Group Button
        Button btn_joinGroup = findViewById(R.id.btn_joinGroup);
        btn_joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_joinGroup = new Intent(Main.this,JoinGroup.class);
                //startActivity(intent_addFrd);
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
                //startActivity(intent_addFrd);
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
                    RoomInfo tmp = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
                    String friendID = tmp.getStudentID();
                    String chatName = tmp.getName();

                    Intent chat = new Intent(Main.this,Chatroom.class);
                    chat.putExtra("id",userID);
                    chat.putExtra("friend_id",friendID);
                    chat.putExtra("chatName", chatName);
                    startActivity(chat);


                } else if(group_class == 0) {
                    groupPosition=1;
                    RoomInfo tmp = (RoomInfo)listAdapter.getChild(groupPosition,childPosition);
                    String chatName = tmp.getRoomName();
                    String roomID = tmp.getStudentID();

                    Intent chat = new Intent(Main.this,Chatroom.class);
                    chat.putExtra("id",userID);
                    chat.putExtra("friend_id",roomID);
                    chat.putExtra("chatName", chatName);
                    startActivity(chat);

                }

                return false;
            }
        });


//        //ListView 實作
//        ListView friendList = findViewById(R.id.friendList);
//        ListAdapter friendAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new String[]{"一", "二", "三","一", "二", "三","一", "二", "三","一", "二", "三"});
//        friendList.setAdapter(friendAdapter);

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
                        Intent intent_logIn = new Intent(Main.this,LogIn.class);
                        startActivity(intent_logIn);
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
        //group.add(new RoomInfo("This is group list."));
        friend = new ArrayList<>();
        //friend.add(new RoomInfo("This is friend list"));
//        List<String> xxx = new ArrayList<>();
//        androidStudio.add("Expandable View");
//        androidStudio.add("Expandable View");
//        androidStudio.add("Expandable View");
//        androidStudio.add("Expandable View");
//
//
//        List<String> ssss = new ArrayList<>();
//        androidStudio.add("Expandable View");
//        androidStudio.add("Expandable View");
//        androidStudio.add("Expandable View");
//        androidStudio.add("Expandable View");

        listHash.put(listDataHeader.get(0),group);
        listHash.put(listDataHeader.get(1),friend);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_CODE :
                String addFriendID = data.getStringExtra("StudentID");
                if(!userID.equals(addFriendID)) {
                    AddNewFriend(addFriendID);
                }
                break;
            case REQUEST_CODE_BuildGroup :
                RoomInfo newGroup = new RoomInfo();
                String groupName = data.getStringExtra("groupName");
                newGroup.setRoomName(groupName);
                newGroup.setMamberList(SelectFriendIntoGroup.groupMember);
                newGroup.setIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.bubble_out));
                group.add(newGroup);
                listHash.put(listDataHeader.get(0),group);
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

    private void AddNewFriend(String StudentID) {
        //friend.add(StudentID);
        //listHash.put(listDataHeader.get(1),friend);
        class GetInfo extends AsyncTask<String,Void,RoomInfo> {

            @Override
            protected void onPostExecute(RoomInfo roomInfo) {
                super.onPostExecute(roomInfo);
                friend.add(roomInfo);
                listHash.put(listDataHeader.get(1),friend);
            }

            @Override
            protected RoomInfo doInBackground(String...params) {
                String address = "http://140.116.82.52:80/phpCode/selectByStudentID.php?StudentID=" + params[0];
                String address2 = "http://140.116.82.52:80/phpCode/getPic.php?StudentID=" + params[0];
                String jsonString = null;
                Bitmap image = null;
                RoomInfo roomInfo = new RoomInfo();
                String[] result = new String[2];
                try {
                    URL url = new URL(address);
                    InputStream inputStream = url.openConnection().getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf8"));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while((line = bufferedReader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    inputStream.close();
                    jsonString = builder.toString();
                    url = new URL(address2);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = DecodeJSON(jsonString);
                roomInfo.setName(result[0]);
                roomInfo.setStudentID(result[1]);
                roomInfo.setRoomName(result[0] + "  " + result[1]);
                roomInfo.setIcon(image);
                return roomInfo;
            }
        }

        GetInfo gi = new GetInfo();
        gi.execute(StudentID);
    }

    private final String[] DecodeJSON(String input) {
        String[] info = new String[2];
        try {
            JSONArray jsonArray = new JSONArray(input);
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                info[0] = jsonData.getString("Name");
                info[1] = jsonData.getString("StudentID");
            }
        } catch (JSONException e) {
            e.printStackTrace();;
        }
        return info;
    }

}

