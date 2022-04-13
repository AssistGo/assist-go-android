package com.example.assistgoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class homemessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);
    }
    public void onNewHomeMessageClick(View view) {
        Intent intent = new Intent(homemessageActivity.this, contactActivity.class);
        startActivity(intent);
    }

}
