package com.sim8500.smsink;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by sbernad on 14/01/15.
 */
public class SmsinkWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getApplicationContext(), SmsinkIntentService.class.getName());

        intent.setComponent(comp);

        // Start the service, keeping the device awake while it is launching
        startWakefulService(context, intent);
        setResultCode(Activity.RESULT_OK);
    }
}
