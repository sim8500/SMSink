package com.sim8500.smsink;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class MainActivity extends Activity implements SmsinkReceiver.SmsinkListener, SurfaceHolder.Callback {

    public static final String SINK_FILENAME = "sms_sink.msgs";

    private SmsinkAdapter adapter = new SmsinkAdapter();

    private RecyclerView smsListView;

    private SurfaceView surfaceView;

    Random random = new Random();

    Paint drawingPaint;

    Paint bmpPaint;

    double drawingPhase = 0.f;

    double drawingRadius = 0.f;

    Bitmap drawingBmp = null;

    Canvas drawingCanvas = null;

    boolean firstDraw = true;

    @Override
    public void onSmsed(final SmsMessage sms) {
        adapter.addMessage(sms);

    }

    private SmsinkReceiver recv = new SmsinkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        smsListView = (RecyclerView)view.findViewById(R.id.listView);

        smsListView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        smsListView.setHasFixedSize(true);
        setContentView(view);

        drawingBmp = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        drawingCanvas = new Canvas(drawingBmp);

        surfaceView = (SurfaceView)view.findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);

        FileInputStream msgsInput = null;
        try {
            msgsInput = openFileInput(SINK_FILENAME);
        }
        catch (FileNotFoundException ex) {
            Log.d("MAIN", "sms_sink.msgs file not found");
        }

        if(msgsInput != null) {
            adapter.initAdapter(this, SmsinkFileIOUtils.readSinkFile(msgsInput));
            smsListView.setAdapter(adapter);
            try {
                msgsInput.close();
            }
            catch(IOException ex) {
                Log.d("MAIN", "sms_sink.msgs error on close()");
            }
        }

    }

/*
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
*/
    @Override
    protected void onSaveInstanceState (Bundle outState) {

        super.onSaveInstanceState(outState);

        if(adapter.getMessages().size() > 0)
        {
            FileOutputStream outp = null;
            try {
                outp = openFileOutput(SINK_FILENAME, MODE_PRIVATE);
            }
            catch (FileNotFoundException ex) {
                Log.d("MAIN", "sms_sink.msgs file not found");
            }

            if(outp != null) {
                SmsinkFileIOUtils.writeSinkFile(outp, adapter.getMessages());
                try {
                    outp.close();
                }
                catch(IOException ex) {
                    Log.d("MAIN", "sms_sink.msgs error on close()");
                }
            }

        }
    }

    public void onResume() {
        ComponentName comp = new ComponentName(this.getApplicationContext(), SmsinkWakefulReceiver.class.getName());
        if(getPackageManager() != null)
            getPackageManager().setComponentEnabledSetting(comp, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        recv.setListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        this.registerReceiver(recv, filter);

        super.onResume();
    }

    public void onPause()
    {
        ComponentName comp = new ComponentName(this.getApplicationContext(), SmsinkWakefulReceiver.class.getName());
        if(getPackageManager() != null)
        getPackageManager().setComponentEnabledSetting(comp, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        recv.setListener(null);
        this.unregisterReceiver(recv);

        super.onPause();
    }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            tryDrawing(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
            tryDrawing(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}

        private void tryDrawing(SurfaceHolder holder) {

            if(drawingCanvas != null) {
                drawMyStuff(drawingCanvas);
            }

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {

                canvas.drawBitmap(drawingBmp, 0.f, 0.f, null);
                holder.unlockCanvasAndPost(canvas);

                surfaceView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawingPhase += 0.05*Math.asin(1.0);
                        tryDrawing(surfaceView.getHolder());
                    }
                }, 1000/12);
            }
        }

        private void drawMyStuff(final Canvas canvas) {

            if(drawingPaint == null) {
                drawingPaint = new Paint();
                drawingPaint.setColor(Color.parseColor("#89BE44"));
                drawingPaint.setStyle(Paint.Style.STROKE);
                drawingPaint.setStrokeWidth(dpToPx(1.0f));
                drawingPaint.setAntiAlias(true);
            }

            float rx = 0.5f*canvas.getWidth();
            float ry = 0.5f*canvas.getHeight();

            if(drawingRadius == 0.0) {
                drawingRadius = dpToPx(64.0f);

            }

            rx += Math.sin(drawingPhase)*drawingRadius;
            ry -= Math.cos(drawingPhase)*drawingRadius;

            if(firstDraw) {
                canvas.drawColor(Color.WHITE);
                firstDraw = false;
            }
            else canvas.drawColor(Color.argb(32, 255, 255, 255));

            canvas.drawCircle(rx, ry, dpToPx(8.f), drawingPaint);
        }


    public float dpToPx(float dpValue) {

        return dpValue * getResources().getDisplayMetrics().density;
    }
}
