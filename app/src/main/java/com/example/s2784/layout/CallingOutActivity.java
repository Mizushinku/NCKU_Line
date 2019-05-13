package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CallingOutActivity extends AppCompatActivity implements View.OnClickListener {

    private String sipPeerAddress;
    private SipAudioCall outgoingCall = null;
    private Button buttonMute;
    private Button buttonSpeaker;
    private Button buttonHangUp;
    private TextView tv_calleeName;
    private ImageView imageViewAvatar;

    private final String TAG = "!CallingOutActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingout);

        buttonMute = findViewById(R.id.button_mute);
        buttonSpeaker = findViewById(R.id.button_speaker);
        buttonHangUp = findViewById(R.id.button_hangup_callingOut);
        tv_calleeName = findViewById(R.id.textView_calleeName);
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
                TextView labelView = findViewById(R.id.textview_calloutMsg);
                labelView.setText(status);
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

}
