package com.example.s2784.layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Browser;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Tabs extends AppCompatActivity implements Tab1.OnFragmentInteractionListener,Tab2.OnFragmentInteractionListener,Tab3.OnFragmentInteractionListener,Tab4.OnFragmentInteractionListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;
    private final static int CAMERA_RESULT = 0;

    public static ArrayList<RoomInfo> friendList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        //For toolbar

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);


        // For toolbar



        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friend).setText("個人頁面"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_inactive).setText("聊天"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.news_inactive).setText("公佈欄"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_inactive).setText("其他"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabs_pic_inactive),getResources().getColor(R.color.white));

        viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        final TextView mainTitle = findViewById(R.id.mainTitle);
        mainTitle.setText("好友&群組");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                              @Override
                                              public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                              }

                                              @Override
                                              public void onPageSelected(int position) {
                                                  switch (position){
                                                      case 0:
                                                         tabLayout.getTabAt(0).setIcon(R.drawable.friend);
                                                          tabLayout.getTabAt(1).setIcon(R.drawable.chat_inactive);
                                                          tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                                                          tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                                                          mainTitle.setText("好友&群組");
                                                          break;
                                                      case 1:
                                                          tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                                                          tabLayout.getTabAt(1).setIcon(R.drawable.chat);
                                                          tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                                                          tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                                                          mainTitle.setText("聊天室");
                                                          break;
                                                      case 2:
                                                          tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                                                          tabLayout.getTabAt(1).setIcon(R.drawable.chat_inactive);
                                                          tabLayout.getTabAt(2).setIcon(R.drawable.news);
                                                          tabLayout.getTabAt(3).setIcon(R.drawable.setting_inactive);
                                                          mainTitle.setText("公佈欄");
                                                          break;
                                                      case 3:
                                                          tabLayout.getTabAt(0).setIcon(R.drawable.friend_inactive);
                                                          tabLayout.getTabAt(1).setIcon(R.drawable.chat_inactive);
                                                          tabLayout.getTabAt(2).setIcon(R.drawable.news_inactive);
                                                          tabLayout.getTabAt(3).setIcon(R.drawable.setting);
                                                          mainTitle.setText("其他");
                                                          break;
                                                  }
                                              }

                                              @Override
                                              public void onPageScrollStateChanged(int state) {

                                              }
                                          });

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        //實作Toolbar的動態更新
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });


    }
    public void onFragmentInteraction(Uri uri) {

    }    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.setting: //點了settings
                Log.d("item","click settings~~~~");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private android.support.v7.widget.Toolbar.OnMenuItemClickListener onMenuItemClick = new android.support.v7.widget.Toolbar.OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.setting:
                    msg += "設定";
                    break;
                case R.id.searchview:
                    msg += "搜尋";
                    break;
                case R.id.build_group:
                    msg += "創建群組";
                 //   friendList = listHash.get(listDataHeader.get(1));
//                    Intent intent_buildGroup = new Intent(Main.this,BuildGroup.class);
//                    startActivityForResult(intent_buildGroup,REQUEST_CODE_BuildGroup);
                    break;
                case R.id.add_friend:
                    msg += "加入好友";
                   Intent intent_addFriend = new Intent(Tabs.this,AddFriend.class);
                   startActivityForResult(intent_addFriend,REQUEST_CODE);
                    break;
                case R.id.log_out:
                    msg += "登出";
                    Leave();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(Tabs.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (viewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.searchview).setVisible(true);
                menu.findItem(R.id.build_group).setVisible(true);
                menu.findItem(R.id.add_friend).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.searchview).setVisible(true);
                menu.findItem(R.id.build_group).setVisible(true);
                menu.findItem(R.id.add_friend).setVisible(true);
                break;
            case 2:
                menu.findItem(R.id.searchview).setVisible(false);
                menu.findItem(R.id.build_group).setVisible(false);
                menu.findItem(R.id.add_friend).setVisible(false);
                break;
            case 3:
                menu.findItem(R.id.searchview).setVisible(false);
                menu.findItem(R.id.build_group).setVisible(false);
                menu.findItem(R.id.add_friend).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }
    private void Leave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("是否要登出?")
                .setPositiveButton("否", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StartInterface.logout();
                        Intent intent_Login = new Intent(Tabs.this,LogIn.class);
                        startActivity(intent_Login);
                        finish();
                    }
                });
        AlertDialog about_dialog = builder.create();
        about_dialog.show();
    }
}
