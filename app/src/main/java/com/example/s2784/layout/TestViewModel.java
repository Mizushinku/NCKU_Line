package com.example.s2784.layout;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class TestViewModel extends ViewModel {
    private final ArrayList<RoomInfo> group = new ArrayList<>();
    private final ArrayList<RoomInfo> friend = new ArrayList<>();
    private final HashMap<String,ArrayList<RoomInfo>> listHash = new HashMap<>();
    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

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

    public ArrayList<RoomInfo> getRoomList() {  ArrayList<RoomInfo> temp = new ArrayList<>();
                                                temp.addAll(friend);
                                                temp.addAll(group);
                                                return temp;}

    public HashMap<String, ArrayList<RoomInfo>> getListHash() {
        return listHash;
    }

    public void putListHash(String string,ArrayList<RoomInfo> list){
        listHash.put(string,list);
    }
}
