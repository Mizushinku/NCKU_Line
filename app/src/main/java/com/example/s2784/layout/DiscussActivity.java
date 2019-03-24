package com.example.s2784.layout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DiscussActivity extends AppCompatActivity implements View.OnClickListener, PlusDialog.PlusDialogListener ,LinkModule.PListener{

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
        setContentView(R.layout.activity_discuss);

        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color

        Intent intent = getIntent();
        roomInfo = intent.getParcelableExtra("roomInfo");

        floatingActionButton = findViewById(R.id.fab_plus);
        floatingActionButton.setOnClickListener(this);

        cardDataAdapter = new CardDataAdapter(this,dataList,roomInfo);
        toolbar = findViewById(R.id.discuss_toolbar);
        toolbar.setTitle("討論區");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
        if(!title.equals("") && !content.equals("")) {
            Tabs.mqtt.addPoster(roomInfo.getCode(), title, content, "post");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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
//        Log.d("POST",theme + content + time + sender + type);
    }

    private void updatePost(String title, String content, String time, String name, String type){
        CardData cardData = new CardData(title,content,time,name,type);
        dataList.add(cardData);
        cardDataAdapter.notifyDataSetChanged();
    }
}
