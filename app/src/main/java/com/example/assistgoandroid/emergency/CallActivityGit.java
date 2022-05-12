package com.example.assistgoandroid.emergency;

import static com.example.assistgoandroid.emergency.Constants.asString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import kotlin.collections.CollectionsKt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assistgoandroid.R;
/**
 * Le Jie Bennett
 * https://github.com/Abror96/CustomPhoneDialer
 */
public class CallActivityGit extends AppCompatActivity {

    @BindView(R.id.answer)
    Button answer;
    @BindView(R.id.hangup)
    Button hangup;
    @BindView(R.id.mute)
    Button mute;
    @BindView(R.id.speaker)
    Button speaker;
    @BindView(R.id.callInfo)
    TextView callInfo;
    @BindView(R.id.contactImagePhone)
    ImageView contactImagePhone;


    private CompositeDisposable disposables;
    private String number;
    private OngoingCallGit ongoingCall;
    private String name = "";
    private boolean speakerON = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CallACtivity", "onCreate: ");
        setContentView(R.layout.activity_call);
        ButterKnife.bind(this);

        ongoingCall = new OngoingCallGit();
        disposables = new CompositeDisposable();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        String data = getIntent().getData().toString();
        Log.d("CallActivityGit", "onCreate: " + data);
        String [] array = data.split(":");
        number = array[0];
        name = array[1];
        number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart();
        //name = Objects.requireNonNull(getIntent().getStringExtra("name"));
        Log.d("CallActivityGit", "onCreate: extras" + name + number);

    }

    @OnClick(R.id.answer)
    public void onAnswerClicked() {
        ongoingCall.answer();
    }

    @OnClick(R.id.hangup)
    public void onHangupClicked() {
        Log.d("CallACtivity", "onHangup ");
        ongoingCall.hangup();
    }

    @OnClick(R.id.mute)
    public void onMuteClicked() {
        Log.d("CallACtivity", "onMuteClicked ");
        AudioManager audM = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if(audM.isMicrophoneMute()==false){
            audM.setMicrophoneMute(true);
            Log.d("CallACtivity", "onMuterClicked now mute");
            mute.setBackgroundResource(R.drawable.mic_transparent);



        }
        else{
            audM.setMicrophoneMute(false);
            Log.d("CallACtivity", "onMuteClicked now unmute");
            mute.setBackgroundResource(R.drawable.mute_icon);


        }
    }

    @OnClick(R.id.speaker)
    public void onSpeakerClicked() {
        Log.d("CallACtivity", "onSpeakerClicked ");


        if(speakerON==false){
            enableSpeaker();
            speaker.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            speakerON = true;


        }
        else {
            disableSpeaker();
            speaker.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            speakerON = false;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CallACtivity", "onStart: ");


        //assert updateUi(-1) != null;
        updateUi(-1);
        disposables.add(
                OngoingCallGit.state
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                updateUi(integer);
                            }
                        }));

        disposables.add(
                OngoingCallGit.state
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return integer == Call.STATE_DISCONNECTED;
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .firstElement()
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                finish();
                            }
                        }));

    }

    @SuppressLint("SetTextI18n")
    private Consumer<? super Integer> updateUi(Integer state) {
        Log.d("CallACtivityGit", "updateUi: " + state +"number: " +  number);
        //callInfo.setText(asString(state) + "\n" + name + " " + number);
        String displayName = number.split(":")[1];
        callInfo.setText(asString(state) + "\n" +displayName);

        setPhoto(displayName);


        if (state != Call.STATE_RINGING) {
            answer.setVisibility(View.GONE);
        } else answer.setVisibility(View.VISIBLE);

        if (CollectionsKt.listOf(new Integer[]{
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE}).contains(state)) {
            hangup.setVisibility(View.VISIBLE);
        } else
            hangup.setVisibility(View.GONE);

        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallActivityGit.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
        Log.d("CallACtivity", "startFunction: ");

    }

    //https://stackoverflow.com/questions/29801031/how-to-add-button-tint-programmatically
    private void enableSpeaker() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setSpeakerphoneOn(true);
            }
        }
    }
    //https://stackoverflow.com/questions/29801031/how-to-add-button-tint-programmatically
    private void disableSpeaker() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);

            }
        }
    }

    private void setPhoto(String displayName){
        switch(displayName){
            case "Ambulance":
                contactImagePhone.setImageResource(R.drawable.image_am);
                break;
            case "Police":
                contactImagePhone.setImageResource(R.drawable.image_police);
                break;
            case "Firefighter":
                contactImagePhone.setImageResource(R.drawable.image_fire);
                break;
            default:
                contactImagePhone.setImageResource(R.drawable.loading_contact);
                break;
        }
    }
}
