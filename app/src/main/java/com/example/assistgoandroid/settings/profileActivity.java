package com.example.assistgoandroid.settings;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.assistgoandroid.MainActivity;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.callActivity;
import com.example.assistgoandroid.contactActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.helpers.VoiceAssistantHelper;
import com.example.assistgoandroid.homemessageActivity;
import com.example.assistgoandroid.translateActivity;

public class profileActivity extends AppCompatActivity implements ChangeUserNameDialog.ChangeUserNameDialogListener {

    private static final int PERMISSION_CODE = 101;
    Button editProfilePicture,changeNumber,editName;
    ImageView userProfilePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        userProfilePicture = findViewById(R.id.userProfilePicture);
        editProfilePicture = findViewById(R.id.editProfilePicture);
        editName = findViewById(R.id.editName);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeUserNameDialog();
            }
        });
        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    checkPermission();
                else
                    selectImageFromGallery();
            }
        });
        changeNumber = findViewById(R.id.changeNumber);
        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this, changeNumberActivity.class);
                startActivity(intent);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setNumber(bundle.getString("input"));
        }

    }

    public void setFirstAndLastName(String firstName, String lastName){
        TextView name = findViewById(R.id.username);
        name.setText(firstName + " " + lastName);
    }

    public void setNumber(String number){
        TextView phoneNumber = findViewById(R.id.phoneNumber);
        phoneNumber.setText(number);
    }
    // check permission to access gallery
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(profileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
        else {
            selectImageFromGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImageFromGallery();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if(data != null)
                        userProfilePicture.setImageURI(data.getData());
                }
            });

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

    public void openChangeUserNameDialog() {
        ChangeUserNameDialog changeUserNameDialog = new ChangeUserNameDialog();
        changeUserNameDialog.show(getSupportFragmentManager(),"changeUserName");
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
