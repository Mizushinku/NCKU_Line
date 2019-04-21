package com.example.s2784.layout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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
            String userID = data.get("userID");
            String roomType = data.get("roomType");

            if(!code.equals(visibleRoomCode) && !msgTitle.equals(curUser)) {
                sendNotification(msgTitle, msgText, code, userID, roomType);
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

    private void sendNotification(String title, String text, String code, String userID, String roomType) {

        Intent resultIntent = new Intent(this, Tabs.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Tabs.class);
        /*
        switch (roomType) {
            case "F":
            case "G":
                resultIntent = new Intent(this, Chatroom.class);
                stackBuilder.addParentStack(Chatroom.class);
                Log.d("fcm", "in case F or G");
                break;

            case "C":
                resultIntent = new Intent(this, Classroom.class);
                stackBuilder.addParentStack(Classroom.class);
                Log.d("fcm", "in case C");
                break;

            default:
                resultIntent =new Intent(this, Tabs.class);
                stackBuilder.addParentStack(Tabs.class);
                Log.d("fcm", "in case default");
                break;
        }
        */
        resultIntent.putExtra("code", code);
        resultIntent.putExtra("id",userID);
        resultIntent.putExtra("roomType", roomType);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, getString(R.string.CHANNEL_ID_ReceiveMsg))
                        .setSmallIcon(R.mipmap.ncku_line2)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(resultPendingIntent);

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
