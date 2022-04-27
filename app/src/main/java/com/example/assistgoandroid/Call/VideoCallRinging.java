package com.example.assistgoandroid.Call;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.models.Contact;
import com.twilio.video.ConnectOptions;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoView;

import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideoCallRinging extends AppCompatActivity {

    String TAG = "VideoCallRinging";
    String CURRENT_TIME;
    Contact contact;
    VideoView videoView;
    String accessToken;
    String tokenURL = "https://rackley-iguana-5070.twil.io/video-token";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_ringing_page);

        TextView contactName = findViewById(R.id.tvContactName);

        ImageView hangupBtn = findViewById(R.id.hangupBtn);
        videoView = findViewById(R.id.thumbnail_video_view);

        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        Log.i(TAG, "Contact is " + contact);

        contactName.setText(contact.getFullName());

        View.OnClickListener hangupClick = v -> {
            hangup();
            //go back to previous page
            finish();
        };

        hangupBtn.setOnClickListener(hangupClick);
    }

    private void hangup() {
        Date currentTime = Calendar.getInstance().getTime();
        CURRENT_TIME = currentTime.toString();
        contact.setLastCalled(CURRENT_TIME);

        finish();
    }

    private void callConnected(Room room) {
        //todo do the connection
        Intent intent = new Intent(this, VideoCall.class);
        intent.putExtra("CONTACT_CARD", contact);
        intent.putExtra("ROOM", room.getName());
        this.startActivity(intent);
    }
}
