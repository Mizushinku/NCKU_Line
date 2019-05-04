package com.example.s2784.layout;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipErrorCode;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.util.Log;
import android.widget.TextView;

public class SipData {
//    private String status;
//    public SipProfile sipProfile;
//    public SipProfile peer;
//    public SipAudioCall sipAudioCall;
//    public SipManager sipManager;
//
//    public SipData(Context context){
//        if(sipManager == null){
//            sipManager = SipManager.newInstance(context);
//        }
//    }
//
//    public String getStatus() {
//        return status;
//    }
//    public void setStatus(String s){
//        status = s;
//    }
//
//    public void setSipProfile() {
//        try {
//            SipProfile.Builder builder = new SipProfile.Builder("4041","140.116.82.40");
//            builder.setPassword("5678@ncku");
//            sipProfile = builder.build();
//        }catch (Exception e) {
//            Log.d("SIP",e.getMessage());
//        }
//    }
//    public void setPeer(){
//        try {
//            SipProfile.Builder builder = new SipProfile.Builder("4040", "140.116.82.40");
//            peer = builder.build();
//        }catch (Exception e){
//            Log.d("SIP",e.getMessage());
//        }
//    }

    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public String domain = "140.116.82.40";
    public String intentAction = "android.NCKU_Line.INCOMING_CALL";
    private Context ctx;
    private final Tabs tabs;
    private String TAG = "!!SIP";

    public SipData(Context context){
        this.ctx = context;
        tabs = (Tabs)ctx;
    }

    public void initializeManager(){
        if(manager == null){
            manager = SipManager.newInstance(ctx);
        }

        initializeLocalProfile();
    }

    public void initializeLocalProfile(){
        if(me != null){
            closeLocalProfile();
        }

        String username = Tabs.mqtt.MapPhoneNum(Tabs.userID);
        String password = username + "@ncku";
        String displayName = username;

        try{
            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            builder.setDisplayName(displayName);
            builder.setAutoRegistration(true);
            builder.setProtocol("UDP");
            me = builder.build();

            Intent i = new Intent();
            i.setAction(intentAction);
            PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);

            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                @Override
                public void onRegistering(String localProfileUri) {
                    updateStatus("Registering with SIP Server");
                    Log.d(TAG, "onRegistering");
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateStatus("Register Done");
                    Log.d(TAG, "Register Done");
                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    updateStatus("Register Fail");
                    Log.d(TAG, "in onRegistrationFailed, error code : " +
                            Integer.toString(errorCode) + "\nerrorMessage: " + errorMessage);

                    // use this to avoid registering fail problem, but I do not why other
                    // (e.g. in ExpandableListAdapter : call out) is not useful

                    if(errorCode == SipErrorCode.IN_PROGRESS){
                        closeLocalProfile();
                        initializeManager();
                        Log.d(TAG, "in onRegistrationFailed SipErrorCode.IN_PROGRESS" );
                    }

                }
            });

        }catch (Exception e){
            updateStatus("Connection error");
        }

        Log.d(TAG, "in initializeLocalProfile");
    }

    public void closeLocalProfile(){
        if(manager == null){
            return;
        }
        try {
            if(me != null){
                manager.close(me.getUriString());
            }
        } catch (Exception e){
            Log.d(TAG, "closeLocalProfile catch");
        }
        Log.d(TAG,"closeLocalProfile");
    }

    public void updateStatus(final String status){
        tabs.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = tabs.findViewById(R.id.textview_sipStatus);
                textView.setText(status);
            }
        });
    }
}
