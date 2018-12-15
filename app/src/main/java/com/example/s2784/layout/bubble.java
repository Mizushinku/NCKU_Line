package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by uscclab on 2018/5/21.
 * 儲存一則聊天訊息
 * type = 0 為他人, type = 1 為自己
 * sender 待補 ... 2018/6/30
 */

public class bubble {
    private String txtmsg;
    private int type;
    private String name;
    private Bitmap pic;
    private String time;
    private String date;
    private Bitmap image;
    public bubble() {}

    //constructor for text message
    public bubble(int type, String txtmsg, String name, String time, Bitmap bitmap) {
        this.txtmsg = txtmsg;
        this.type = type;
        this.name = name;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
        this.pic = bitmap;
    }
    public bubble(int type, String txtmsg, String name, String time) {
        this.txtmsg = txtmsg;
        this.type = type;
        this.name = name;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
    }

    //constructor foe image message
    public bubble(int type, Bitmap image, String name, String time, Bitmap bitmap) {
        this.image = image;
        this.type = type;
        this.name = name;
        String[] tokens = time.split(" ");
        this.date = tokens[0];
        this.time = tokens[1].substring(0,5);
        this.pic = bitmap;
    }
    public bubble(int type, Bitmap image, String name, String time) {
        this.image = image;
        this.type = type;
        this.name = name;
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
}
