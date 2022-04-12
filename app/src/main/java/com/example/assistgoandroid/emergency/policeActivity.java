package com.example.assistgoandroid.emergency;

<<<<<<< HEAD
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;

public class policeActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 101;
    Button editProfilePicture,changeNumber,editName;
    ImageView userProfilePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.police_page);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null)
                        userProfilePicture.setImageURI(data.getData());
                }
            });
=======
import androidx.appcompat.app.AppCompatActivity;

public class policeActivity extends AppCompatActivity {
>>>>>>> origin/master
}
