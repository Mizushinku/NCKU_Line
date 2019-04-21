package com.example.s2784.layout;

import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallingOutActivity extends AppCompatActivity implements View.OnClickListener {

    private String status;
    private Handler handler;
    private Runnable runnable;
    private Button hang_up;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingout);
        hang_up = findViewById(R.id.button_hangup_callingOut);
        textView = findViewById(R.id.textview_callingOut);

//        textView.setText(MainActivity.sipData.sipAudioCall.getPeerProfile().getDisplayName());
        hang_up.setOnClickListener(this);

        SipAudioCall.Listener listener = new SipAudioCall.Listener() {
            @Override
            public void onCallEstablished(SipAudioCall call) {
                super.onCallEstablished(call);
                call.startAudio();
                call.setSpeakerMode(true);
                if(call.isMuted()){
                    call.toggleMute();
                }
                status = "Call Established";
                handler.post(runnable);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                super.onCallEnded(call);
                call.close();
                finish();
                status = "Call Ended";
                handler.post(runnable);
            }

            @Override
            public void onCallBusy(SipAudioCall call) {
                super.onCallBusy(call);
                call.close();
                finish();
                status = "Call Busy";
                handler.post(runnable);
            }

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                super.onRinging(call, caller);
                status = "onRinging";
                handler.post(runnable);
            }
//
//            @Override
//            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
//                super.onError(call, errorCode, errorMessage);
//                Log.d("SIP",errorMessage + " qweqwe");
//                try {
//                    call.endCall();
//                }catch (Exception e){
//                    Log.d("SIP",e.getMessage());
//                }
//                call.close();
//                finish();
//            }
        };
        try {
            Tabs.sipData.sipAudioCall = Tabs.sipData.sipManager.makeAudioCall(Tabs.sipData.sipProfile.getUriString(), Tabs.sipData.peer.getUriString(), listener, 30 );
        }catch (Exception e){
            Log.d("SIP",e.getMessage());
        }
        Tabs.sipData.sipAudioCall.setListener(listener);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        };

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_hangup_callingOut:
                try {
                    Tabs.sipData.sipAudioCall.endCall();
                } catch (Exception e) {
                    Log.d("SIP", e.getMessage());
                }
                break;
        }
    }

    private void refresh(){textView.setText(status);}
}
