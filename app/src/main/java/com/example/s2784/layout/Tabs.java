package com.example.s2784.layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Browser;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tabs extends AppCompatActivity implements Tab1.OnFragmentInteractionListener,Tab2.OnFragmentInteractionListener,Tab3.OnFragmentInteractionListener,Tab4.OnFragmentInteractionListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_BuildGroup = 2;
    private static final int REQUEST_CODE_MsgBulletin = 3;
    private static final int REQUEST_CODE_JoinGroup = 4;
    private final static int CAMERA_RESULT = 0;


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
                    Intent intent_joinGroup = new Intent(Tabs.this,JoinGroup.class);
                    startActivityForResult(intent_joinGroup,REQUEST_CODE_JoinGroup);
                    break;
                case R.id.add_friend:
                    msg += "加入好友";
                    callCamera();
                    break;
                case R.id.log_out:
                    msg += "登出";

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
    public void callCamera(){
        String[] permissionNeed = {
                Manifest.permission.CAMERA,
        };
        if( hasPermission(permissionNeed)){
            Scanner();
        }else {
            getPermission();
        }
    }

    @TargetApi(23)
    public void getPermission(){
        if(Build.VERSION.SDK_INT>=23) {
            String[] permissionNeed = {
                    Manifest.permission.CAMERA,
            };
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "需要相機權限掃描條碼", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(permissionNeed, CAMERA_RESULT);
        }
    }

    public void Scanner(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("請對準條碼");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }
    private boolean hasPermission(String[] permission) {
        if (canMakeSmores()) {
            for (String permissions : permission) {
                return (ContextCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults ){
        switch (requestCode){
            case CAMERA_RESULT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Scanner();
                } else {
                    Toast.makeText(this,"需要相機權限掃描條碼",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }}
    private boolean canMakeSmores() {
        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void sendBarcode_returnWebPage(String code) {
        // 取得網頁送過來的資料
        Intent tIntent = this.getIntent();

        // 取得 URL 的 URI
        Uri myURI = tIntent.getData();

        // 取得呼叫網頁網址
        final String call_url = myURI.getQueryParameter("callurl");

        // 設定資料庫網址及條碼
        final String sendcode_url = myURI.getQueryParameter("returnurl") + "&code=" + code;

        // ---- 傳送條碼至資料庫 ----- //
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(sendcode_url);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setReadTimeout(0);
                    connection.setConnectTimeout(0);
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        Looper.prepare();
                        Toast.makeText(Tabs.this,"條碼傳送成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(Tabs.this,"條碼傳送失敗",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        // delay 200 milisecond for finishing sending barcode
        try{

            Thread.sleep(200);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        // ---- 返回呼叫網頁 ---- //
        Intent intent_main = new Intent(Intent.ACTION_VIEW);

        // 指定使用 chrome 開啟
        intent_main.setPackage("com.android.chrome");
        intent_main.setData(Uri.parse(call_url));

        // 放入 EXTRA_APPLICATION_ID ，以重複使用同一分頁
        intent_main.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome");

        // 開啟網頁
        try{
            startActivity(intent_main);
        }catch (ActivityNotFoundException ex){
            // 手機沒有 chrome 的時候，改用 default browser 開啟 => package 設為 null
            intent_main.setPackage(null);
            startActivity(intent_main);
            Toast.makeText(Tabs.this,"建議下載 chrome，可避免產生多餘分頁",Toast.LENGTH_SHORT).show();
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                this.finish();
            } else {
                Log.d("MainActivity", "Scanned");
                // Toast.makeText(this, "掃描結果: " + result.getContents(), Toast.LENGTH_LONG ).show();   // 顯示條碼

                Intent intent = getIntent();
                intent.putExtra("StudentID",result.getContents());
                //Main.arrayList.add(result.getContents());
                setResult(REQUEST_CODE, intent);

                this.finish();

                if (this.getIntent().getDataString() != null) {
                    //this.getIntent().getDataString() : usccbarcodescanner://?callurl=http://mmm.lifeacademy.org/erpweb/testbarcodeapp&returnurl=http://mmm.lifeacademy.org/erpweb/Scancode/PutScanCode?username=

                    sendBarcode_returnWebPage(result.getContents());
                    this.finish();
                }
                else {
                    //Toast.makeText(this, "請從網頁開啟本程式", Toast.LENGTH_LONG).show();
                    this.finish();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
