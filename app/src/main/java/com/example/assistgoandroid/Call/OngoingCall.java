package com.example.assistgoandroid.Call;


import android.os.Build;
import android.telecom.Call;
import android.telecom.VideoProfile;

import androidx.annotation.RequiresApi;

import io.reactivex.subjects.BehaviorSubject;
//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/OngoingCall.kt
public class OngoingCall {
    public static OngoingCall instance;

    public static OngoingCall getInstance() {
        return instance;
    }
    BehaviorSubject<Integer> state = BehaviorSubject.create();

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {
            state.onNext(newState);
        }
    };

    Call call = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setCall(Call newCall) {
        if(call == null){
            call.unregisterCallback(callback);
        }
        call.registerCallback(callback);
        state.onNext(call.getState());


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void answer(){
        call.answer(VideoProfile.STATE_AUDIO_ONLY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hangup(){
        call.disconnect();
    }




}
