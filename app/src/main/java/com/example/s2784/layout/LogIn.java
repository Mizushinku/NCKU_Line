package com.example.s2784.layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Browser;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogIn extends AppCompatActivity {
    private final static int CAMERA_RESULT = 0;
    private Mqtt_Client mqtt;
    //SQLite
    //public static boolean LoginOrNot = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);




        Button btn_logIn = findViewById(R.id.btn_logIn);
                btn_logIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callCamera();
                        /*
                        Intent mainintent = new Intent(LogIn.this,Main.class);
                        startActivity(mainintent);
                        finish();
                        */
                    }
                });

        //StartInterface.LoginOrNot = true;

//        StartInterface.db = openOrCreateDatabase(StartInterface.db_name, Context.MODE_PRIVATE, null);
//        String createTable = "CREATE TABLE IF NOT EXISTS " + StartInterface.tb_name + "name VARCHAR(32), " + "id VARCHAR(9))";

//        Cursor c = StartInterface.db.rawQuery("SELECT * FROM "+StartInterface.tb_name,null);
//        if(c.getCount()==0)
//        {
//            StartInterface.addData("LogIn","22222222");
//        }
//        StartInterface.db.close();

        mqtt = new Mqtt_Client(this.getApplicationContext(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        mqtt.Connect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqtt.disconnect();
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
                        Toast.makeText(LogIn.this,"條碼傳送成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(LogIn.this,"條碼傳送失敗",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(LogIn.this,"建議下載 chrome，可避免產生多餘分頁",Toast.LENGTH_SHORT).show();
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

                // send to server check id exists or not
                Toast.makeText(this, "學號:" + result.getContents(), Toast.LENGTH_SHORT).show();
                mqtt.Login(result.getContents());

                // Go student Data page
//                Intent studentDataIntent = new Intent(LogIn.this,StudentData.class);
//                studentDataIntent.putExtra("studentID",result.getContents());
//                startActivity(studentDataIntent);


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

    public class Mqtt_Client{
        private static final String MQTT_HOST = "tcp://140.116.82.52:1883";
        private MqttAndroidClient client;
        private MqttConnectOptions options;

        private Context context;
        private String user;

        public Mqtt_Client(Context context, String user) {
            this.context = context;
            this.user = user;
        }

        public void Connect(){
            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(context, MQTT_HOST, clientId);

            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        mqttSub();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if(reconnect){
                        mqttSub();
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String[] idf = topic.split("/");
                    switch (idf[1]){
                        case "Login":
                            String msg[] = new String(message.getPayload()).split(",");
                            if(msg[0].equals("True")){
                                // Go Main page
                                Intent mainIntent = new Intent(LogIn.this,Main.class);
                                mainIntent.putExtra("userID",msg[1]);
                                startActivity(mainIntent);
                                StartInterface.addData("Login",msg[1]);
                                finish();
                            }else if(msg[0].equals("False")){
                                callCamera();
                            }
                            break;
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }
        public void disconnect() {
            if(client != null && client.isConnected()) {
                try {
                    client.unsubscribe("IDF/Login/" + user + "/Re");
                    client.disconnect();
                    client.unregisterResources();
                    client = null;
                    Log.d("TAG","Try Disconnect");
                } catch (MqttException e) {
                    Log.d("TAG","Disconnect Error");
                    e.printStackTrace();
                }
            }
        }

        private void mqttSub() {
            try {
                String topic = "IDF/Login/" + user + "/Re";
                client.subscribe(topic,2);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void Login(String id){
            String topic = "IDF/Login/" + user;
            String MSG = id;
            try {
                client.publish(topic,MSG.getBytes(),0,false);
            }catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
