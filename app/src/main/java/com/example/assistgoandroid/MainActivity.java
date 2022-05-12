package com.example.assistgoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.helpers.VoiceAssistantHelper;
import com.example.assistgoandroid.settings.settingsActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;

/**
 * Le Jie Bennett
 */
public class MainActivity extends AppCompatActivity {



    Button contactLabelBtn, callLabelBtn, messageLabelBtn, translateLabelBtn;
    ImageButton contactImageBtn, callImageBtn, messageImageBtn, translateImageBtn, settingsImageBtn, emergencyAmbulanceBtn;
    TextView emergencyLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        contactLabelBtn = findViewById(R.id.contactBtnLabel);
        callLabelBtn = findViewById(R.id.callBtnLabel);
        messageLabelBtn = findViewById(R.id.messageBtnLabel);
        translateLabelBtn = findViewById(R.id.translateBtnLabel);
        emergencyLabel = findViewById(R.id.emergencyBtn);


        contactImageBtn = findViewById(R.id.contactImageBtn);
        callImageBtn = findViewById(R.id.callImageBtn);
        messageImageBtn = findViewById(R.id.messageImageBtn);
        translateImageBtn = findViewById(R.id.translateImageBtn);
        settingsImageBtn = findViewById(R.id.settingsImageBtn);
        emergencyAmbulanceBtn = findViewById(R.id.emergencyAmbulanceBtn);

        settingsImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, settingsActivity.class);
                startActivity(intent);
            }
        });

        contactImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, contactActivity.class);
                startActivity(intent);
            }
        });
        contactLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, contactActivity.class);
                startActivity(intent);
            }
        });

        callImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, callActivity.class);
                startActivity(intent);
            }
        });
        callLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, callActivity.class);
                startActivity(intent);
            }
        });

        messageImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, homemessageActivity.class);
                startActivity(intent);
            }
        });
        messageLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, homemessageActivity.class);
                startActivity(intent);
            }
        });



        translateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, translateActivity.class);
                startActivity(intent);
            }
        });

        translateLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, translateActivity.class);
                startActivity(intent);
            }
        });

        emergencyLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, emergencyActivity.class);
                startActivity(intent);
            }
        });

        emergencyAmbulanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,emergencyActivity.class);
                startActivity(intent);
            }
        });
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

        VoiceAssistantHelper.parseInput(this, requestCode, resultCode, data);
    }
}