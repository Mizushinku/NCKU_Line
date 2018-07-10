package com.example.s2784.layout;

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
    private ImageView pic;
    public bubble() {}

    public bubble(int type, String txtmsg,String name) {
        this.txtmsg = txtmsg;
        this.type = type;
        this.name = name;
    }

    public String getTxtMsg() {
        return txtmsg;
    }

    public void setTxtMsg(String txtmsg) {
        this.txtmsg = txtmsg;
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

    public ImageView getPic() {
        return pic;
    }

    public void setPic(ImageView pic) {
        this.pic = pic;
    }
}
