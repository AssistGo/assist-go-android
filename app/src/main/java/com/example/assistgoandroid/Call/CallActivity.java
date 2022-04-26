package com.example.assistgoandroid.Call;

import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.TimeUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assistgoandroid.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/CallActivity.kt
public class CallActivity extends AppCompatActivity {
    private static CallActivity instance;
    public static CallActivity getInstance() {
        return instance;
    }

    CompositeDisposable disposables = new CompositeDisposable();
    String number;

    TextView contactName, callStatus;
    ImageView contactImage;
    ImageView onGoingHangupBtn;

    LinearLayout emergencyVideoLayout;
    ImageView emergencyCallBtn, videoCallBtn;

    LinearLayout audioLayout;
    ImageView muteBtn, speakerBtn;

    LinearLayout recievingCallLayout;
    ImageView answerBtn, declineBtn;

    OngoingCall ongoingCall = OngoingCall.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);
        number = getIntent().getData().getEncodedSchemeSpecificPart();

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
            }
        });


        disposables.add(ongoingCall.state.subscribe(state->updateUi(state)));

        disposables.add(ongoingCall.state
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
}
