package com.example.assistgoandroid.settings;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.MainActivity;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.callActivity;
import com.example.assistgoandroid.contactActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.helpers.VoiceAssistantHelper;
import com.example.assistgoandroid.homemessageActivity;
import com.example.assistgoandroid.translateActivity;

public class aboutActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_page);
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
