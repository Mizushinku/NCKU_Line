package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 儲存好友或群組的資訊 ... 2018/6/30
 */

public class RoomInfo implements Serializable, Parcelable{

    private String roomName;
    private String code;
    private String StudentID;
    private String friendName;
    private String type;
    private byte[] icon_data;


    public RoomInfo() {}
    public RoomInfo(Parcel in){
        roomName = in.readString();
        code = in.readString();
        StudentID = in.readString();
        friendName = in.readString();
        type = in.readString();
        icon_data = in.createByteArray();
    }

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
        dest.writeString(code);
        dest.writeString(StudentID);
        dest.writeString(friendName);
        dest.writeString(type);
        dest.writeByteArray(icon_data);
    }

    public static Creator<RoomInfo> CREATOR = new Creator<RoomInfo>(){
        @Override
        public RoomInfo createFromParcel(Parcel source) {
            return new RoomInfo(source);
        }

        @Override
        public RoomInfo[] newArray(int size) {
            return new RoomInfo[size];
        }
    };
}
