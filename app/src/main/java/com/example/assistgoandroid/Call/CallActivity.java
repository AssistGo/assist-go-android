package com.example.assistgoandroid.Call;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;
import com.example.assistgoandroid.emergency.OngoingCallGit;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
//https://github.com/Abror96/CustomPhoneDialer/blob/master/app/src/main/java/customphonedialer/abror96/customphonedialer/CallActivity.java
//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/CallActivity.kt
public class CallActivity extends AppCompatActivity {
    private CompositeDisposable disposables;
    private String number;
    private OngoingCallGit ongoingCall;

    TextView contactName, callStatus;
    ImageView contactImage;
    ImageView onGoingHangupBtn;

    LinearLayout emergencyVideoLayout;
    ImageView emergencyCallBtn, videoCallBtn;

    LinearLayout audioLayout;
    ImageView muteBtn, speakerBtn;

    LinearLayout recievingCallLayout;
    ImageView answerBtn, declineBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);

        ongoingCall = new OngoingCallGit();
        disposables = new CompositeDisposable();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        number = Objects.requireNonNull(getIntent().getData()).getEncodedSchemeSpecificPart();

        Log.d("CallActivity", "onCreate: " + number);

    }



    @Override
    protected void onStart() {
        super.onStart();
        onGoingHangupBtn = findViewById(R.id.onGoingHangupBtnPhone);
        declineBtn = findViewById(R.id.declineBtnPhone);
        answerBtn = findViewById(R.id.answerBtnPhone);

        audioLayout = findViewById(R.id.onGoingCallLinearLayoutPhone);

        answerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                ongoingCall.answer();
            }
        });

        onGoingHangupBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                ongoingCall.hangup();
                finish();
            }
        });


        disposables.add(OngoingCallGit.state.subscribe(state->updateUi(state)));

        disposables.add(OngoingCallGit
                .state
                .filter(state-> state == Call.STATE_DISCONNECTED)
                .delay(1, TimeUnit.SECONDS).firstElement().subscribe(state->finish()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    private void updateUi(int state){
        callStatus.setText(getCallStateString(state));
        if(state==Call.STATE_RINGING){
            recievingCallLayout.setVisibility(View.VISIBLE);

        }
        else{
            recievingCallLayout.setVisibility(View.INVISIBLE);

        }

        if(state == Call.STATE_ACTIVE||state == Call.STATE_DIALING){
            audioLayout.setVisibility(View.VISIBLE);
            emergencyVideoLayout.setVisibility(View.VISIBLE);
            onGoingHangupBtn.setVisibility(View.VISIBLE);
        }
        else{
            audioLayout.setVisibility(View.INVISIBLE);
            emergencyVideoLayout.setVisibility(View.INVISIBLE);
            onGoingHangupBtn.setVisibility(View.INVISIBLE);

        }

    }

    //https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/CallStateString.kt
    private String getCallStateString(int state) {
        switch (state) {
            case Call.STATE_NEW:
                return "NEW";
            case Call.STATE_RINGING:
                return "RINGING";
            case Call.STATE_DIALING:
                return "DIALING";
            case Call.STATE_ACTIVE:
                return "ACTIVE";
            case Call.STATE_HOLDING:
                return "HOLDING";
            case Call.STATE_DISCONNECTED:
                return "DISCONNECTED";
            case Call.STATE_CONNECTING:
                return "CONNECTING";
            case Call.STATE_DISCONNECTING:
                return "DISCONNECTING";
            case Call.STATE_SELECT_PHONE_ACCOUNT:
                return "SELECT_PHONE_ACCOUNT";
            default:
                return "UNKNOWN";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void start(Context context, Call call){
        Intent intent = new Intent(context, CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }
}
