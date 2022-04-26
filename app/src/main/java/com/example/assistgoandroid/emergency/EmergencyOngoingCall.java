package com.example.assistgoandroid.emergency;


import android.os.Build;
import android.telecom.Call;
import android.telecom.VideoProfile;

import androidx.annotation.RequiresApi;

import io.reactivex.subjects.BehaviorSubject;
//https://github.com/Abror96/CustomPhoneDialer/blob/master/app/src/main/java/customphonedialer/abror96/customphonedialer/OngoingCall.java
//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/OngoingCall.kt
public class EmergencyOngoingCall {
    public static BehaviorSubject<Integer> state = BehaviorSubject.create();
    private static Call call;

    private Object callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {
            super.onStateChanged(call,newState);
            state.onNext(newState);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public final void setCall(Call value) {
        if(call != null){
            call.unregisterCallback((Call.Callback)callback);
        }
        if(value!=null){
            value.registerCallback((Call.Callback)callback);
            state.onNext(value.getState());
        }

        call = value;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void answer(){
        assert call!=null;
        call.answer(VideoProfile.STATE_AUDIO_ONLY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hangup(){
        assert call!=null;
        call.disconnect();
    }




}
