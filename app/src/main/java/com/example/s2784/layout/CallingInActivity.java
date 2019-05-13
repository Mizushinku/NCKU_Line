package com.example.s2784.layout;

import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CallingInActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewAvatar;
    private TextView tv_callerName;
    private Button buttonAnswer;
    private Button buttonCallEnd;
    private Button buttonMute;
    private Button buttonSpeaker;
    private Group groupCallIn;
    private String TAG = "!CallingInActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingin);

        imageViewAvatar = findViewById(R.id.imageview_call_in_avatar);
        tv_callerName = findViewById(R.id.textView_callerName);
        groupCallIn = findViewById(R.id.group_call_in);
        buttonAnswer = findViewById(R.id.button_answer_callingIn);
        buttonCallEnd = findViewById(R.id.button_hangup_callingIn);
        buttonMute = findViewById(R.id.button_call_in_mute);
        buttonSpeaker = findViewById(R.id.button_call_in_speaker);

        String callerName = getIntent().getStringExtra("callerName");
        tv_callerName.setText(callerName);

        buttonAnswer.setOnClickListener(this);
        buttonCallEnd.setOnClickListener(this);

        SipAudioCall.Listener listener = new SipAudioCall.Listener(){
            @Override
            public void onChanged(SipAudioCall call) {
                super.onChanged(call);
                Log.d(TAG, "onChanged");
            }

            @Override
            public void onCallEstablished(SipAudioCall call) {
                super.onCallEstablished(call);
                updateStatus("通話中...");
                Log.d(TAG, "onCallEstablished");
            }

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                super.onRinging(call, caller);
                updateStatus("正在撥電話給你...");
                Log.d(TAG, "onRinging");
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

        Tabs.sipData.call.setListener(listener, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_answer_callingIn:
                changeUI();
                try{
                    Tabs.sipData.call.answerCall(30);
                    Tabs.sipData.call.startAudio();
                    Tabs.sipData.call.setSpeakerMode(false);
                    if(Tabs.sipData.call.isMuted()){
                        Tabs.sipData.call.toggleMute();
                    }
                }catch (Exception e){
                    Tabs.sipData.call.close();
                    Tabs.sipData.call = null;
                    Log.d(TAG, "in onClick ANSWER_CALL catch block");
                }
                Log.d(TAG, "in onClick ANSWER_CALL");
                break;
            case R.id.button_hangup_callingIn:
                closeCall();
                break;
        }

    }

    public void updateStatus(final String status){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView labelView = findViewById(R.id.textview_callinMsg);
                labelView.setText(status);
            }
        });
    }

    private void closeCall(){
        try{
            Tabs.sipData.call.endCall();
            updateStatus("通話結束...");
        }catch (Exception e){
            Log.d(TAG,"in closeCall func:" + e.getMessage());
        }
        Tabs.sipData.call.close();
        Tabs.sipData.call = null;
        finish();
    }

    private void changeUI(){
        buttonAnswer.setVisibility(View.GONE);
        groupCallIn.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) buttonCallEnd.getLayoutParams();
        layoutParams.horizontalBias = 0.5f;
        buttonCallEnd.setLayoutParams(layoutParams);
    }

    public void toggleMute(View view){
//        Toast.makeText(this, "toggleMute", Toast.LENGTH_LONG).show();
        if(Tabs.sipData.call.isInCall()){
            if(buttonMute.isSelected()){
                buttonMute.setSelected(false);
            }else{
                buttonMute.setSelected(true);
            }
            Tabs.sipData.call.toggleMute();
        }
    }
    public void toggleSpeaker(View view){
//        Toast.makeText(this, "toggleSpeaker", Toast.LENGTH_LONG).show();
        if(Tabs.sipData.call.isInCall()){
            if(buttonSpeaker.isSelected()){
                buttonSpeaker.setSelected(false);
                Tabs.sipData.call.setSpeakerMode(false);
            }else {
                buttonSpeaker.setSelected(true);
                Tabs.sipData.call.setSpeakerMode(true);
            }
        }
    }
}
