package com.example.assistgoandroid.Call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.models.Contact;

import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VoiceCallRinging extends AppCompatActivity {

    String TAG = "VoiceCallRinging";
    String CURRENT_TIME;
    Contact contact;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_chat_ringing_page);

        ImageView contactPicture = findViewById(R.id.ivContactImage);
        TextView contactName = findViewById(R.id.tvContactName);

        ImageView emergencyBtn = findViewById(R.id.emergencyBtn);
        ImageView videochatBtn = findViewById(R.id.videochatBtn);
        ImageView hangupBtn = findViewById(R.id.hangupBtn);

        contact = (Contact) getIntent().getParcelableExtra("CONTACT_CARD");
        Log.i(TAG, "Contact is " + contact);

        contactName.setText(contact.getFullName());
        Glide.with(this)
                .load(contact.getProfileImageUrl())
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

        View.OnClickListener hangupClick = v -> {
            hangup();
            //go back to previous page
            finish();
        };

        emergencyBtn.setOnClickListener(emergencyClick);
        videochatBtn.setOnClickListener(videoChatClick);
        hangupBtn.setOnClickListener(hangupClick);
    }

    private void hangup() {
        Date currentTime = Calendar.getInstance().getTime();
        CURRENT_TIME = currentTime.toString();
        contact.setLastCalled(CURRENT_TIME);
        finish();
    }

    private void callConnected() {
        //todo do the connection
        Intent intent = new Intent(this, VoiceCall.class);
        intent.putExtra("CONTACT_CARD", contact);
        this.startActivity(intent);
    }
}
