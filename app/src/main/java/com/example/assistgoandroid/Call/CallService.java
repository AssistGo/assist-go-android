package com.example.assistgoandroid.Call;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
//https://github.com/arekolek/simple-phone/blob/master/app/src/main/java/com/github/arekolek/phone/CallService.kt
public class CallService extends InCallService {
    OngoingCall ongoingCall = OngoingCall.getInstance();
    CallActivity callActivity = CallActivity.getInstance();

    @Override
    public void onCallAdded(Call call) {
        ongoingCall.call = call;
        Intent i = new Intent(this,CallActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(call.getDetails().getHandle());
        startActivity(i);
    }

    @Override
    public void onCallRemoved(Call call) {
        ongoingCall.call = null;
    }
}
