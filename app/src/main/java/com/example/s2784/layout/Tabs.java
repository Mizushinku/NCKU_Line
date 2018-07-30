package com.example.s2784.layout;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class Tabs extends AppCompatActivity implements Tab1.OnFragmentInteractionListener,Tab2.OnFragmentInteractionListener,Tab3.OnFragmentInteractionListener,Tab4.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        //For toolbar

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // For toolbar
        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friend));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_inactive));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.news_inactive));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_inactive));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
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

            case R.id.action_settings: //點了settings
                Log.d("item","click settings");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(Tabs.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

}
