package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 儲存好友或群組的資訊 ... 2018/6/30
 */

public class RoomInfo implements Parcelable, Comparable {

    private boolean checked;
    private String roomName;
    private String code;
    private String StudentID;
    private String friendName;
    private String type;
    private String rMsg;
    private String rMsgDate;
    private byte[] icon_data;
    private ArrayList<String> memberID = new ArrayList<>();
    private int unReadNum;


    public RoomInfo() {
    }

    public RoomInfo(Parcel in) {
        checked = in.readByte() != 0;
        roomName = in.readString();
        code = in.readString();
        StudentID = in.readString();
        friendName = in.readString();
        type = in.readString();
        rMsg = in.readString();
        rMsgDate = in.readString();
        icon_data = in.createByteArray();
        memberID = in.createStringArrayList();
        unReadNum = in.readInt();
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getrMsg() {
        return rMsg;
    }

    public void setrMsg(String rMsg) {
        this.rMsg = rMsg;
    }

    public String getrMsgDate() {
        return rMsgDate;
    }

    public void setrMsgDate(String rMsgDate) {
        this.rMsgDate = rMsgDate;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public byte[] getIcon_data() {
        return icon_data;
    }

    public void setIcon_data(byte[] icon_data) {
        this.icon_data = icon_data;
    }

    public ArrayList<String> getMemberID() { return memberID; }

    public void setMemberID(ArrayList<String> memberID) { this.memberID = memberID; }

    public void addMemberID(String id) { this.memberID.add(id); }

    public void removeMemberID(String id) { this.memberID.remove(id); }

    public int getUnReadNum() { return unReadNum; }

    public void setUnReadNum(int unReadNum) { this.unReadNum = unReadNum; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(roomName);
        dest.writeString(code);
        dest.writeString(StudentID);
        dest.writeString(friendName);
        dest.writeString(type);
        dest.writeString(rMsg);
        dest.writeString(rMsgDate);
        dest.writeByteArray(icon_data);
        dest.writeStringList(memberID);
        dest.writeInt(unReadNum);
    }

    public static Creator<RoomInfo> CREATOR = new Creator<RoomInfo>() {
        @Override
        public RoomInfo createFromParcel(Parcel source) {
            return new RoomInfo(source);
        }

        @Override
        public RoomInfo[] newArray(int size) {
            return new RoomInfo[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        RoomInfo roomInfo = (RoomInfo) o;
        if (this.getrMsgDate().equals("XXXX-XX-XX XX:XX")) {
            return 1;
        } else if (roomInfo.getrMsgDate().equals("XXXX-XX-XX XX:XX")) {
            return -1;
        } else {
            String str1 = roomInfo.getrMsgDate();
            String str2 = this.getrMsgDate();
            int y1, y2, M1, M2, d1, d2, H1, H2, m1, m2;
            y1 = Integer.parseInt(str1.substring(0, 4));
            y2 = Integer.parseInt(str2.substring(0, 4));
            M1 = Integer.parseInt(str1.substring(5, 7));
            M2 = Integer.parseInt(str2.substring(5, 7));
            d1 = Integer.parseInt(str1.substring(8, 10));
            d2 = Integer.parseInt(str2.substring(8, 10));
            H1 = Integer.parseInt(str1.substring(11, 13));
            H2 = Integer.parseInt(str2.substring(11, 13));
            m1 = Integer.parseInt(str1.substring(14, 16));
            m2 = Integer.parseInt(str2.substring(14, 16));
            if (y1 > y2) {
                return 1;
            } else if (y1 < y2) {
                return -1;
            } else if (M1 > M2) {
                return 1;
            } else if (M1 < M2) {
                return -1;
            } else if (d1 > d2) {
                return 1;
            } else if (d1 < d2) {
                return -1;
            } else if (H1 > H2) {
                return 1;
            } else if (H1 < H2) {
                return -1;
            } else if (m1 > m2) {
                return 1;
            } else if (m1 < m2) {
                return -1;
            }
        }
        return 0;
    }
}
