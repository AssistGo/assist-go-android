package com.example.assistgoandroid.Settings;
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

import com.example.assistgoandroid.Helpers.LocalDatabaseHelper;
import com.example.assistgoandroid.MainActivity;
import com.example.assistgoandroid.R;
import com.example.assistgoandroid.callActivity;
import com.example.assistgoandroid.contactActivity;
import com.example.assistgoandroid.emergency.emergencyActivity;
import com.example.assistgoandroid.homemessageActivity;
import com.example.assistgoandroid.models.Contact;
import com.example.assistgoandroid.models.User;
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

        if(requestCode == 100 && resultCode == RESULT_OK){
            String resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            // TODO PARSE STRING HERE!!!!
            if (resultString.equalsIgnoreCase("go to the main screen")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the main page")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Page")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Main Screen")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Page")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Landing Screen")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main screen")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the main page")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main screen")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main page")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to main")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to main")) {
                Intent intent = new Intent(profileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency screen")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the emergency page")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("i have an emergency")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Page")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("emergency Screen")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("ambulance")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("police")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("fire")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("firefighter")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency screen")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the emergency page")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency screen")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency page")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to emergency")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to emergency")) {
                Intent intent = new Intent(profileActivity.this, emergencyActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to send a message")) {
                Intent intent = new Intent(profileActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Message")) {
                Intent intent = new Intent(profileActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(profileActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Send a message")) {
                Intent intent = new Intent(profileActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("Messages")) {
                Intent intent = new Intent(profileActivity.this, homemessageActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translate")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("translation")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me translation page")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translation screen")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to translate screen")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate screen")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translate page")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation screen")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to translation page")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate something")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to translate")) {
                Intent intent = new Intent(profileActivity.this, translateActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("call")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I want to call someone")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call page")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to call screen")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call page")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the call screen")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call page")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the call screen")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call page")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call screen")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to call")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("I need to call")) {
                Intent intent = new Intent(profileActivity.this, callActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go settings")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings page")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("settings screen")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings screen")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the settings page")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting screen")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to the setting page")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings screen")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to settings page")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting screen")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("go to setting page")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("setting")) {
                Intent intent = new Intent(profileActivity.this, settingsActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts page")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contacts screen")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact page")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("contact screen")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts page")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contacts screen")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact page")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to contact screen")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts page")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contacts screen")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact page")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            } else if (resultString.equalsIgnoreCase("take me to the contact screen")) {
                Intent intent = new Intent(profileActivity.this, contactActivity.class);
                startActivity(intent);
            }
        }
    }
}
