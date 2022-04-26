package com.example.assistgoandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.assistgoandroid.Helpers.LocalDatabaseHelper;
import com.example.assistgoandroid.Settings.profileActivity;
import com.example.assistgoandroid.models.User;

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
        user.syncUser();

    }

    public void registerUserListener(View view){
        Intent intent = new Intent(this, profileActivity.class);
        startActivity(intent);
        finish();
    }
}