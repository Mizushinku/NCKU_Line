package com.example.s2784.layout;

/**
 * Created by uscclab on 2018/5/21.
 * 儲存一則聊天訊息
 * type = 0 為他人, type = 1 為自己
 * sender 待補 ... 2018/6/30
 */

public class bubble {
    private String txtmsg;
    private int type;

    public bubble() {}

    public bubble(int type, String txtmsg) {
        this.txtmsg = txtmsg;
        this.type = type;
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
}
