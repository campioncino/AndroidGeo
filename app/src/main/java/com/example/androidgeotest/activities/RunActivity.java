package com.example.androidgeotest.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Chronometer.MyChronometer;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

/**
 * Created by r.sciamanna on 03/05/2016.
 */
public class RunActivity extends AppCompatActivity {

    private final String TAG = "RunActivity";
    private Handler mHandler = new Handler();
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReStart;

    private IconicsButton btnLock;
    private Chronometer chronometer;

    long timeWhenStopped = 0;
    double kmValueWhenStopped = 0.000;
    double kcalValueWhenStopped = 0.000;

    private Menu menu;
    private Toolbar toolbar;

    private ActionBar actionBar;
    private TextView kcalText;
    private TextView kmText;

    private EditText myChronometer;

    private LocationTracker locationTracker;

    private static List<Location> locationList = new ArrayList<Location>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.running_activity_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Nuova Attività");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        menu = toolbar.getMenu();

        Fragment mapfragment = new MapFragment();
        FragmentManager mapfragmentManager = getSupportFragmentManager();
        FragmentTransaction mapfragmentTransaction = mapfragmentManager
                .beginTransaction().replace(R.id.mymap, mapfragment);
        mapfragmentTransaction.commit();



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


}
