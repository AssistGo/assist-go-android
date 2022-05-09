package com.example.assistgoandroid.emergency;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.telecom.TelecomManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import kotlin.collections.ArraysKt;

import static android.Manifest.permission.CALL_PHONE;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import com.example.assistgoandroid.R;
/**
 * Le Jie Bennett
 * https://github.com/Abror96/CustomPhoneDialer
 */
public class DialerActivityGit extends AppCompatActivity {

    @BindView(R.id.phoneNumberInput)
    EditText phoneNumberInput;

    @BindView(R.id.confirmCall)
    Button confirmCall;

    public static int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        ButterKnife.bind(this);
        Log.d("DialerActivity", "onCreate: ");


        if (getIntent() != null && getIntent().getData() != null)
            phoneNumberInput.setText(getIntent().getData().getSchemeSpecificPart());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("DialerActivity", "onStart: ");

        offerReplacingDefaultDialer();


        confirmCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });
        phoneNumberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                makeCall();
                return true;
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeCall() {
        Log.d("DialerActivity", "MakeCall: ");
        Log.d("DialerActivity", "makeCall: Permission Granted");
        //Uri uri = Uri.parse("tel:"+phoneNumberInput.getText().toString().trim());

        //Uri uri = Uri.parse("tel:"+ getIntent().getStringExtra("number"));
        Uri uri = Uri.parse("tel:"+ getIntent().getStringExtra("number") + ":" + getIntent().getStringExtra("name"));

        Log.d("DialerActivity", "makeCall: Permission Granted" + phoneNumberInput.getText().toString());
        Log.d("DialerActivity", "makeCall: Permission Granted" + getIntent().getStringExtra("number"));


        requestPermissions(new String[]{CALL_PHONE}, 1);


        if (PermissionChecker.checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {

            Intent callIntent = new Intent(Intent.ACTION_CALL,uri);
            callIntent.putExtra("name",getIntent().getStringExtra("name"));
            Log.d("DialerActivityGit", "makeCall: " + getIntent().getStringExtra("name"));

            ;           //startActivity(new Intent(Intent.ACTION_CALL, uri));
            startActivity(callIntent);

        }
    }

    private void offerReplacingDefaultDialer() {
        Log.d("DialerActivity", "OfferReplacingDefaultDialer ");

        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        startActivity(intent);

        if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            Intent intent2 = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(intent2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("DialerActivity", "OnRequestPersmissionResult ");

        if (requestCode == REQUEST_PERMISSION && ArraysKt.contains(grantResults, PERMISSION_GRANTED)) {
            makeCall();
        }
    }
}
