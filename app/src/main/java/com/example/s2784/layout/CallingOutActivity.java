package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.sip.SipAudioCall;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CallingOutActivity extends AppCompatActivity implements View.OnClickListener {

    private String sipPeerAddress;
    private SipAudioCall outgoingCall = null;
    private Button buttonMute;
    private Button buttonSpeaker;
    private Button buttonHangUp;
    private TextView tv_calleeName;
    private TextView tv_status;
    private ImageView imageViewAvatar;
    private Long startTime;
    private Handler handler = new Handler();

    private final String TAG = "!CallingOutActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingout);

        //Change status color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color

        buttonMute = findViewById(R.id.button_mute);
        buttonSpeaker = findViewById(R.id.button_speaker);
        buttonHangUp = findViewById(R.id.button_hangup_callingOut);
        tv_calleeName = findViewById(R.id.textView_calleeName);
        tv_status = findViewById(R.id.textview_calloutMsg);
        imageViewAvatar = findViewById(R.id.imageview_call_out_avatar);

        String peerUserName = getIntent().getStringExtra("sipUserName");
        String calleeName = getIntent().getStringExtra("Name");
        byte[] bytes = getIntent().getByteArrayExtra("avatar");

        tv_calleeName.setText(calleeName);
        sipPeerAddress = peerUserName + "@" + Tabs.sipData.domain;
        Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageViewAvatar.setImageBitmap(avatar);

        buttonHangUp.setOnClickListener(this);

        initiateCall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_hangup_callingOut:
                closeCall();
                break;
        }
    }

    private void closeCall(){
        try {
            outgoingCall.endCall();
            updateStatus("通話結束...");
        }catch (Exception e){
            Log.d(TAG,"in closeCall func:" + e.getMessage());
        }
        outgoingCall.close();
        Tabs.sipData.call = null;
        endCount();
        finish();
    }

    public void initiateCall(){
        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener(){
                @Override
                public void onChanged(SipAudioCall call) {
                    super.onChanged(call);
                    Log.d(TAG, "onChanged");
                }

                @Override
                public void onReadyToCall(SipAudioCall call) {
                    super.onReadyToCall(call);
                    Log.d(TAG, "onReadyToCall");
                }

                @Override
                public void onCalling(SipAudioCall call) {
                    super.onCalling(call);
                    updateStatus("正在連線...");
                    Log.d(TAG, "onCalling");
                }

                @Override
                public void onCallBusy(SipAudioCall call) {
                    super.onCallBusy(call);
                    updateStatus("無人接聽...");
                    closeCall();
                    Log.d(TAG, "onCallBusy");
                }

                @Override
                public void onRingingBack(SipAudioCall call) {
                    super.onRingingBack(call);
                    updateStatus("撥號中...");
                    Log.d(TAG, "onRingingBack");
                }

                @Override
                public void onCallEstablished(SipAudioCall call) {
                    super.onCallEstablished(call);
                    call.startAudio();
                    updateStatus("通話中...");
                    Log.d(TAG, "onCallEstablished");
                    startCount();
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    super.onCallEnded(call);
                    updateStatus("通話結束...");
                    Log.d(TAG, "onCallEnded");
                    closeCall();
                }

                @Override
                public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                    super.onError(call, errorCode, errorMessage);
                    Log.d(TAG, "in onError,  errorCode: " + Integer.toString(errorCode)
                            + "\nerrorMessage : " + errorMessage);
                    closeCall();
                }
            };

            outgoingCall = Tabs.sipData.manager.makeAudioCall(Tabs.sipData.me.getUriString(), sipPeerAddress, null, 30);
            outgoingCall.setListener(listener, true);
            outgoingCall.setSpeakerMode(false);

            Tabs.sipData.call = outgoingCall;
        }catch (Exception e){
            Log.i(TAG, "in InitiateCall catch block", e);
            Tabs.sipData.closeLocalProfile();
            Tabs.sipData.initializeManager();
            if (outgoingCall != null) {
                outgoingCall.close();
            }
        }
    }

    public void updateStatus(final String status){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText(status);
            }
        });
    }

    public void toggleMute(View view){
//        Toast.makeText(this, "toggleMute", Toast.LENGTH_LONG).show();
        if(outgoingCall.isInCall()){
            if(buttonMute.isSelected()){
                buttonMute.setSelected(false);
            }else{
                buttonMute.setSelected(true);
            }
            outgoingCall.toggleMute();
        }
    }
    public void toggleSpeaker(View view){
//        Toast.makeText(this, "toggleSpeaker", Toast.LENGTH_LONG).show();
        if(outgoingCall.isInCall()){
            if(buttonSpeaker.isSelected()){
                buttonSpeaker.setSelected(false);
                outgoingCall.setSpeakerMode(false);
            }else {
                buttonSpeaker.setSelected(true);
                outgoingCall.setSpeakerMode(true);
            }
        }
    }

    private void startCount(){
        Log.d("TEST", "start");
        startTime = System.currentTimeMillis();
        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);
        updateStatus("0:00");
    }

    private void endCount(){
        Log.d("TEST", "end");
        handler.removeCallbacks(updateTimer);
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            Long spentTime = System.currentTimeMillis() - startTime;
            Long min = (spentTime/1000)/60;
            Long sec = (spentTime/1000)%60;
            String str;
            if(sec < 10){
                str = min + ":0" + sec;
            }else{
                str = min + ":" + sec;
            }
            handler.postDelayed(this, 1000);
            updateStatus(str);
            Log.d("TEST", "run");
        }
    };

}
