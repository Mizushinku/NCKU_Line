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
    private String friendName;
    private String type;
    private Bitmap icon;
    private byte[] icon_data;


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

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getFriendName() { return friendName; }

    public void setFriendName(String friendName) { this.friendName = friendName; }

    public byte[] getIcon_data() { return icon_data; }

    public void setIcon_data(byte[] icon_data) { this.icon_data = icon_data; }
}
