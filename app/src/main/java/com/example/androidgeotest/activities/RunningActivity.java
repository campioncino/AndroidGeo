package com.example.androidgeotest.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Chronometer.MyChronometer;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

/**
 * Created by r.sciamanna on 11/05/2016.
 */


public class RunningActivity extends AppCompatActivity implements View.OnClickListener {


    private Fragment mapFragment;
    final static String TAG = RunningActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReStart;

    private IconicsButton btnLock;
    private Chronometer chronometer;

    double kmValueWhenStopped = 0.000;
    double kcalValueWhenStopped = 0.000;
    private LocationTracker locationTracker;
    private Location location;

    private static List<Location> locationList = new ArrayList<Location>();

    private TextView kcalText;
    private TextView kmText;
    private long timeWhenStopped = 0;
    private String text;

    private Bundle mbundle;
    private int viewId;

    private View myChronometerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.running_activity_layout);
//        AndroidBug5497Workaround.assistActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myChronometerLayout = findViewById(R.id.mychronometerLayout);

        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment).commit();


        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Attività");
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedAppbar);

        //setto l'appbar collapsed, così vedo una sola riga, mantenendo il dettaglio se scrollo
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.setExpanded(false);


        chronometer = (Chronometer) myChronometerLayout.findViewById(R.id.mychronometer);
        kcalText = (TextView) findViewById(R.id.id_kcal_text);
        kcalText.setText(String.valueOf(kcalValueWhenStopped));

        kmText = (TextView) findViewById(R.id.id_km_text);
        kmText.setText(String.valueOf(kmValueWhenStopped));
        btnStart = (Button) findViewById(R.id.big_start_button);
        btnStart.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.stop_button);
        btnStop.setOnClickListener(this);
        btnStop.setEnabled(false);

        btnPause = (Button) findViewById(R.id.pause_button);
        btnPause.setOnClickListener(this);
        btnPause.setEnabled(false);

        btnReStart = (Button) findViewById(R.id.restart_button);
        btnReStart.setOnClickListener(this);
        btnReStart.setEnabled(false);

        btnLock = (IconicsButton) findViewById(R.id.lock_button);
        btnLock.setOnClickListener(this);
        initChrono();


    }


    private void initChrono() {
        chronometer.setText("00:00:00");
        chronometer = (Chronometer) findViewById(R.id.mychronometer);
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
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.big_start_button:
                doFirstStart();
                break;
            case R.id.lock_button:
                doLock();
                break;
            case R.id.pause_button:
                doPause();
                break;
            case R.id.restart_button:
                doReStart();
                break;
            case R.id.stop_button:
                doStop();
                break;
        }
    }

    private void doFirstStart() {
        initChrono();
        kmText.setText(String.valueOf(kmValueWhenStopped));
        chronometer.setBase(SystemClock.elapsedRealtime()
                + timeWhenStopped);
        chronometer.start();
        startLocationTracker(this);



        mHandler.postDelayed(updateTask, 2000);

        btnLock.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.VISIBLE);
        btnPause.setEnabled(false);
        btnStop.setVisibility(View.VISIBLE);
        btnStop.setEnabled(false);
        btnStart.setVisibility(View.GONE);
        btnReStart.setVisibility(View.GONE);
    }

    public Location getLastBestLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {

            return locationGPS;
        }
        else {

            return locationNet;
        }
    }


    private void doLock() {
        if (btnLock.isEnabled()) {
            btnReStart.setEnabled(true);
            btnReStart.setBackgroundColor(getResources().getColor(R.color.green400));
            btnPause.setEnabled(true);
            btnPause.setBackgroundColor(getResources().getColor(R.color.orange700));
            btnStop.setEnabled(true);
            btnStop.setBackgroundColor(getResources().getColor(R.color.red700));
            btnLock.setEnabled(false);
            btnLock.setTextColor(getResources().getColor(R.color.blueGray400));
            btnLock.setText("{gmd-lock-open}");
            /*************************************************/

            final Runnable r = new Runnable() {
                public void run() {
                    btnLock.setText("{gmd-lock}");
                    btnLock.setEnabled(true);
                    btnLock.setTextColor(getResources().getColor(R.color.accent));
                    btnStop.setEnabled(false);
                    btnStop.setBackgroundColor(getResources().getColor(R.color.blueGray400));
                    btnPause.setEnabled(false);
                    btnPause.setBackgroundColor(getResources().getColor(R.color.blueGray400));
                    btnReStart.setEnabled(false);
                    btnReStart.setBackgroundColor(getResources().getColor(R.color.blueGray400));
                }
            };
            mHandler.postDelayed(r, 5000);

            /****************************************************/


        } else {
            System.out.println("entro qui");
            btnLock.setText("{gmd-lock}");
            btnLock.setEnabled(true);
            btnLock.setTextColor(getResources().getColor(R.color.accent));
            btnStop.setEnabled(false);
            btnStop.setBackgroundColor(getResources().getColor(R.color.blueGray400));
            btnPause.setEnabled(false);
            btnPause.setBackgroundColor(getResources().getColor(R.color.blueGray400));
            btnReStart.setEnabled(false);
            btnReStart.setBackgroundColor(getResources().getColor(R.color.green400));
        }
    }

    private Runnable updateTask = new Runnable() {
        public void run() {
            Log.wtf(TAG + " ChatList.updateTask()",
                    "updateTask run!");

            kmText.setText("" + kmValueWhenStopped);
            kmValueWhenStopped = kmValueWhenStopped + 5;

            // queue the task to run again in 5 seconds...
            mHandler.postDelayed(updateTask, 5000);
        }
    };

    public void doPause() {
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
        mHandler.removeCallbacks(updateTask);

        btnReStart.setVisibility(View.VISIBLE);
        btnReStart.setEnabled(true);
        btnStop.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
    }

    public void doReStart() {
        Log.wtf(TAG, "Restart pushed");
        chronometer.setBase(SystemClock.elapsedRealtime()
                + timeWhenStopped);
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
        kmValueWhenStopped = 0;
        chronometer.setText("00:00:00");
        kmText.setText("" + 0.0);
        mHandler.removeCallbacks(updateTask);
        stopListeningLocation();
        btnPause.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
        btnReStart.setVisibility(View.GONE);
        if (!locationList.isEmpty()) {
            Log.wtf("locationList", "elementi " + locationList.size());
        }
    }

    private void startLocationTracker(Context context) {
        TrackerSettings settings =
                new TrackerSettings()
                        .setUseGPS(true)
                        .setUseNetwork(true)
                        .setUsePassive(true)
                        //update every 30 mins
//                        .setTimeBetweenUpdates(30 * 60 * 1000)
                        //update every 3 seconds
                        .setTimeBetweenUpdates(3 * 1000);
//        update every 100 mt
//                        .setMetersBetweenUpdates(100);

        locationTracker = new LocationTracker(context, settings) {

            @Override
            public void onLocationFound(Location location) {
                locationList.add(location);
                if (!locationList.isEmpty()) {
                    Log.wtf("locationList", "elementi " + locationList.size());
                }
            }

            @Override
            public void onTimeout() {
                Log.wtf(TAG, "Timeout");
            }
        };
        locationTracker.startListening();
    }

    private void stopListeningLocation() {
        if (locationTracker != null && locationTracker.isListening()) {
            locationTracker.stopListening();
        }
    }


    public static List<Location> getLocations() {
        return locationList;
    }

    public static void clearLocations() {
        locationList.clear();
    }

    public static Location getLastLocation() {
        if (!locationList.isEmpty()) {
            return locationList.get(locationList.size() - 1);
        }
        return null;
    }

    public static Location getFirstLocation() {
        if (!locationList.isEmpty()) {
            return locationList.get(0);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                super.onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
