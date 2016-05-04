package com.example.androidgeotest.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Util.MyApplication;
import com.example.androidgeotest.activities.Util.MyChronometer;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

/**
 * Created by r.sciamanna on 03/05/2016.
 */
public class RunActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler mHandler = new Handler();
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReStart;

    private Chronometer chronometer;

    long timeWhenStopped = 0;
    double kmValueWhenStopped = 0.000;
    double kcalValueWhenStopped = 0.000;

    private Menu menu;
    private Toolbar toolbar;
    private MenuItem menuItemShare;

    private TextView kcalText;
    private TextView kmText;


    private LocationTracker locationTracker;

    private static List<Location> locationList = new ArrayList<Location>();

    private MyApplication myApplication;

    private static final int MENU_FILTER = 123;
    private static final int MENU_FILTER_CLEAR = 666;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        menu = toolbar.getMenu();

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setText("00:00:00");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
//                cArg.setText(hh+":"+mm+":"+ss);
                cArg.setText(String.format("%02d:%02d:%02d", h, m, s));
            }
        });
//        chronometer.setBase(SystemClock.elapsedRealtime());
        kcalText = (TextView) findViewById(R.id.id_kcal_text);
        kcalText.setText(String.valueOf(kcalValueWhenStopped));

        kmText = (TextView) findViewById(R.id.id_km_text);
        kmText.setText(String.valueOf(kmValueWhenStopped));
        btnStart = (Button) findViewById(R.id.big_start_button);
        btnStart.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.stop_button);
        btnStop.setOnClickListener(this);

        btnPause = (Button) findViewById(R.id.pause_button);
        btnPause.setOnClickListener(this);

        btnReStart = (Button) findViewById(R.id.restart_button);
        btnReStart.setOnClickListener(this);

        myApplication = new MyApplication();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//            finish();
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pause_button:
                doPause();
                break;
            case R.id.stop_button:
                doStop();
                break;
            case R.id.big_start_button:
               doFirstStart();

            case R.id.restart_button:
                doReStart();
                break;
        }
    }



    private Runnable updateTask = new Runnable () {
        public void run() {
            Log.wtf(getString(R.string.app_name) + " ChatList.updateTask()",
                    "updateTask run!");

            kmText.setText(""+kmValueWhenStopped);
            kmValueWhenStopped = kmValueWhenStopped+5;

            // queue the task to run again in 5 seconds...
            mHandler.postDelayed(updateTask, 5000);
        }
    };

    public void doPause(){
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
        mHandler.removeCallbacks(updateTask);

        btnReStart.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
    }

    public void doReStart(){
        chronometer.setBase(SystemClock.elapsedRealtime()
                +timeWhenStopped);
        chronometer.start();
        mHandler.postDelayed(updateTask, 5000);
        btnPause.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);
        btnReStart.setVisibility(View.GONE);
    }

    private void doStop() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        timeWhenStopped = 0;
        kmValueWhenStopped=0;
        chronometer.setText("00:00:00");
        kmText.setText(""+0);
        mHandler.removeCallbacks(updateTask);
        stopListeningLocation();
        btnPause.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
        btnReStart.setVisibility(View.GONE);
        if(!locationList.isEmpty()){
            Log.wtf("locationList","elementi "+locationList.size());
        }
    }

    private void doFirstStart() {
        chronometer.setBase(SystemClock.elapsedRealtime()
                +timeWhenStopped);
        chronometer.start();
        startLocationTracker(this);
        mHandler.postDelayed(updateTask, 5000);

        btnPause.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);
        btnReStart.setVisibility(View.GONE);
    }

    private void startLocationTracker(Context context) {
        TrackerSettings settings =
                new TrackerSettings()
                        .setUseGPS(true)
                        .setUseNetwork(true)
                        .setUsePassive(true);
                        //update every 30 mins
//                        .setTimeBetweenUpdates(30 * 60 * 1000)
                        //update every 100 mt
//                        .setMetersBetweenUpdates(100);

        LocationTracker tracker = new LocationTracker(context, settings) {

            @Override
            public void onLocationFound(Location location) {
                locationList.add(location);
                if(!locationList.isEmpty()){
                    Log.wtf("locationList","elementi "+locationList.size());
                }
            }

            @Override
            public void onTimeout() {
                Log.wtf("timeout","orcodio");
            }
        };
        tracker.startListening();
    }

    private void stopListeningLocation() {
        if (locationTracker != null && locationTracker.isListening()) {
            locationTracker.stopListening();
        }
    }
}
