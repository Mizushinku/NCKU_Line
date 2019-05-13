package com.example.s2784.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.util.Log;

import java.util.Objects;

public class IncomingCallReceiver extends BroadcastReceiver {

    private String TAG = "!!IncomingCallReceiver";
    private SipAudioCall incomingCall = null;
    private String callerName;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"in onReceive");

        try{
            incomingCall = Tabs.sipData.manager.takeAudioCall(intent, null);
            if(Tabs.sipData.call != null){
                incomingCall.endCall();
                incomingCall.close();
                return;
            }
            Tabs.sipData.call = incomingCall;
            Log.d(TAG, "in onReceive try block");
        }catch (Exception e){
            if(incomingCall != null){
                incomingCall.close();
            }
            Log.d(TAG, "in onReceive catch");
        }

        Intent callInActivity;
        callInActivity = new Intent(context, CallingInActivity.class);
        callerName = Objects.requireNonNull(incomingCall).getPeerProfile().getUserName();
        callInActivity.putExtra("callerName", callerName);
        context.startActivity(callInActivity);


    }
}

