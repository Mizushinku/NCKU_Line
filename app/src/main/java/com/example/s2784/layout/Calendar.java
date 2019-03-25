package com.example.s2784.layout;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Calendar extends AppCompatActivity implements View.OnClickListener,CalendarPlusDialog.PlusDialogListener,LinkModule.PListener{

    private List<CardData> dataList = new ArrayList<>();
    private CardDataAdapter cardDataAdapter;
    protected android.support.v7.widget.Toolbar toolbar;

    private final int space = 20;

    private FloatingActionButton floatingActionButton;

    private RoomInfo roomInfo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color


//        Intent intent = getIntent();
//        roomInfo = intent.getParcelableExtra("roomInfo");

        floatingActionButton = findViewById(R.id.calendar_fab_plus);
        floatingActionButton.setOnClickListener(this);

        cardDataAdapter = new CardDataAdapter(this,dataList,roomInfo);
        toolbar = findViewById(R.id.calendar_toolbar);
        toolbar.setTitle("行事曆");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        RecyclerView recyclerView = findViewById(R.id.calendar_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(cardDataAdapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(space));

        LinkModule.getInstance().setPListener(this);
//        Tabs.mqtt.getPoster(roomInfo.getCode());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.calendar_fab_plus:
                Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
                openDialog();
                break;
        }
    }

    public void openDialog(){
        CalendarPlusDialog calendarplusDialog = new CalendarPlusDialog();
        calendarplusDialog.show(getSupportFragmentManager(),"Calendar Plus Dialog");
    }


//    @Override
//    public void applyTexts(String title, String content) {
//        if(!title.equals("") && !content.equals("")) {
//            Tabs.mqtt.addPoster(roomInfo.getCode(), title, content, "post");
//        }

//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void fetchPoster(String record) {

    }

    @Override
    public void updatePoster(String code, String theme, String content, String type, String sender, String time) {

    }

    @Override
    public void applyTexts(String title, String content) {

    }
}
