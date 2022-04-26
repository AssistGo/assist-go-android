package com.example.assistgoandroid.emergency;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.assistgoandroid.Call.CallActivity;
import com.example.assistgoandroid.Call.OngoingCall;

@RequiresApi(api = Build.VERSION_CODES.M)
//https://github.com/Abror96/CustomPhoneDialer/blob/master/app/src/main/java/customphonedialer/abror96/customphonedialer/CallService.java
//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/CallService.kt
public class EmergencyCallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        Log.d("EmergencyCallService", "onCallAdded: On Call added");
        super.onCallAdded(call);
        new OngoingCall().setCall(call);
        EmergencyCallActivity.start(this,call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }
}
