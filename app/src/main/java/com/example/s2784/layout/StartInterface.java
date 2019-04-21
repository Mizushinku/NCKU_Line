package com.example.s2784.layout;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class StartInterface extends AppCompatActivity {
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_interface);

        // For Badge's table and Login's table
        SQLiteManager.setContext(this);
        SQLiteManager.DBinit();
        SQLiteManager.createTableForBadge();
        SQLiteManager.createTableForLogin();
        SQLiteManager.createTableForIntro();
        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        else{
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color
        if(SQLiteManager.queryForLogin()){//若已LogIn過，則需跳頁至Tabs.java
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String user = SQLiteManager.getUserID();
                    Intent mainIntent = new Intent(StartInterface.this, Tabs.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainIntent.putExtra("userID",user);
                    StartInterface.this.startActivity(mainIntent);
                    StartInterface.this.finish();
                }
            }, 1000);
        }else{//若未LogIn過，則需跳頁至LogIn.java
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginIntent = new Intent(StartInterface.this, LogIn.class);
                    StartInterface.this.startActivity(loginIntent);
                    StartInterface.this.finish();
                }
            }, 3000);
        }

    }
}
