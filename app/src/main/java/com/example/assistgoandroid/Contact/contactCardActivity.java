package com.example.assistgoandroid.Contact;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.Call.DialerActivity;
import com.example.assistgoandroid.Call.VideoCall;
import com.example.assistgoandroid.Call.VideoCallRinging;
import com.example.assistgoandroid.Call.VoiceCallRinging;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.messageActivity;
import com.example.assistgoandroid.models.Contact;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class contactCardActivity extends AppCompatActivity {
    String TAG = "ContactCard";
    final String CONTACT_CARD = "CONTACT_CARD";
    Contact contact;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_card_activity);

        ImageView contactProfilePicture = findViewById(R.id.ivContactProfilePicture);
        TextView contactName = findViewById(R.id.tvContactName);
        TextView contactPhoneNumber = findViewById(R.id.tvPhoneNumber);

        contact = (Contact) getIntent().getParcelableExtra(CONTACT_CARD);
        Log.i(TAG, "Contact is " + contact);

        contactName.setText(contact.getFullName());
        contactPhoneNumber.setText(contact.getPhoneNumber());
        Glide.with(this)
                .load(contact.getProfileImageUrl())
                .centerCrop()
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(500,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(contactProfilePicture);

    }

    public void onCallClick(View view){
        //Intent intent = new Intent(this, VoiceCallRinging.class);
        Intent intent = new Intent(this, DialerActivity.class);

        intent.putExtra(CONTACT_CARD, contact);
        this.startActivity(intent);
    }

    public void onVideoCallClick(View view){
        Intent intent = new Intent(this, VideoCallRinging.class);
        //Intent intent = new Intent(this, VideoCall.class);

        intent.putExtra(CONTACT_CARD, contact);
        this.startActivity(intent);
    }

    public void onMessageClick(View view){
        Intent intent = new Intent(this, messageActivity.class);
        this.startActivity(intent);
    }

    public void onEditContactClick(View view){
        Intent intent = new Intent(this, editContactCardActivity.class);
        intent.putExtra(CONTACT_CARD, contact);
        this.startActivity(intent);
    }
}
