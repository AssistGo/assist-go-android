package com.example.assistgoandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;

import com.example.assistgoandroid.Helpers.LocalDatabaseHelper;
import com.example.assistgoandroid.Settings.profileActivity;
import com.example.assistgoandroid.models.User;

import org.json.JSONException;

import java.util.UUID;

public class EnteringActivity extends AppCompatActivity {

    public static User user;
    LocalDatabaseHelper localDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering);

        if (localDatabaseHelper.getCurrentUser() != null){
            user = localDatabaseHelper.getCurrentUser();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        EditText userPhoneNumber = findViewById(R.id.evRegisterPhoneNum);

        localDatabaseHelper = LocalDatabaseHelper.getInstance(this);

        user = new User();

        String uniqueID = UUID.randomUUID().toString();

        user.setId(uniqueID);
        user.setPhoneNumber(String.valueOf(userPhoneNumber.getText()));

        localDatabaseHelper.addOrUpdateUser(user);
        try {
            user.syncUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void registerUserListener(View view){
        Intent intent = new Intent(this, profileActivity.class);
        startActivity(intent);
        finish();
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

        }
    }
}