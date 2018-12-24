package com.example.s2784.layout;


import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class Classroom extends Chatroom{
    private String course_letters[] = {"選擇圖片"};
    private int course_icons[] = {R.drawable.pic};

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
