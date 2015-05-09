package com.sim8500.smsink;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbernad on 08/01/15.
 */
public class SmsinkAdapter extends RecyclerView.Adapter<SmsinkAdapter.SmsinkViewHolder> {

    private Context context;
    private List<SmsMessage> messages = new ArrayList<SmsMessage>();

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
        notifyItemInserted(messages.size()-1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    public void removeMessage(int position) {
        Log.d("Smsink", String.format("(-) - %d", position));
        messages.remove(position);
        notifyItemRemoved(position);
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
    public void onBindViewHolder(SmsinkViewHolder holder, final int position) {
        holder.sv.applyMessage(messages.get(position));

        holder.sv.msgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeMessage(position);
                return true;
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }
}
