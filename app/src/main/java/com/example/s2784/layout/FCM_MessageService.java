package com.example.s2784.layout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
    private static String curUser = "";

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

            if(!code.equals(visibleRoomCode) && !msgTitle.equals(curUser)) {
                sendNotification(msgTitle, msgText);
                SQLiteManager.setContext(getApplicationContext());
                SQLiteManager.DBinit();
                SQLiteManager.queryForBadge(code);

                Intent intent = new Intent();
                intent.putExtra("code_for_badge",code);
                intent.setAction("com.example.s2784.layout.action.badgeNum");
                sendBroadcast(intent);
            }
        }
    }

    private void sendNotification(String title, String text) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, getString(R.string.CHANNEL_ID_ReceiveMsg))
                        .setSmallIcon(R.mipmap.ncku_line2)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID_ReceiveMsg),
                    getString(R.string.CHANNEL_NAME_ReceiveMsg),
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        int notiID = Integer.parseInt(getString(R.string.CHANNEL_ID_ReceiveMsg));
        manager.notify(notiID, builder.build());
    }

    public static void setVisibleRoomCode(String visibleRoomCode) {
        FCM_MessageService.visibleRoomCode = visibleRoomCode;
    }

    public static void setCurUser(String curUser) {
        FCM_MessageService.curUser = curUser;
    }
}
