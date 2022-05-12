package com.example.assistgoandroid;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.RecognizerIntent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.message.ChatAdapter;
import com.example.assistgoandroid.message.Message;
import com.example.assistgoandroid.settings.settingsActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.models.Contact;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class messageActivity extends AppCompatActivity {

    Contact contact;
    ImageButton btnSendSMS;
    ImageButton btnRecordVoice;
    RecyclerView rvMessages;
    private final List<Message> messageList = new ArrayList<>();
    IntentFilter intentFilter;
    EditText evMessage;
    String currUserID = "me";
    ChatAdapter adapter;
    String TAG = "messaging";

    private final BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //display msg in text view
            String messageBody = intent.getExtras().getString("message");
            Message message = new Message();
            message.setMessageBody(messageBody);
            message.setProfilePicture(contact.getProfileImageUrl());
            message.setUserName(contact.getFullName());
            message.setUserID(contact.getFullPhoneNumber());
            messageList.add(message);
            adapter.notifyDataSetChanged();

            Log.i(TAG, "msg received: " + messageBody);
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

        btnSendSMS = findViewById(R.id.sendSMSBtn);
        evMessage = findViewById(R.id.etMessage);

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

    private void populateMessages() {
        adapter = new ChatAdapter(messageActivity.this, currUserID, messageList);
        rvMessages.setAdapter(adapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(messageActivity.this);
        rvMessages.setLayoutManager(linearLayoutManager);
    }

    private void sendMsg(String phoneNumber, String myMsg) {
        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, myMsg, sentPI, deliveredPI);

        Message message = new Message();
        message.setUserID("me");
        message.setMessageBody(myMsg);
        messageList.add(message);
        adapter.notifyDataSetChanged();

        //testing purposes
        if(myMsg.contains("hi") || myMsg.contains("hello") || myMsg.contains("how")) {
            Message message2 = new Message();
            message2.setMessageBody("Hello! Today is fantastic! How are you?");
            message2.setProfilePicture(contact.getProfileImageUrl());
            message2.setUserName(contact.getFullName());
            message2.setUserID(contact.getFullPhoneNumber());
            messageList.add(message2);
            adapter.notifyDataSetChanged();
        }
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


    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            String resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            // TODO PARSE STRING HERE!!!!
            if (resultString.equalsIgnoreCase("go to the main screen")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the main page")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Page")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Screen")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Page")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Screen")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main screen")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main page")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main screen")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main page")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to main")) {
                Intent intent = new Intent(messageActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency screen")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency page")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("i have an emergency")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Page")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Screen")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("ambulance")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("police")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("fire")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("firefighter")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency screen")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency page")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency screen")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency page")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to emergency")) {
                Intent intent = new Intent(messageActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to send a message")) {
                Intent intent = new Intent(messageActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Message")) {
                Intent intent = new Intent(messageActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(messageActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Send a message")) {
                Intent intent = new Intent(messageActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(messageActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translate")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translation")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me translation page")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation screen")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate screen")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate screen")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate page")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation screen")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation page")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate something")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate")) {
                Intent intent = new Intent(messageActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("call")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call someone")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call page")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call screen")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call page")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call screen")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call page")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call screen")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call page")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call screen")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I need to call")) {
                Intent intent = new Intent(messageActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go settings")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings page")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings screen")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings screen")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings page")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting screen")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting page")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings screen")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings page")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting screen")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting page")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("setting")) {
                Intent intent = new Intent(messageActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts page")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts screen")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact page")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact screen")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts page")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts screen")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact page")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact screen")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts page")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts screen")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact page")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact screen")) {
                Intent intent = new Intent(messageActivity.this, contactActivity.class);
                startActivity(intent);
            }
        }
    }
}
