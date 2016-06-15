package com.example.androidgeotest.activities.running;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.business.CrudException;
import com.example.androidgeotest.activities.business.RaceService;
import com.example.androidgeotest.activities.business.model.Race;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mikepenz.iconics.view.IconicsButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;

/**
 * Created by r.sciamanna on 11/05/2016.
 */


public class RunningActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private Fragment mapFragment;
    final static String TAG = RunningActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReStart;
    private boolean amIrunning = false;
    private IconicsButton btnLock;
    private Chronometer chronometer;

    private Race myRace;

    double kmValueWhenStopped = 0.000;
    double kcalValueWhenStopped = 0.000;
    private LocationTracker locationTracker;
    private Location location;

    private static List<Location> locationList = new ArrayList<Location>();

    private TextView kcalText;
    private TextView kmText;
    private long timeWhenStopped = 0;
    private String text;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    private PolylineOptions polylineOptions;

    private Bundle mbundle;
    private int viewId;

    private View myChronometerLayout;

    public RaceService raceService;


    public DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* mi setto sul mio firebase db */
        rootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://androidgeotest-6915a.firebaseio.com/");

        setContentView(R.layout.running_activity_layout);
//        AndroidBug5497Workaround.assistActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myChronometerLayout = findViewById(R.id.mychronometerLayout);

//        mapFragment = new MapFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.map_fragment, mapFragment).commit();
        raceService = new RaceService(this);
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

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mFragment.getMapAsync(this);

    }

    /************
     * CHRONO
     ****************/

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
        myRace = new Race();
        polylineOptions = new PolylineOptions();
        mGoogleMap.clear();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        try {
            myRace.setStart(dateformat.parse(c.getTime().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        kmText.setText(String.valueOf(kmValueWhenStopped));
        chronometer.setBase(SystemClock.elapsedRealtime()
                + timeWhenStopped);
        chronometer.start();
        locationList.clear();
        amIrunning = true;
        mHandler.postDelayed(updateTask, 2000);

        btnLock.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.VISIBLE);
        btnPause.setEnabled(false);
        btnStop.setVisibility(View.VISIBLE);
        btnStop.setEnabled(false);
        btnStart.setVisibility(View.GONE);
        btnReStart.setVisibility(View.GONE);
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
        amIrunning = false;
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
        amIrunning = true;
        mHandler.postDelayed(updateTask, 5000);
        btnPause.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);
        btnReStart.setVisibility(View.GONE);
    }

    private void doStop() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        amIrunning = false;
        timeWhenStopped = 0;
        kmValueWhenStopped = 0;
        chronometer.setText("00:00:00");
        kmText.setText("" + 0.0);
        mHandler.removeCallbacks(updateTask);
        btnPause.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
        btnReStart.setVisibility(View.GONE);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        try {
            myRace.setStop(dateformat.parse(c.getTime().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!locationList.isEmpty()) {
            Log.wtf("locationList", "elementi " + locationList.size());
            draw(locationList);
            setFinish(locationList,myRace);
            try {
                raceService.insert(myRace);
            } catch (CrudException e) {
                e.printStackTrace();
            }
        }
    }


    public void setFinish(List<Location> locations, Race race){
        race.setTrip(new Gson().toJson(locations));
        race.setTotalDuration(
                locations.get(locations.size()-1).getElapsedRealtimeNanos()-locations.get(0).getElapsedRealtimeNanos());
        race.setTotalDistace(calculateDistance(locations));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Race");

        myRef.setValue(race);
        //rootRef.setValue(race);
    }

    public float calculateDistance(List<Location> locations){
        float distance=0;
        for(int i=0; i<locations.size()-1;i++){
                distance = locations.get(i).distanceTo(locations.get(i + 1));
        }
        return distance;
    }

     /************
     * END CHRONO
     *************/

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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


    /************
     * MAPS
     ****************/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }


    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(16).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        if(amIrunning) {
            Log.wtf("On locationChanged latLng", latLng.toString());
            locationList.add(location);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (amIrunning) {
            locationList.add(location);
            Log.wtf("lastLocation", location.toString());
        }
        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }


    public void draw(List<Location> locationList){

        polylineOptions = new PolylineOptions();
        for(Location loc : locationList){
            polylineOptions.add(new LatLng(loc.getLatitude(),loc.getLongitude()));
            Log.wtf("polyline", loc.toString());
        }
        polylineOptions.width(5).color(Color.BLUE);
        mGoogleMap.addPolyline(polylineOptions);

    }
    /************ END MAPS *************/
}
