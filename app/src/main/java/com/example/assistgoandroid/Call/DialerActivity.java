package com.example.assistgoandroid.Call;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.assistgoandroid.models.Contact;

import java.net.URI;

//https://data-flair.training/blogs/calling-app-in-android/
//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/DialerActivity.kt
public class DialerActivity extends AppCompatActivity {
    String phoneNumberToCall;
    Contact contact;
    int REQUEST_PERMISSION = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            contact= (Contact) bundle.getSerializable("CONTACT_CARD");
            phoneNumberToCall = contact.getPhoneNumber();

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantResultsPERMISSION_GRANTED = false;
        for(int i = 0; i<grantResults.length; i++) {
            if (grantResults[i] == PERMISSION_GRANTED) {
                grantResultsPERMISSION_GRANTED = true;
                break;
            }
        }
        if(requestCode == REQUEST_PERMISSION && grantResultsPERMISSION_GRANTED == true) {
            makeCall();
        }
    }



    private void makeCall(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = Uri.parse("tel:" + phoneNumberToCall);
            Intent i = new Intent(Intent.ACTION_CALL,uri);
            startActivity(i);
        }
    }

}
