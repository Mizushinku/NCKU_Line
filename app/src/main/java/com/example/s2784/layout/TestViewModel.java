package com.example.s2784.layout;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TestViewModel extends ViewModel {
    private final ArrayList<RoomInfo> group = new ArrayList<>();
    private final ArrayList<RoomInfo> friend = new ArrayList<>();
    private final ArrayList<RoomInfo> course = new ArrayList<>();
    private final ArrayList<RoomInfo> roomlist = new ArrayList<>();
    private final HashMap<String,ArrayList<RoomInfo>> listHash = new HashMap<>();
    private String userID;
    private String userName = "USER";
    private Bitmap userIcon;
//    private boolean dataChange = false;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public Bitmap getUserIcon() { return userIcon; }

    public void setUserIcon(Bitmap userIcon) { this.userIcon = userIcon; }

    public void removeFromGroup(int pos){
        group.remove(pos);
    }

    public void removeFromFriend(int pos){
        friend.remove(pos);
    }

    public void addInGroup(RoomInfo roomInfo){
        group.add(roomInfo);
    }

    public void addInFriend(RoomInfo roomInfo){
        friend.add(roomInfo);
    }

    public ArrayList<RoomInfo> getGroup() {
        return group;
    }

    public ArrayList<RoomInfo> getFriend() {
        return friend;
    }

    public ArrayList<RoomInfo> getCourse() { return course; }

    public ArrayList<RoomInfo> getRoomList() {  roomlist.clear();
                                                roomlist.addAll(friend);
                                                roomlist.addAll(group);
                                                roomlist.addAll(course);
                                                Collections.sort(roomlist);
                                                return roomlist; }

    public HashMap<String, ArrayList<RoomInfo>> getListHash() {
        return listHash;
    }

    public void putListHash(String string,ArrayList<RoomInfo> list){
        listHash.put(string,list);
    }

    public RoomInfo getRoomInfo(String code){
        RoomInfo roomInfo = new RoomInfo();
        for(int i=0;i<friend.size();i++){
            roomInfo = friend.get(i);
            if(code.equals(roomInfo.getCode())){
                return roomInfo;
            }
        }
        for (int i=0;i<group.size();i++) {
            roomInfo = group.get(i);
            if(code.equals(roomInfo.getCode())){
                return roomInfo;
            }
        }
        for (int i=0;i<course.size();i++) {
            roomInfo = course.get(i);
            if(code.equals(roomInfo.getCode())){
                return roomInfo;
            }
        }
        return roomInfo;
    }
}
