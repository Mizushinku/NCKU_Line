package com.example.s2784.layout;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class Classroom extends Chatroom{
    private String course_letters[] = {"選擇圖片"};
    private int course_icons[] = {R.drawable.pic};

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
            Toast.makeText(this, "u r ★3", Toast.LENGTH_SHORT).show();
        } else if(auth == 1) {
            Toast.makeText(this, "u r ☆5", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void set_gridAdapter() {
        if(roomInfo.getType().equals("C")){
            gridAdapter = new Grid_Adapter(this,course_icons,course_letters);
        }
    }

    @Override
    protected void set_gridview_onItemClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(roomInfo.getType().equals("C")) {
                    switch (position) {
                        case 0:
                            choosePic();
                            break;
                        default:
                            Toast.makeText(Classroom.this, "Wrong", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }
}
