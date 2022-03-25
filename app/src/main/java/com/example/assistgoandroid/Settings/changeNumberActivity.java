package com.example.assistgoandroid.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;

public class changeNumberActivity extends AppCompatActivity {
    Button changeNumberButton;
    EditText phoneNumberInput;
    ImageButton sendButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_number_page);
        changeNumberButton = findViewById(R.id.changeNumberButton);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        sendButton = findViewById(R.id.sendButton);
        changeNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumberInput.getText().toString().isEmpty()){
                    Toast.makeText(changeNumberActivity.this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    openVerificationDialog();
//                    displayVerifiedNumber();
                }
            }
        });

    }
    //prompt user to enter verification code sent to their number to verify number
    public void openVerificationDialog(){
        VerificationDialog verificationDialog = new VerificationDialog();
        verificationDialog.show(getSupportFragmentManager(),"verification");

    }

    //once we have verified the number we send verified number to profile activity
    public void displayVerifiedNumber(){
        Intent intent  = new Intent(changeNumberActivity.this,profileActivity.class);
        String input = phoneNumberInput.getText().toString();
        intent.putExtra("input",input);
        startActivity(intent);

    }
}
