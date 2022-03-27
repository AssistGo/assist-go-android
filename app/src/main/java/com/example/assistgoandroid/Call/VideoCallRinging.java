package com.example.assistgoandroid.Call;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.Contact.Contact;
import com.example.assistgoandroid.R;

import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideoCallRinging extends AppCompatActivity {

    String TAG = "VideoCallRinging";
    String CURRENT_TIME;
    Contact contact;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_ringing_page);

        ImageView contactPicture = findViewById(R.id.ivContactPicture);
        TextView contactName = findViewById(R.id.tvContactName);

        ImageView hangupBtn = findViewById(R.id.hangupBtn);

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

    private void callConnected() {
        //todo do the connection
        Intent intent = new Intent(this, VideoCall.class);
        intent.putExtra("CONTACT_CARD", contact);
        this.startActivity(intent);
    }
}
