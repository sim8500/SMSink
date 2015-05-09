package com.sim8500.smsink;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by sbernad on 12/01/15.
 */
public class SmsView extends FrameLayout {

    public TextView msgView;

    public TextView numberView;

    public SmsView(Context context) {
        super(context);
        init();
    }

    public SmsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.row_message, this);

        msgView = (TextView)findViewById(R.id.msgView);
        numberView = (TextView)findViewById(R.id.numberTxtView);
    }

    public void applyMessage(SmsMessage msg) {
        msgView.setText(msg.getDisplayMessageBody());
        numberView.setText(msg.getOriginatingAddress());
    }
}
