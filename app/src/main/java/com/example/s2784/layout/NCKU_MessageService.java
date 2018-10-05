package com.example.s2784.layout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class NCKU_MessageService extends Service {

    private final static String Service_CHANNEL_ID_AddFriend = "1";
    private final static String Service_CHANNEL_NAME_AddFriend = "1";
    private final static String Service_CHANNEL_ID_AddGroup = "2";
    private final static String Service_CHANNEL_NAME_AddGroup = "2";

    private MqttAndroidClient client;
    private MqttConnectOptions options;
    private String user = null;

    public NCKU_MessageService() {
        Log.d("TAG","in Service cont");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TAG","in Service onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        final String MQTT_HOST = "tcp://140.116.82.52:1883";


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTT_HOST, clientId);

        options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        Log.d("TAG","in Service onCreate()");

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startID) {
        user = intent.getStringExtra("userID");

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
                if (reconnect) {
                    mqttSub();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String[] idf = topic.split("/");
                if(idf[0].equals("Service")) {
                    switch (idf[1]) {
                        case "AddFriendNotification" :
                            String friendName = new String(message.getPayload());
                            AddFriendNotification_re(friendName);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        Log.d("TAG","in Service onStartCommand");
        return super.onStartCommand(intent,flag,startID);
    }

    @Override
    public void onDestroy() {
        disconnect();
        Log.d("TAG","in Service onDestroy()");
    }



    private void mqttSub() {
        try {
            String topic = "Service/+/" + user + "/Re";
            client.subscribe(topic, 2);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe("Service/+/" + user + "/Re");
                client.disconnect();
                client.unregisterResources();
                client = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void AddFriendNotification_re(String friendName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(),Service_CHANNEL_ID_AddFriend);
        builder.setSmallIcon(R.mipmap.ncku_line2);
        builder.setContentTitle("Title : Friend request");
        builder.setContentText(friendName);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Service_CHANNEL_ID_AddFriend,Service_CHANNEL_NAME_AddFriend,NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int notificationId = 1;
        notificationManager.notify(notificationId,builder.build());
    }
}
