package com.example.assistgoandroid.Call;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.R;

import java.util.Calendar;
import java.util.Date;

public class VideoCall extends AppCompatActivity implements Call {

    String TAG = "VideoCall";
    String CURRENT_TIME;
    Contact contact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_page);

        ImageView switchCameraBtn = findViewById(R.id.switchCamBtn);
        ImageView videochatBtn = findViewById(R.id.videochatBtn);
        ImageView muteBtn = findViewById(R.id.muteBtn);
        ImageView hangupBtn = findViewById(R.id.hangupBtn);

        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        Log.i(TAG, "Contact is " + contact);

        View.OnClickListener switchCameraClick = v -> {
            switchCamera();
        };

        View.OnClickListener videoChatClick = v -> {
            //todo turn off camera or turn on camera
        };


        View.OnClickListener muteClick = v -> {
            //if clicked for the first time
            mute();
            //else
            unmute();
        };

        View.OnClickListener hangupClick = v -> {
            hangup();
            //go back to previous page
            finish();
        };

        switchCameraBtn.setOnClickListener(switchCameraClick);
        videochatBtn.setOnClickListener(videoChatClick);
        muteBtn.setOnClickListener(muteClick);
        hangupBtn.setOnClickListener(hangupClick);
    }

    private void switchCamera() {
        //TODO implement method
    }

    @Override
    public void turnOnSpeaker() {

    }

    @Override
    public void turnOffSpeaker() {

    }

    @Override
    public void mute() {

    }

    @Override
    public void unmute() {

    }

    @Override
    public void hangup() {
        Date currentTime = Calendar.getInstance().getTime();
        CURRENT_TIME = currentTime.toString();
        contact.setLastCalled(CURRENT_TIME);

        finish();
    }
}
