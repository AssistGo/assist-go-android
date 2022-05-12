package com.example.assistgoandroid.call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;
import com.example.assistgoandroid.models.Contact;
import com.twilio.video.Room;
import com.twilio.video.VideoView;

import java.util.Calendar;
import java.util.Date;

/**
 * Unused class
 */
public class VideoCallRinging extends AppCompatActivity {

    String TAG = "VideoCallRinging";
    String CURRENT_TIME;
    Contact contact;
    VideoView videoView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_ringing_page);

        TextView contactName = findViewById(R.id.tvContactName);

        ImageView hangupBtn = findViewById(R.id.hangupBtn);
        videoView = findViewById(R.id.thumbnail_video_view);

        contact = getIntent().getParcelableExtra("CONTACT_CARD");
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
