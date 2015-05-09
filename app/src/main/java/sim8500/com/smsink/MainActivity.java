package sim8500.com.smsink;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements SmsinkReceiver.SmsinkListener {

    private TextView smsTxtView;

    @Override
    public void onSmsed(final SmsMessage sms) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                smsTxtView.setText(smsTxtView.getText() + "\n\n\n" + sms.getDisplayMessageBody());
            }
        });

    }

    private SmsinkReceiver recv = new SmsinkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        smsTxtView = (TextView)view.findViewById(R.id.smsTxtView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStart()
    {
        super.onStart();
        recv.setListener(this);
        this.registerReceiver(recv, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    public  void onStop()
    {
        super.onStop();
        recv.setListener(null);
        this.unregisterReceiver(recv);
    }
}
