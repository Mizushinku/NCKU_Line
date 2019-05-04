package com.example.s2784.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.util.Log;

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

//        try {
//            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
//                @Override
//                public void onRinging(SipAudioCall call, SipProfile caller) {
//                    try {
//                        call.answerCall(30);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            Tabs tabs = (Tabs) context;
//            incomingCall = tabs.sipData.sipManager.takeAudioCall(intent, listener);
//            tabs.sipData.sipAudioCall = incomingCall;
//        } catch (Exception e) {
//            if (incomingCall != null) {
//                incomingCall.close();
//            }
//        }

        Intent callInActivity;
        callInActivity = new Intent(context, CallingInActivity.class);
        context.startActivity(callInActivity);
    }
}

