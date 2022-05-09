package com.example.assistgoandroid.emergency;

import android.telecom.Call;
import android.telecom.InCallService;
/**
 * Le Jie Bennett
 * https://github.com/Abror96/CustomPhoneDialer
 */
public class CallServiceGit extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        new OngoingCallGit().setCall(call);
        CallActivityGit.start(this, call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCallGit().setCall(null);
    }
}
