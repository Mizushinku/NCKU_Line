package com.example.s2784.layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import me.leolin.shortcutbadger.ShortcutBadger;

public class SQLiteManager {
    static private SQLiteDatabase DB;
    static private String DBNAME = "CHENG_LINE";
    static private String Badge_table_name = "BADGE";
    static private String Login_table_name = "LOGIN";
    static private Context ctx;

    static public void setContext(Context context){ ctx = context; }

    static public void DBinit(){
        DB = ctx.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
    }

    static public void createTableForBadge(){
        String instruction = "CREATE TABLE IF NOT EXISTS " +  Badge_table_name + "(" + "Room VARCHAR(40)" +
                "," + "Count int(10)" + ")";
        DB.execSQL(instruction);
    }

    static public void createTableForLogin(){
        String instruction = "CREATE TABLE IF NOT EXISTS " + Login_table_name + "(id VARCHAR(9))";
        DB.execSQL(instruction);
    }

    static public void queryForBadge(String code){
        Cursor c = DB.rawQuery("SELECT " + "*" + " FROM " + Badge_table_name + " WHERE " + "Room = " + "'" + code + "'", null);
        if(c.getCount() == 0){
            insertFirst(code);
        }
        badgePlus(code);
    }

    static public boolean queryForLogin(){
        Cursor c = DB.rawQuery("SELECT id FROM " + Login_table_name,null);
        if(c.getCount() == 0){
            return false;
        }else {
            return true;
        }
    }

    static public String getUserID(){
        Cursor c = DB.rawQuery("SELECT id FROM " + Login_table_name + " LIMIT 1",null);
        c.moveToFirst();
        return c.getString(0);
    }

    static public void addUser(String id){
        ContentValues cv = new ContentValues(1);
        cv.put("id",id);
        DB.insert(Login_table_name,null,cv);
    }

    static public void insertFirst(String code){
        ContentValues cv = new ContentValues(2);
        cv.put("Room",code);
        cv.put("count",0);
        DB.insert(Badge_table_name,null,cv);
    }

    static public void badgePlus(String code){
        String instruction = "UPDATE " + Badge_table_name + " SET " + "Count = Count + 1" + " WHERE " +
                "Room = " + "'" + code + "'";
        DB.execSQL(instruction);
        upDateIcon();
    }

    static public void badgeClear(String code){
        String instruction = "UPDATE " + Badge_table_name + " SET " + "Count = 0" + " WHERE " +
                "Room = " + "'" + code + "'";
        DB.execSQL(instruction);
        upDateIcon();
    }

    static public void upDateIcon(){
        int num = 0;
        Cursor c = DB.rawQuery("SELECT " + "Count" + " FROM " + Badge_table_name,null);
        if(c.moveToFirst()){
            for(int i=0;i<c.getCount();i++){
                int cnt = c.getInt(0);
                num += cnt;
                c.moveToNext();
            }
        }
        ShortcutBadger.applyCount(ctx,num);
    }

    static public void deleteAllBadge(){
        String instruction = "DELETE FROM " + Badge_table_name;
        DB.execSQL(instruction);
        upDateIcon();
    }

    static public void deleteAllUser(){
        String instruction = "DELETE FROM " + Login_table_name;
        DB.execSQL(instruction);
    }
}
