package com.example.s2784.layout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class DiscussActivity extends AppCompatActivity implements View.OnClickListener, PlusDialog.PlusDialogListener ,LinkModule.PListener{

    private List<CardData> dataList = new ArrayList<>();
    private CardDataAdapter cardDataAdapter;
    private final int space = 20;

    private FloatingActionButton floatingActionButton;

    private RoomInfo roomInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        Intent intent = getIntent();
        roomInfo = intent.getParcelableExtra("roomInfo");

        floatingActionButton = findViewById(R.id.fab_plus);
        floatingActionButton.setOnClickListener(this);

        cardDataAdapter = new CardDataAdapter(this,dataList);
        RecyclerView recyclerView = findViewById(R.id.discuss_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(cardDataAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));

        LinkModule.getInstance().setPListener(this);
        Tabs.mqtt.getPoster(roomInfo.getCode());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_plus:
//                Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
                openDialog();
                break;
        }
    }

    public void openDialog(){
        PlusDialog plusDialog = new PlusDialog();
        plusDialog.show(getSupportFragmentManager(),"Plus Dialog");
    }


    @Override
    public void applyTexts(String title, String content) {
//        Calendar calendar = Calendar.getInstance();
//        String dateFormat = "yyyy年MM月dd日";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
//        String today = simpleDateFormat.format(calendar.getTime());
//        String date = today.substring(0,5);
//        if(today.substring(5,6).equals("0")){
//            date = date.concat(today.substring(6,7));
//        }else{
//            date = date.concat(today.substring(5,7));
//        }
//        date = date.concat("月");
//        if(today.substring(8,9).equals("0")){
//            date = date.concat(today.substring(9,10));
//        }else{
//            date = date.concat(today.substring(8,10));
//        }
//        date = date.concat("日");
//
//        CardData cardData = new CardData(title,content, date,"NAME");
//        dataList.add(cardData);
//        cardDataAdapter.notifyDataSetChanged();
//        Toast.makeText(this,"SIZE:" + dataList.size(), Toast.LENGTH_LONG).show();
        Tabs.mqtt.addPoster(roomInfo.getCode(),title,content,"post");
    }

    @Override
    public void fetchPoster(String record) {
        StringTokenizer stringTokenizer = new StringTokenizer(record,"\r");
        while (stringTokenizer.hasMoreElements()){
            String token = stringTokenizer.nextToken();
            String token_splitLine[] = token.split("\t");
            updatePost(token_splitLine[1],token_splitLine[2],token_splitLine[3],token_splitLine[0],token_splitLine[4]);
        }
    }

    @Override
    public void updatePoster(String code, String theme, String content, String type, String sender, String time) {
        updatePost(theme,content,time,sender,type);
    }

    private void updatePost(String title, String content, String time, String name, String type){
        CardData cardData = new CardData(title,content,time,name,type);
        dataList.add(cardData);
        cardDataAdapter.notifyDataSetChanged();
    }
}
