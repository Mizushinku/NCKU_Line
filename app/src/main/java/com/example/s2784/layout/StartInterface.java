package com.example.s2784.layout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class StartInterface extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_interface);

        // For Badge's table and Login's table
        SQLiteManager.setContext(this);
        SQLiteManager.DBinit();
        SQLiteManager.createTableForBadge();
        SQLiteManager.createTableForLogin();

        if(SQLiteManager.queryForLogin()){//若已LogIn過，則需跳頁至Tabs.java
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String user = SQLiteManager.getUserID();
                    Intent mainIntent = new Intent(StartInterface.this, Tabs.class);
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
