package com.example.assistgoandroid;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class profileActivity extends AppCompatActivity {

    Button editProfilePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        editProfilePicture = findViewById(R.id.editProfilePicture);

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Allow users to upload their profile pictures
            }
        });
    }

    public void setFirstAndLastName(String firstName, String lastName){
        TextView name = findViewById(R.id.editName);
        name.setText(firstName + " " + lastName);
    }

    public void setNumber(String number){
        TextView phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumber.setText("Number: " + number);
    }


}
