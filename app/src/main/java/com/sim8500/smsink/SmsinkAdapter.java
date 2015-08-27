package com.sim8500.smsink;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbernad on 08/01/15.
 */
public class SmsinkAdapter extends RecyclerView.Adapter<SmsinkAdapter.SmsinkViewHolder> {

    private Context context;
    private List<SmsMessage> messages = new ArrayList<SmsMessage>();

    private AlertDialog dialogInstance = null;

    public static class SmsinkViewHolder extends RecyclerView.ViewHolder {

        public SmsView sv;

        public SmsinkViewHolder(SmsView v) {
            super(v);
            sv = v;
        }
    }

    public void initAdapter(Context context, List<SmsMessage> msgs) {

        this.context = context;
        this.messages.addAll(msgs);
    }

    public List<SmsMessage> getMessages() { return this.messages; }

    public void addMessage(SmsMessage msg) {
        messages.add(msg);
        if(messages.size() == 1)
            notifyDataSetChanged();
        else
            notifyItemInserted(messages.size()-1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    public void removeMessage(String messageId) {
        int msgIndex = -1;
        for(int i = 0; i < messages.size(); ++i) {
            SmsMessage msg = messages.get(i);
            if(SmsView.obtainMessageId(msg).equals(messageId))
            {
                msgIndex = i;
                break;
            }
        }
        if(msgIndex != -1) {
            Log.d("Smsink", String.format("(-) - %d", msgIndex));
            messages.remove(msgIndex);
            notifyItemRemoved(msgIndex);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    @Override
    public SmsinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SmsView smsview = new SmsView(context);
        SmsinkViewHolder svh = new SmsinkViewHolder(smsview);
        return svh;
    }

    @Override
    public void onBindViewHolder(SmsinkViewHolder holder, int position) {
        holder.sv.applyMessage(messages.get(position));

        final SmsView smsview = holder.sv;
        holder.sv.msgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(smsview);
                return true;
            }
        });
    }

    public void showDialog(SmsView view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final String messageId = view.getMessageId();
        View dlgView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_delete_confirm, null);
        ((Button)dlgView.findViewById(R.id.confirm_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsinkAdapter.this.removeMessage(messageId);
                if(dialogInstance != null)
                    dialogInstance.dismiss();
            }
        });
        builder.setView(dlgView);
        dialogInstance = builder.create();
        dialogInstance.show();
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }
}
