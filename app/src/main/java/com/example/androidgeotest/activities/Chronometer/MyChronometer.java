package com.example.androidgeotest.activities.Chronometer;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.androidgeotest.R;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;


/**
 * Created by r.sciamanna on 03/05/2016.
 */
public class MyChronometer extends Fragment implements View.OnClickListener{
    final static String TAG = MyChronometer.class.getSimpleName();
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

    private static List<Location> locationList = new ArrayList<Location>();

    private TextView kcalText;
    private TextView kmText;
    private long timeWhenStopped = 0;
    private Chronometer myChronometer;
    private String text;

    private Bundle mbundle;
    private int viewId;

    private View myView;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.mychronometer_layout, container, false);
        mbundle = getArguments();


        myChronometer = (Chronometer) myView.findViewById(R.id.mychronometer);
//        myChronometer.setText("00:00:00");

        kcalText = (TextView) myView.findViewById(R.id.id_kcal_text);
        kcalText.setText(String.valueOf(kcalValueWhenStopped));

        kmText = (TextView) myView.findViewById(R.id.id_km_text);
        kmText.setText(String.valueOf(kmValueWhenStopped));
        btnStart = (Button) myView.findViewById(R.id.big_start_button);
        btnStart.setOnClickListener(this);

        btnStop = (Button) myView.findViewById(R.id.stop_button);
        btnStop.setOnClickListener(this);
        btnStop.setEnabled(false);

        btnPause = (Button) myView.findViewById(R.id.pause_button);
        btnPause.setOnClickListener(this);
        btnPause.setEnabled(false);

        btnReStart = (Button) myView.findViewById(R.id.restart_button);
        btnReStart.setOnClickListener(this);
        btnReStart.setEnabled(false);

        btnLock = (IconicsButton) myView.findViewById(R.id.lock_button);
        btnLock.setOnClickListener(this);
        init();

        return myView;
    }


    public void init() {
        myChronometer.setText("00:00:00");
        myChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
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
        myChronometer.setBase(SystemClock.elapsedRealtime());

    }


    public void start() {
        myChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        myChronometer.start();
    }


    public void stop() {
        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.stop();
        timeWhenStopped = 0;
        timeWhenStopped = myChronometer.getBase() - SystemClock.elapsedRealtime();
        myChronometer.setText("00:00:00");

    }

    public void pause() {
        timeWhenStopped = myChronometer.getBase() - SystemClock.elapsedRealtime();
        myChronometer.stop();
    }

    public void restart() {
        myChronometer.setBase(SystemClock.elapsedRealtime()
                + timeWhenStopped);
        myChronometer.start();
    }

    public long getCurrentTime() {
        return timeWhenStopped;
    }

    public void setCurrentTime(long time) {
        timeWhenStopped = time;
        myChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
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
        kmText.setText("" + 0);
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

    private void doFirstStart() {
        initChrono();
        kmText.setText(String.valueOf(kmValueWhenStopped));
        chronometer.setBase(SystemClock.elapsedRealtime()
                + timeWhenStopped);
        chronometer.start();
        startLocationTracker(getActivity());

        mHandler.postDelayed(updateTask, 2000);

        btnLock.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.VISIBLE);
        btnPause.setEnabled(false);
        btnStop.setVisibility(View.VISIBLE);
        btnStop.setEnabled(false);
        btnStart.setVisibility(View.GONE);
        btnReStart.setVisibility(View.GONE);
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
                        .setTimeBetweenUpdates(3*1000);
//        update every 100 mt
//                        .setMetersBetweenUpdates(100);

        LocationTracker tracker = new LocationTracker(context, settings) {

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
        tracker.startListening();
    }

    private void stopListeningLocation() {
        if (locationTracker != null && locationTracker.isListening()) {
            locationTracker.stopListening();
        }
    }

    private void initChrono() {
        chronometer = (Chronometer) myView.findViewById(R.id.mychronometer);
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
}