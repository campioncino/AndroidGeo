package com.example.androidgeotest.activities.excursion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Code;
import com.example.androidgeotest.activities.business.model.FreezerLocation;
import com.example.androidgeotest.activities.business.model.FreezerRace;
import com.example.androidgeotest.activities.business.model.FreezerRaceEntityManager;
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
import com.mikepenz.iconics.view.IconicsButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;

/**
 * Created by r.sciamanna on 29/06/2016.
 */
public class ExcursionActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FreezerRace myrace;
    private Fragment mapFragment;
    final static String TAG = ExcursionActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReStart;
    private boolean amIrunning = false;
    private IconicsButton btnLock;

    double kmValueWhenStopped = 0.000;
    double kcalValueWhenStopped = 0.000;
    private LocationTracker locationTracker;
    private Location location;
    private static List<Marker> markers = new ArrayList<>();

    private static List<Location> locationList = new ArrayList<Location>();
    private static List<FreezerLocation> freezerocationList = new ArrayList<FreezerLocation>();
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    public static int count=1;
    private PolylineOptions polylineOptions;

    private Bundle mbundle;
    private int viewId;

    private View myExcursionPanel;

    FreezerRaceEntityManager fRaceEm = new FreezerRaceEntityManager();

    public DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* mi setto sul mio firebase db */
        rootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Code.FIREBASE_DB);

        setContentView(R.layout.excursion_layout_activity);
//        AndroidBug5497Workaround.assistActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myExcursionPanel = findViewById(R.id.myExcursionPanelLayout);

//        mapFragment = new MapFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.map_fragment, mapFragment).commit();
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Attività");
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedAppbar);

        //setto l'appbar collapsed, così vedo una sola riga, mantenendo il dettaglio se scrollo
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.setExpanded(false);


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

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mFragment.getMapAsync(this);

    }

    /************
     * CHRONO
     ****************/


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.big_start_button:
                doPickPoint();
                break;
            case R.id.lock_button:
                Log.wtf(TAG, "" + view.getId());
                break;
            case R.id.pause_button:
                Log.wtf(TAG, "" + view.getId());
                break;
            case R.id.restart_button:
                Log.wtf(TAG, "" + view.getId());
                break;
            case R.id.stop_button:
                Log.wtf(TAG, "" + view.getId());
                break;
        }
    }

    private void doPickPoint() {
        Log.wtf(TAG,"sono in pickpoint");
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            Bitmap bmp = Bitmap.createBitmap(200, 50, conf);
//            Canvas canvas = new Canvas(bmp);
//
//            Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
//
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.MAGENTA);
//            paint.setTypeface(tf);
//            paint.setTextAlign(Paint.Align.CENTER);
//            paint.setTextSize(20);
//
//
//            canvas.drawText(String.valueOf(count), 0, 50, paint); // paint defines the text color, stroke width, size
//            mGoogleMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
//                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
//                    .anchor(0.5f, 1)
//            );



            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Last Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            markerOptions.icon(R.drawable.common_plus_signin_btn_icon_light);

            mGoogleMap.addMarker(markerOptions);
            Marker lastMarker =  mGoogleMap.addMarker(markerOptions);
//            mGoogleMap.setOnMarkerClickListener(this);
//            if(onMarkerClick(currLocationMarker)){
//                Log.wtf(TAG,"hai cliccato sul marker"+currLocationMarker.getTitle());
//            }
            count++;
        }
    }

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
        FreezerLocation tmp = new FreezerLocation(location);
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        if(amIrunning) {
            Log.wtf("On locationChanged latLng", latLng.toString());
            locationList.add(location);
            freezerocationList.add(tmp);
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

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        Log.wtf(TAG,"marker"+marker.getTitle());
//        return true;
//    }


    /************ END MAPS *************/
}
