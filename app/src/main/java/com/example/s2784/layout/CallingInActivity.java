package com.example.s2784.layout;

import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncorti.slidetoact.SlideToActView;

public class CallingInActivity extends AppCompatActivity {

    private ImageView imageViewAvatar;
    private TextView tv_callerName;
    private TextView tv_status;
    private SlideToActView buttonAnswer;
    private SlideToActView buttonCallEnd;
    private Button buttonMute;
    private Button buttonSpeaker;
    private Button buttonHangup;
    private Group groupCallIn;
    private Long startTime;
    private Handler handler = new Handler();

    private final String TAG = "!CallingInActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingin);

        imageViewAvatar = findViewById(R.id.imageview_call_in_avatar);
        tv_callerName = findViewById(R.id.textView_callerName);
        tv_status = findViewById(R.id.textview_callinMsg);
        groupCallIn = findViewById(R.id.group_call_in);
        buttonAnswer = findViewById(R.id.slide_btn_answer);
        buttonCallEnd = findViewById(R.id.slide_btn_hangup);
        buttonMute = findViewById(R.id.button_call_in_mute);
        buttonSpeaker = findViewById(R.id.button_call_in_speaker);
        buttonHangup = findViewById(R.id.button_hangup_callingIn);

        String callerName = getIntent().getStringExtra("callerName");
        tv_callerName.setText(Tabs.mqtt.MapAlias(Tabs.mqtt.MapPhoneNum2ID(callerName)));
        imageViewAvatar.setImageBitmap(Tabs.mqtt.MapBitmap(Tabs.mqtt.MapPhoneNum2ID(callerName)));

        setBtnListener();

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
                startCount();
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

    public void updateStatus(final String status){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_status.setText(status);
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
        endCount();
        finish();
    }

    private void changeUI(){
        buttonAnswer.setVisibility(View.GONE);
        buttonCallEnd.setVisibility(View.GONE);
        groupCallIn.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) buttonHangup.getLayoutParams();
        layoutParams.horizontalBias = 0.5f;
        buttonHangup.setLayoutParams(layoutParams);
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

    private void setBtnListener(){

        buttonAnswer.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
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
            }
        });

        buttonCallEnd.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                buttonAnswer.setVisibility(View.GONE);
                closeCall();
            }
        });

        buttonAnswer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    buttonCallEnd.setVisibility(View.GONE);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    buttonCallEnd.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        buttonCallEnd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    buttonAnswer.setVisibility(View.GONE);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    buttonAnswer.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        buttonAnswer.setOnSlideToActAnimationEventListener(new SlideToActView.OnSlideToActAnimationEventListener() {
            @Override
            public void onSlideCompleteAnimationStarted(SlideToActView slideToActView, float v) {
                buttonCallEnd.setVisibility(View.GONE);
            }

            @Override
            public void onSlideCompleteAnimationEnded(SlideToActView slideToActView) {
                buttonCallEnd.setVisibility(View.GONE);
            }

            @Override
            public void onSlideResetAnimationStarted(SlideToActView slideToActView) {
            }

            @Override
            public void onSlideResetAnimationEnded(SlideToActView slideToActView) {
            }
        });

        buttonCallEnd.setOnSlideToActAnimationEventListener(new SlideToActView.OnSlideToActAnimationEventListener() {
            @Override
            public void onSlideCompleteAnimationStarted(SlideToActView slideToActView, float v) {
                buttonAnswer.setVisibility(View.GONE);
            }

            @Override
            public void onSlideCompleteAnimationEnded(SlideToActView slideToActView) {
                buttonAnswer.setVisibility(View.GONE);
            }

            @Override
            public void onSlideResetAnimationStarted(SlideToActView slideToActView) {
            }

            @Override
            public void onSlideResetAnimationEnded(SlideToActView slideToActView) {
            }
        });

        buttonHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCall();
            }
        });

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
