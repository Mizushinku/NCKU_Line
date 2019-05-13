package com.example.s2784.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Handler;
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
    private TextView tv_msg;
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
        tv_msg = findViewById(R.id.textview_calloutMsg);
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

    private void closeCall(){
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_hangup_callingOut:
                closeCall();
                break;
        }
    }

    public void initiateCall(){

    }

    public void toggleMute(View view){
        Toast.makeText(this, "toggleMute", Toast.LENGTH_LONG).show();
    }
    public void toggleSpeaker(View view){
        Toast.makeText(this, "toggleSpeaker", Toast.LENGTH_LONG).show();
    }
}
