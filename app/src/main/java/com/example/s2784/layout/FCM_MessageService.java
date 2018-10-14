package com.example.s2784.layout;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCM_MessageService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("TAG","Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendRegistrationToServer(String token) {

    }
}
