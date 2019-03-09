package com.example.s2784.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReplyActivity extends AppCompatActivity implements View.OnClickListener ,PenDialog.PenDialogListener{

    private TextView reply_title, reply_name, reply_time, reply_content;
    private FloatingActionButton floatingActionButton;
    private CardData cardData;

    private List<ReplyData> dataList = new ArrayList<>();
    private ReplyDataAdapter replyDataAdapter;
    private final int space = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        reply_title = findViewById(R.id.reply_title);
        reply_name = findViewById(R.id.reply_name);
        reply_time = findViewById(R.id.reply_time);
        reply_content = findViewById(R.id.reply_content);

        cardData = getIntent().getParcelableExtra("CardData");
        reply_title.setText(cardData.getTitle());
        reply_name.setText(cardData.getName());
        reply_time.setText(cardData.getTime());
        reply_content.setText(cardData.getContent());

        floatingActionButton = findViewById(R.id.fab_pen);
        floatingActionButton.setOnClickListener(this);

        replyDataAdapter = new ReplyDataAdapter(this,dataList);
        RecyclerView recyclerView = findViewById(R.id.reply_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(replyDataAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_pen:
//                Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        String dateFormat = "yyyy年MM月dd日";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String today = simpleDateFormat.format(calendar.getTime());
        String date = today.substring(0,5);
        if(today.substring(5,6).equals("0")){
            date = date.concat(today.substring(6,7));
        }else{
            date = date.concat(today.substring(5,7));
        }
        date = date.concat("月");
        if(today.substring(8,9).equals("0")){
            date = date.concat(today.substring(9,10));
        }else{
            date = date.concat(today.substring(8,10));
        }
        date = date.concat("日");

        ReplyData replyData = new ReplyData("SAM",date,content);
        dataList.add(replyData);
        replyDataAdapter.notifyDataSetChanged();
    }
}
