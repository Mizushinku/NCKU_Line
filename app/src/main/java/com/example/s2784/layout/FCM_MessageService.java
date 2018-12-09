package com.example.s2784.layout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCM_MessageService extends FirebaseMessagingService {

    private static String visibleRoomCode = "";

    @Override
    public void onNewToken(String token) {
        Log.d("TAG","Refreshed token: " + token);
        getSharedPreferences("FCM_Token", MODE_PRIVATE).edit().putString("token", token).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size() > 0) {
            Map<String,String> data = remoteMessage.getData();
            String msgTitle = data.get("msgTitle");
            String msgText = data.get("msgText");
            String code = data.get("code");

            if(!code.equals(visibleRoomCode)) {
                sendNotification(msgTitle, msgText);
                SQLiteManager.setContext(getApplicationContext());
                SQLiteManager.DBinit();
                SQLiteManager.queryForBadge(code);
            }
        }
    }

    private void sendNotification(String title, String text) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, NotiValues.CHANNEL_ID_ReceiveMsg)
                        .setSmallIcon(R.mipmap.ncku_line2)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotiValues.CHANNEL_ID_ReceiveMsg,
                    NotiValues.CHANNEL_NAME_ReceiveMsg,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(NotiValues.notificationId_ReceiveMsg, builder.build());
    }

    public static void setVisibleRoomCode(String visibleRoomCode) {
        FCM_MessageService.visibleRoomCode = visibleRoomCode;
    }

}
