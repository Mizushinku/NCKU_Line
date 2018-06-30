package com.example.s2784.layout;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 儲存好友或群組的資訊 ... 2018/6/30
 */

public class RoomInfo implements Serializable{

    private String roomName;
    private String code;
    private String StudentID;
    private Bitmap icon;



    public RoomInfo() {}


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
