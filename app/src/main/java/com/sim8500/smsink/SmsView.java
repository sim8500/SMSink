package com.sim8500.smsink;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sbernad on 12/01/15.
 */
public class SmsView extends FrameLayout {

    public TextView msgView;

    public TextView numberView;

    private String messageId = "";

    public TextView dateView;

    public Button showMsgButton;

    private String msgBody;

    private boolean isMsgShown = false;

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
        showMsgButton = (Button)findViewById(R.id.showMsgButton);
        dateView = (TextView)findViewById(R.id.dateTxtView);

        showMsgButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMsgShown)
                {
                    isMsgShown = true;
                    msgView.setText(msgBody);
                    showMsgButton.setVisibility(GONE);
                    msgView.requestLayout();
                }
            }
        });

    }

    public void applyMessage(SmsMessage msg) {

        msgBody = msg.getDisplayMessageBody();
        msgView.setText("\"...\"");
        numberView.setText(msg.getOriginatingAddress());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateView.setText(formatter.format(new Date(msg.getTimestampMillis())));

        messageId = obtainMessageId(msg);
    }

    public String getMessageId() {
        return messageId;
    }

    public static String obtainMessageId(SmsMessage msg) {
        return String.format("%d_%s", msg.getTimestampMillis(), msg.getOriginatingAddress());
    }
}
