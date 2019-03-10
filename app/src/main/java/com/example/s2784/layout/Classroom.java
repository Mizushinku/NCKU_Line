package com.example.s2784.layout;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class Classroom extends Chatroom{

    private static final int choosePic = 0;
    private static final int news = 1;
    private static final int changeAuth0 = 2;
    private static final int changeAuth1 = 3;
    private static final int changeAuth2 = 4;
    private static final int member = 5;
    private static final int document = 6;
    private static final int album = 7;
    private static final int schedule = 8;
    private static final int annoouncement = 9;
    private static final int assignTA = 10;

    private String A0_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆","公告","指派助教"};
    private String A1_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆","公告"};
    private String A2_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆",};
    private int A0_course_icons[] = {R.drawable.pic,R.drawable.pic,R.drawable.news_inactive,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar,R.drawable.announcement,R.drawable.assign_ta};
    private int A1_course_icons[] = {R.drawable.pic,R.drawable.pic,R.drawable.news_inactive,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar,R.drawable.announcement,};
    private int A2_course_icons[] = {R.drawable.pic,R.drawable.pic,R.drawable.news_inactive,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar};

    private int auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tabs.mqtt.getAuth(code);
    }

    @Override
    public void setAuth(int auth) {
        this.auth = auth;
        if(auth == 0) {
            Toast.makeText(this, "auth 0", Toast.LENGTH_SHORT).show();
        }
        else if(auth == 1) {
            Toast.makeText(this, "auth 1", Toast.LENGTH_SHORT).show();
        }
        else if(auth == 2) {
            Toast.makeText(this, "auth 2", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void set_gridAdapter() {
        if(roomInfo.getType().equals("C")){
            gridAdapter = new Grid_Adapter(this,A2_course_icons, A2_course_letters);
        }
    }

    @Override
    protected void set_gridview_onItemClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(roomInfo.getType().equals("C")) {
                    switch (position) {
                        case choosePic:
                            choosePic();
                            break;
                        case 1:
                            Intent discuss = new Intent(getApplicationContext(),DiscussActivity.class);
                            discuss.putExtra("roomInfo", roomInfo);
                            startActivity(discuss);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            break;
                        case 9:
                            break;
                        default:
                            Toast.makeText(Classroom.this, "Wrong", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }
    private void changeAuth(int auth){
        this.auth  = auth;
    }
}
