package com.example.s2784.layout;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReplyActivity extends AppCompatActivity implements View.OnClickListener ,PenDialog.PenDialogListener, LinkModule.RListener{

    private TextView reply_title, reply_name, reply_time, reply_content;
    private FloatingActionButton floatingActionButton;
    private CardData cardData;

    private List<CardData> dataList = new ArrayList<>();
    private ReplyDataAdapter replyDataAdapter;
    private final int space = 20;

    private RoomInfo roomInfo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color
        reply_title = findViewById(R.id.reply_title);
        reply_name = findViewById(R.id.reply_name);
        reply_time = findViewById(R.id.reply_time);
        reply_content = findViewById(R.id.reply_content);

        cardData = getIntent().getParcelableExtra("CardData");
        roomInfo = getIntent().getParcelableExtra("RoomInfo");
        reply_title.setText(cardData.getTitle());

        if(Tabs.mqtt.MapAlias(cardData.getName()) != null){
            reply_name.setText(Tabs.mqtt.MapAlias(cardData.getName()));
        }else {
            reply_name.setText(cardData.getName());
        }

        String time = cardData.getTime();
        String date = time.substring(0,4);
        date = date.concat("年");
        if(time.substring(5,6).equals("0")){
            date = date.concat(time.substring(6,7));
        }else{
            date = date.concat(time.substring(5,7));
        }
        date = date.concat("月");
        if(time.substring(8,9).equals("0")){
            date = date.concat(time.substring(9,10));
        }else{
            date = date.concat(time.substring(8,10));
        }
        date = date.concat("日");
        reply_time.setText(date);

        reply_content.setText(cardData.getContent());

        floatingActionButton = findViewById(R.id.fab_pen);
        floatingActionButton.setOnClickListener(this);

        replyDataAdapter = new ReplyDataAdapter(this, dataList, roomInfo);
        RecyclerView recyclerView = findViewById(R.id.reply_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(replyDataAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));

        LinkModule.getInstance().setRListener(this);
        Tabs.mqtt.getPosterReply(roomInfo.getCode(),cardData.getTitle());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_pen:
//                Toast.makeText(this,roomInfo.getCode(),Toast.LENGTH_SHORT).show();
                openDialog();
                break;
        }
    }

    public void openDialog(){
        PenDialog penDialog = new PenDialog();
        penDialog.show(getSupportFragmentManager(),"Pen Dialog");
    }

    @Override
    public void applyTexts(String content) {
        if(!content.equals("")) {
            Tabs.mqtt.addPoster(roomInfo.getCode(), cardData.getTitle(), content, "reply");
        }
    }

    @Override
    public void fetchPosterReply(String record) {
        StringTokenizer stringTokenizer = new StringTokenizer(record,"\r");
        while (stringTokenizer.hasMoreElements()){
            String token = stringTokenizer.nextToken();
            String token_splitLine[] = token.split("\t");
            updatePost(token_splitLine[1],token_splitLine[2],token_splitLine[3],token_splitLine[0],token_splitLine[4]);
        }
    }

    @Override
    public void updatePosterReply(String code, String theme, String content, String type, String sender, String time) {
        if(cardData.getTitle().equals(theme)) {
            updatePost(theme, content, time, sender, type);
        }
    }

    private void updatePost(String title, String content, String time, String name, String type){
        CardData cardData = new CardData(title,content,time,name,type);
        dataList.add(cardData);
        replyDataAdapter.notifyDataSetChanged();
    }
}
