package com.example.assistgoandroid.Call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.emergency.emergencyActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VoiceCall extends AppCompatActivity implements Call {

    String TAG = "VoiceCall";
    String CURRENT_TIME;
    Contact contact;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    TextView timestamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_chat_page);

        ImageView contactPicture = findViewById(R.id.ivContactImage);
        ImageView emergencyBtn = findViewById(R.id.emergencyBtn);
        ImageView videochatBtn = findViewById(R.id.videochatBtn);
        ImageView speakerBtn = findViewById(R.id.speakerBtn);
        ImageView muteBtn = findViewById(R.id.muteBtn);
        ImageView hangupBtn = findViewById(R.id.hangupBtn);

        TextView contactName = findViewById(R.id.tvContactName);
        timestamp = findViewById(R.id.tvVCTimeStamp);

        timer = new Timer();
        startTimer();

        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        Log.i(TAG, "Contact is " + contact);

        contactName.setText(contact.getName());
        Glide.with(this)
                .load(contact.getContactPicture())
                .centerCrop()
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(500,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(contactPicture);

        View.OnClickListener emergencyClick = v -> {
            // todo turn off the current call and open the emergency numbers
            finish();
            Intent intent = new Intent(this, emergencyActivity.class);
            startActivity(intent);
        };

        View.OnClickListener videoChatClick = v -> {
            //todo transfer user to videochat call
            Intent intent = new Intent(this, VideoCall.class);
            startActivity(intent);
        };

        View.OnClickListener speakerClick = v -> {
            //if clicked for the first time
            turnOnSpeaker();
            //else
            turnOffSpeaker();
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

        emergencyBtn.setOnClickListener(emergencyClick);
        videochatBtn.setOnClickListener(videoChatClick);
        speakerBtn.setOnClickListener(speakerClick);
        muteBtn.setOnClickListener(muteClick);
        hangupBtn.setOnClickListener(hangupClick);
    }

    @Override
    public void turnOnSpeaker() {
        //TODO implement method
    }

    @Override
    public void turnOffSpeaker() {
        //TODO implement method
    }

    @Override
    public void mute() {
        //TODO implement method
    }

    @Override
    public void unmute() {
        //TODO implement method
    }

    @Override
    public void hangup() {
        //TODO implement method

        timerTask.cancel();
        Date currentTime = Calendar.getInstance().getTime();
        CURRENT_TIME = currentTime.toString();
        contact.setLastCalled(CURRENT_TIME);
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(() -> {
                    time++;
                    timestamp.setText(getTimerText());
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + ":" + String.format("%02d",minutes) + ":" + String.format("%02d",seconds);
    }
}
