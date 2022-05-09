package com.example.assistgoandroid.Call;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.assistgoandroid.emergency.EmergencyDialerActivity;
import com.example.assistgoandroid.models.Contact;


import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import kotlin.collections.ArraysKt;
//https://github.com/Abror96/CustomPhoneDialer/blob/master/app/src/main/java/customphonedialer/abror96/customphonedialer/DialerActivity.java
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
            contact= (Contact) bundle.getParcelable("CONTACT_CARD");
            phoneNumberToCall = contact.getPhoneNumber().replaceAll("[^\\d.]", "");
            Log.d("RecievedPhoneNumber", "onCreate: " + phoneNumberToCall);
        }

        checkPermission();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        offerReplacingDefaultDialer();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(DialerActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            //Request Permission
            ActivityCompat.requestPermissions(DialerActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
        }
        else {
            makeCall();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        }
        else {
            Toast.makeText(DialerActivity.this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            checkPermission();
        }


//        if (requestCode == REQUEST_PERMISSION && ArraysKt.contains(grantResults, PERMISSION_GRANTED)) {
//            makeCall();
//        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void offerReplacingDefaultDialer() {
        Log.d("DialerActivity", "offerReplacingDefaultDialer: ");
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {

            Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            Log.d("DialerActivity", "offerReplacingDefaultDialer: created Intent");
            startActivity(intent);
        }
    }

    private void makeCall(){
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {
            //Temporary phone to call
//            phoneNumberToCall = "5183391997";
            Uri uri = Uri.parse("tel:" + phoneNumberToCall);
            Log.d("EmergencyDialerActivity", "makeCall: " + phoneNumberToCall);
            Intent i = new Intent(Intent.ACTION_CALL,uri);
            startActivity(i);
            Date currentTime = Calendar.getInstance().getTime();
            String CURRENT_TIME = currentTime.toString();
            contact.setLastCalled(CURRENT_TIME);
        }
    }

}
