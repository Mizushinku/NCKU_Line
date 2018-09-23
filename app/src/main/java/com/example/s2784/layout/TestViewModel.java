package com.example.s2784.layout;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TestViewModel extends ViewModel {
    private final ArrayList<RoomInfo> group = new ArrayList<>();
    private final ArrayList<RoomInfo> friend = new ArrayList<>();
    private final ArrayList<RoomInfo> roomlist = new ArrayList<>();
    private final HashMap<String,ArrayList<RoomInfo>> listHash = new HashMap<>();
    private String userID;
    private boolean dataChange = false;

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

    public ArrayList<RoomInfo> getRoomList() {  roomlist.clear();
                                                roomlist.addAll(friend);
                                                roomlist.addAll(group);
                                                Collections.sort(roomlist);
                                                return roomlist; }

    public HashMap<String, ArrayList<RoomInfo>> getListHash() {
        return listHash;
    }

    public void putListHash(String string,ArrayList<RoomInfo> list){
        listHash.put(string,list);
    }

    public boolean isDataChange() { return dataChange; }

    public void setDataChange(boolean dataChange) { this.dataChange = dataChange; }
}
