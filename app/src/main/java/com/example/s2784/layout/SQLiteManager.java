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

    static public void queryForBadge(String code){
        Cursor c = DB.rawQuery("SELECT " + "*" + " FROM " + Badge_table_name + " WHERE " + "Room = " + "'" + code + "'", null);
        if(c.getCount() == 0){
            insertFirst(code);
        }
        badgePlus(code);
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
}
