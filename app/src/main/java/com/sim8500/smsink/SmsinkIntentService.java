package com.sim8500.smsink;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sbernad on 13/01/15.
 */
public class SmsinkIntentService extends IntentService {


    public SmsinkIntentService() {
        super("SmsinkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle pdusBundle = intent.getExtras();
        Object[] pdus = (Object[]) pdusBundle.get("pdus");
        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);

        FileOutputStream outp = null;
        try {
            outp = openFileOutput(MainActivity.SINK_FILENAME, MODE_PRIVATE|MODE_APPEND);
        }
        catch (FileNotFoundException ex) {
            Log.d("MAIN", "sms_sink.msgs file not found");
        }

        if(outp != null) {
            SmsinkFileIOUtils.appendSinkFile(outp, sms);
            try {
                outp.close();
            }
            catch(IOException ex) {
                Log.d("MAIN", "sms_sink.msgs error on close()");
            }
        }
        SmsinkWakefulReceiver.completeWakefulIntent(intent);
    }
}
