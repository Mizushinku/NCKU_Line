package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by uscclab on 2018/5/21.
 * 儲存一則聊天訊息
 * type = 0 為他人, type = 1 為自己
 * sender 待補 ... 2018/6/30
 */

public class Bubble {
    private String txtmsg;
    private String name;
    private Bitmap pic;         // sender icon
    private String time;        // hour,min "XX:XX"
    private String date;        // date "XXXX-XX-XX"
    private Bitmap image;       // msg
    private String full_time;   // full time information from DB

    //message from others or self, 0 = others, 1 = self
    private int type;

    //message type, 0 = text data, 1 = image data
    private int data_t;


    public Bubble() {}

    //constructor for text message
    public Bubble(int type, int data_t, String txtmsg, String name, String time, Bitmap bitmap) {
        this.txtmsg = txtmsg;
        this.data_t = data_t;
        this.type = type;
        this.name = name;
        this.full_time = time;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
        this.pic = bitmap;
    }
    public Bubble(int type, int data_t, String txtmsg, String name, String time) {
        this.txtmsg = txtmsg;
        this.data_t = data_t;
        this.type = type;
        this.name = name;
        this.full_time = time;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
    }

    //constructor foe image message
    public Bubble(int type, int data_t, Bitmap image, String name, String time, Bitmap bitmap) {
        this.image = image;
        this.data_t = data_t;
        this.type = type;
        this.name = name;
        this.full_time = time;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
        this.pic = bitmap;
    }
    public Bubble(int type, int data_t, Bitmap image, String name, String time) {
        this.image = image;
        this.data_t = data_t;
        this.type = type;
        this.name = name;
        this.full_time = time;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
    }


    public String getTxtMsg() {
        return txtmsg;
    }

    public void setTxtMsg(String txtmsg) {
        this.txtmsg = txtmsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) { this.pic = pic; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getData_t() {
        return data_t;
    }

    public void setFull_time(String full_time) { this.full_time = full_time; }

    public String getFull_time() { return full_time; }

}
