package sim8500.com.smsink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 2014-11-30.
 */
public class SmsinkReceiver extends BroadcastReceiver {

    public static interface SmsinkListener {
        public void onSmsed(SmsMessage sms);
    }

    private SmsinkListener listener;

    private List<SmsMessage> messages = new ArrayList<SmsMessage>();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle pdusBundle = intent.getExtras();
        Object[] pdus = (Object[]) pdusBundle.get("pdus");
        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);
        messages.add(sms);
        if(listener != null)
            listener.onSmsed(sms);

        Log.d("SMS-Sink", String.format("SMS: %s, count = %d", sms.getDisplayMessageBody(), messages.size()));
    }

    public void setListener(SmsinkListener lsnr) {
        listener = lsnr;
    }
}