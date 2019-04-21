package com.example.s2784.layout;

import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallingInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button answer;
    private Button hang_up;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingin);
        answer = findViewById(R.id.button_answer_callingIn);
        hang_up = findViewById(R.id.button_hangup_callingIn);
        textView = findViewById(R.id.textview_callingIn);

        textView.setText(Tabs.sipData.sipAudioCall.getPeerProfile().getDisplayName() + " is calling you");
        answer.setOnClickListener(this);
        hang_up.setOnClickListener(this);

        SipAudioCall.Listener listener = new SipAudioCall.Listener(){
            @Override
            public void onCallEnded(SipAudioCall call) {
                super.onCallEnded(call);
//                call.close();
                try {
                    call.endCall();
                    finish();
                }catch (Exception e){
                    Log.d("SIP", e.getMessage());
                }
            }
        };
        Tabs.sipData.sipAudioCall.setListener(listener);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_answer_callingIn:
                if(!Tabs.sipData.sipAudioCall.isInCall()){
                    try {
                        Tabs.sipData.sipAudioCall.answerCall(30);
                        Tabs.sipData.sipAudioCall.startAudio();
                        Tabs.sipData.sipAudioCall.setSpeakerMode(true);
                        if(Tabs.sipData.sipAudioCall.isMuted()) {
                            Tabs.sipData.sipAudioCall.toggleMute();
                        }

                    }catch(Exception e){
                        Log.d("SIP",e.getMessage());
                    }
                    textView.setText("Call Established");
                }
                break;
            case R.id.button_hangup_callingIn:
                try {
                    Tabs.sipData.sipAudioCall.endCall();
                } catch (Exception e) {
                    Log.d("SIP", e.getMessage());
                }
                break;
        }
    }

}
