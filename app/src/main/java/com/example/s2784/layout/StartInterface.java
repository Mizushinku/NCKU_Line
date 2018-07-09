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
import android.widget.Toast;


public class StartInterface extends AppCompatActivity {

    public static boolean LoginOrNot = false;
    //public static SQLiteDatabase db = new SQLiteDatabase();
    public static SQLiteDatabase db;
    static final String db_name = "test_db";
    static final String tb_name="test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_interface);



//
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name + "(name VARCHAR(32), " + "id VARCHAR(9))";
        db.execSQL(createTable);

        Cursor c = db.rawQuery("SELECT name FROM "+tb_name,null);
//        if(c.getCount()==0)
//        {
//           addData("test","22222222");
//        }
//        if(c.moveToFirst()){
//            String str = null;
//            str+=c.getString(0);
//            if(str.equals("LogIn"))
//            {
//                LoginOrNot = true;
//            }
//        }

        if(c.moveToFirst()){
                String str = c.getString(0);;
                if(str.equals("Login"))
                {
                    LoginOrNot = true;
                }
        }


        //db.close();

        if (LoginOrNot == false) //若未LogIn過，則需跳頁至LogIn.java
        {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginIntent = new Intent(StartInterface.this, LogIn.class);
                    StartInterface.this.startActivity(loginIntent);
                    StartInterface.this.finish();
                }
            }, 3000);
        }

        else if (LoginOrNot == true) //若已LogIn過，則需跳頁至Main.java
        {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Cursor cc = db.rawQuery("SELECT id FROM " + tb_name + " LIMIT 1",null);
                    cc.moveToFirst();
                    String user = cc.getString(0);
                    //Toast.makeText(StartInterface.this,"user = " + user, Toast.LENGTH_LONG).show();

                    Intent mainIntent = new Intent(StartInterface.this, Main.class);
                    mainIntent.putExtra("userID",user);
                    StartInterface.this.startActivity(mainIntent);
                    StartInterface.this.finish();
                }
            }, 1000);
        }

    }
    public static void addData(String name,String id)
    {
        ContentValues cv=new ContentValues(2);
        cv.put("name",name);
        cv.put("id",id);
        db.insert(tb_name,null,cv);
    }

    public static void logout() {
        db.delete(tb_name,null,null);
        db.close();
        LoginOrNot = false;
    }
}
