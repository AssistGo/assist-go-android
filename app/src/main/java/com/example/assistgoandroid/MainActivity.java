package com.example.assistgoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button contactLabelBtn, callLabelBtn, messageLabelBtn, translateLabelBtn;
    ImageButton contactImageBtn, callImageBtn, messageImageBtn, translateImageBtn;
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
                Intent intent = new Intent(MainActivity.this, messageActivity.class);
                startActivity(intent);
            }
        });
        messageLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, messageActivity.class);
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



    }
}