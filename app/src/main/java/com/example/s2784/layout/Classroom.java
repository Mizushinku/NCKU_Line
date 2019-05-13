package com.example.s2784.layout;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Classroom extends Chatroom implements OnMenuItemClickListener {

    private static final int choosePic = 0;
    private static final int discuss = 1;
    private static final int changeAuth0 = 2;
    private static final int changeAuth1 = 3;
    private static final int changeAuth2 = 4;
    private static final int member = 5;
    private static final int document = 6;
    private static final int album = 7;
    private static final int schedule = 8;
    private static final int annoouncement = 9;
    private static final int assignTA = 10;

    private String A0_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆","公告","指派助教"};
    private String A1_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆","公告"};
    private String A2_course_letters[] = {"選擇圖片","討論區","修改權限0","修改權限1","修改權限2","成員名單","課程文件","相簿","行事曆",};
    private int A0_course_icons[] = {R.drawable.pic,R.drawable.discuss,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar,R.drawable.announcement,R.drawable.assign_ta};
    private int A1_course_icons[] = {R.drawable.pic,R.drawable.discuss,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar,R.drawable.announcement,};
    private int A2_course_icons[] = {R.drawable.pic,R.drawable.discuss,R.drawable.auth0,R.drawable.auth1,R.drawable.auth2,R.drawable.group_member,R.drawable.document,R.drawable.album,R.drawable.calendar};

    private int auth = -1;

    private ContextMenuDialogFragment contextMenuDialogFragment;
    private ArrayList<MenuObject> menuObjects;

    //onCreate -> startCreate -> setAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startCreate() {
        super.startCreate();
        Tabs.mqtt.getAuth(code);
    }

    @Override
    public void setAuth(int auth) {
        this.auth = auth;
        set_gridAdapter();
        set_gridview_onItemClickListener();
        if(auth == 0) {
            Toast.makeText(this, "auth 0", Toast.LENGTH_SHORT).show();
            setContextMenu();
        }
        else if(auth == 1) {
            Toast.makeText(this, "auth 1", Toast.LENGTH_SHORT).show();
        }
        else if(auth == 2) {
            Toast.makeText(this, "auth 2", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void set_gridAdapter() {
        if(auth < 0) return;
        if(roomInfo.getType().equals("C")){
            if(auth == 0) {
                gridAdapter = new Grid_Adapter(this, A0_course_icons, A0_course_letters);
            } else if(auth == 1) {
                gridAdapter = new Grid_Adapter(this, A1_course_icons, A1_course_letters);
            } else if(auth == 2) {
                gridAdapter = new Grid_Adapter(this, A2_course_icons, A2_course_letters);
            }
        }
        gridView.setAdapter(gridAdapter);
    }

    @Override
    protected void set_gridview_onItemClickListener() {
        if(auth < 0) return;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(roomInfo.getType().equals("C")) {
                    switch (position) {
                        case choosePic:
                            choosePic();
                            break;
                        case discuss:
                            Intent discuss = new Intent(getApplicationContext(),DiscussActivity.class);
                            discuss.putExtra("roomInfo", roomInfo);
                            startActivity(discuss);
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case schedule:
                            Intent calendar = new Intent(getApplicationContext(),Calendar.class);
//                            discuss.putExtra("roomInfo", roomInfo);
                            startActivity(calendar);

//                            DatePickerFragment fragment = new DatePickerFragment();
//                            fragment.show(getSupportFragmentManager(), "DatePickerFragment");
//                            TimePickerFragment timePickerFragment = new TimePickerFragment();
//                            timePickerFragment.show(getSupportFragmentManager(),"TimePickerFragment");
                            break;
                        case annoouncement:
                            if(getSupportFragmentManager().findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                                contextMenuDialogFragment.show(getSupportFragmentManager(), ContextMenuDialogFragment.TAG);
                            }
                            break;
                        default:
                            Toast.makeText(Classroom.this, "Wrong", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    private void setContextMenu() {
        menuObjects = new ArrayList<>();
        menuObjects.add(newMO(R.string.assignment, R.drawable.assignment));
        menuObjects.add(newMO(R.string.exam, R.drawable.exam));
        menuObjects.add(newMO(R.string.vote, R.drawable.vote));
        menuObjects.add(newMO(R.string.cancel, R.drawable.cancel));

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize(250);
        menuParams.setMenuObjects(menuObjects);
        menuParams.setClosableOutside(true);
        menuParams.setFitsSystemWindow(true);
        menuParams.setClipToPadding(false);

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        contextMenuDialogFragment.setItemClickListener(this);
    }

    private MenuObject newMO(int titleID, int drawableID) {
        MenuObject menuObject = new MenuObject(getResources().getString(titleID));
        menuObject.setResource(drawableID);
        return menuObject;
    }

    @Override
    public  void onMenuItemClick(View v, int p) {
        Toast.makeText(this, "Clicked on : " + menuObjects.get(p).getTitle(), Toast.LENGTH_SHORT).show();
        if(!menuObjects.get(p).getTitle().equals(getResources().getString(R.string.cancel))) {
            String text = String.format("From : %s,\nAnnouncement : %s",
                    roomInfo.getRoomName(), menuObjects.get(p).getTitle());
            Tabs.annocViewModel.add_annoc(text);
        }
    }

    private void changeAuth(int auth){
        this.auth  = auth;
    }

}
