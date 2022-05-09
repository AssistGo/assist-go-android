package com.example.assistgoandroid;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.models.Contact;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class messageActivity extends AppCompatActivity {

    Contact contact;
    ImageButton btnSendSMS;
    ImageButton btnRecordVoice;
    RecyclerView rvMessages;
    private List<String> messageList = new ArrayList<>();
    IntentFilter intentFilter;
    EditText evMessage;
    String currUser;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //display msg in text view
            //todo add it to text view
            String message = intent.getExtras().getString("message");
            messageList.add(message);
            rvMessages.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        contact = getIntent().getParcelableExtra("CONTACT_CARD");

        TextView tvContactName = findViewById(R.id.tvContactName);
        ImageView ivContactProfilePicture = findViewById(R.id.ivContactProfilePicture);
        rvMessages = findViewById(R.id.rvChat);

        tvContactName.setText(contact.getFullName());

        Glide.with(this)
                .load(contact.getProfileImageUrl())
                .centerCrop()
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(500,10))
                .placeholder(R.drawable.loading_contact)
                .error(R.drawable.loading_contact)
                .into(ivContactProfilePicture);

        checkPermission();

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        btnSendSMS = (ImageButton) findViewById(R.id.sendSMSBtn);
        evMessage = (EditText) findViewById(R.id.MessagePhraseInput);

        btnSendSMS.setOnClickListener(view -> {
            String myMsg = evMessage.getText().toString();
            sendMsg(contact.getPhoneNumber(), myMsg);
            evMessage.setText("");
        });

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(messageActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(messageActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            //Request Permission
            ActivityCompat.requestPermissions(messageActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);
            ActivityCompat.requestPermissions(messageActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 200);
        } else {
            populateMessages();
        }
    }

    private void sendMsg(String phoneNumber, String myMsg) {
        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, myMsg, sentPI, deliveredPI);
    }

    @Override
    protected void onResume() {
        //register receiver
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //unregister receiver
        unregisterReceiver(intentReceiver);
        super.onPause();
    }


}
