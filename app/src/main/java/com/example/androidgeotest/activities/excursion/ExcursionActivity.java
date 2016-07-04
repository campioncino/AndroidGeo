package com.example.androidgeotest.activities.excursion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Code;
import com.example.androidgeotest.activities.Util.MapUtils;
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
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;
import com.hoan.dsensor_master.DProcessedSensor;
import com.hoan.dsensor_master.DProcessedSensorEvent;
import com.hoan.dsensor_master.DSensor;
import com.hoan.dsensor_master.DSensorEvent;
import com.hoan.dsensor_master.DSensorManager;
import com.hoan.dsensor_master.interfaces.DProcessedEventListener;
import com.hoan.dsensor_master.interfaces.DSensorEventListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by r.sciamanna on 29/06/2016.
 */
public class ExcursionActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener,DProcessedEventListener {

    private FreezerRace myrace;
    private Fragment mapFragment;
    final static String TAG = ExcursionActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReStart;
    private Button btnBearing;
    private boolean amIrunning = false;
    private IconicsButton btnLock;
    private TextView textView;

    double kmValueWhenStopped = 0.000;
    double kcalValueWhenStopped = 0.000;
    private LocationTracker locationTracker;
    private Location mylocation;
    private static List<Marker> markers = new ArrayList<>();
    private CameraPosition cameraPosition;
    private static List<Location> locationList = new ArrayList<Location>();
    private static List<FreezerLocation> freezerocationList = new ArrayList<FreezerLocation>();
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    public static int count;
    private PolylineOptions polylineOptions;

    private Bundle mbundle;
    private int viewId;
    private TextView altitude;
    private TextView latText;
    private TextView lonText;
    private View myExcursionPanel;

    private ImageView nav;

    /***sensor value */
    public static final String DPROCESSEDSENSOR_TYPE = "DProcessedSensorType";
    private int angle;

    private TextView mCompassValueTextView;
    private TextView mDepreciatedOrientationValueTextView;

    private int mDProcessedSensorType;
    FreezerRaceEntityManager fRaceEm = new FreezerRaceEntityManager();

    public DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count = 0;
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
        collapsingToolbar.setTitle("Escursione");
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedAppbar);

        //setto l'appbar collapsed, così vedo una sola riga, mantenendo il dettaglio se scrollo
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.setExpanded(false);


        btnStart = (Button) findViewById(R.id.big_start_button);
        btnStart.setOnClickListener(this);

        btnBearing =(Button) findViewById(R.id.bearing_button);
        btnBearing.setOnClickListener(this);

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

        altitude = (TextView) findViewById(R.id.id_altitude);
        latText = (TextView) findViewById(R.id.id_lat_text);
        lonText = (TextView) findViewById(R.id.id_lon_text);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mFragment.getMapAsync(this);
        initSensor();
        nav =(ImageView) findViewById(R.id.navigation_icon);
    }

    /************
     * CHRONO
     ****************/


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.big_start_button:
                doPickPoint();
                break;
            case R.id.bearing_button:
                draw(locationList);
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
        Log.wtf(TAG, "sono in pickpoint");

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            locationList.add(mLastLocation);

            placeMArker(this, count, latLng, mGoogleMap);


            count++;
        }
    }

    private void placeMArker(Context context, int count, LatLng latLng, GoogleMap mGoogleMap) {

        IconGenerator iconFactory = new IconGenerator(this);

//        iconFactory.setRotation(0);
//        iconFactory.setContentRotation(0);
        if (count == 0) {
            iconFactory.setStyle(IconGenerator.STYLE_BLUE);
//            iconFactory.setBackground(new IconicsDrawable(this)
//                    .icon(GoogleMaterial.Icon.gmd_directions_car)
//                    .color(Color.BLUE)
//                    .sizeDp(24));

            addIcon(iconFactory, "P", latLng, mGoogleMap);
//            placeMArker(context,count,latLng,mGoogleMap);
        } else {
            iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
            addIcon(iconFactory, "" + count, latLng, mGoogleMap);
        }
        mGoogleMap.setOnMarkerClickListener(this);


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
                Log.wtf(TAG, "home");
                mGoogleMap.setMyLocationEnabled(true);
                super.onBackPressed();
                break;
            default:
                Log.wtf(TAG, "default");
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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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

        mGoogleMap.setOnMarkerClickListener(this);

        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);

        buildGoogleApiClient();

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
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
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(16).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); //30 seconds
        mLocationRequest.setFastestInterval(10000); //10 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

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
        mylocation=location;
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

        /* aggiorno i dati */
        altitude.setText(String.valueOf(location.getAltitude()));
        latText.setText(String.valueOf(location.getLatitude()));
        lonText.setText(String.valueOf(location.getLongitude()));
        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
//        if(location!=null && locationList.size()>0){
//             cameraPosition = new CameraPosition.Builder()
//                    .target(latLng).zoom(16).bearing(location.bearingTo(locationList.get(0))).build();
//        }
//        else{
//             cameraPosition = new CameraPosition.Builder()
//                    .target(latLng).zoom(16).build();
//        }
        cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(16).build();
        //zoom to current position:
        FreezerLocation tmp = new FreezerLocation(location);
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        if(amIrunning) {
            Log.wtf("On locationChanged latLng", latLng.toString());
            locationList.add(location);
            freezerocationList.add(tmp);
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


    public void draw(List<Location> locationList) {
//        mGoogleMap.clear();
        polylineOptions = new PolylineOptions();
        for (Location loc : locationList) {
            polylineOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            Log.wtf("polyline", loc.toString());
        }
        polylineOptions.width(5).color(Color.BLUE);
        mGoogleMap.addPolyline(polylineOptions);

    }

    public void onInfoWindowClick(final Marker marker) {


        final String title = marker.getTitle();
        Log.wtf(TAG,"marker pressed");
        marker.hideInfoWindow();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       Log.wtf(TAG, "marker clicked" + marker.getTitle());
        return true;
    }


    /************ END MAPS *************/


    /***********
     * MAPS EXTENSION
     **********/
    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position, GoogleMap map) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                .title(position.latitude + "," + position.longitude);

        map.addMarker(markerOptions);
    }

    private CharSequence makeCharSequence(String text) {
        String prefix = "Mixing ";
        String suffix = "different fonts";
        String sequence = prefix + suffix;
        SpannableStringBuilder ssb = new SpannableStringBuilder(sequence);
        ssb.setSpan(new StyleSpan(ITALIC), 0, prefix.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(BOLD), prefix.length(), sequence.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    /***********
     * END MAPS EXTENSION
     *******/

//    /*****************
//     * utils
//     *************/
//    public void drawMarker(Context context, TextView tv, GoogleMap map, int count, LatLng position) {
//        IconGenerator factory = new IconGenerator(context);
//        factory.setBackground(new IconicsDrawable(this)
//                .icon(GoogleMaterial.Icon.gmd_directions_car)
//                .color(Color.BLUE)
//                .sizeDp(24));
//        tv.setText(count);
//        tv.setTextColor(Color.CYAN);
//        factory.setContentView(tv);
//        Bitmap icon = factory.makeIcon();
//        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(position).
//                anchor(factory.getAnchorU(), factory.getAnchorV()));
//    }
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        /** TODO Inserire dialog con informazioni e opzione cancella punto **/
//        Log.wtf(TAG, "marker clicked" + marker.getTitle());
//        return true;
//    }

    @Override
    public void onDestroy() {
        SupportMapFragment f = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        Log.wtf(TAG,"onDestry");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        if (f.isResumed()) {
            Log.wtf(TAG,"is resumed");
        }
        super.onDestroy();
    }

    public void initSensor(){
        Intent intent = getIntent();
        mDProcessedSensorType = intent == null ? DProcessedSensor.TYPE_3D_COMPASS
                : intent.getIntExtra(DPROCESSEDSENSOR_TYPE, DProcessedSensor.TYPE_3D_COMPASS);
        if (mDProcessedSensorType == DProcessedSensor.TYPE_COMPASS_FLAT_ONLY_AND_DEPRECIATED_ORIENTATION
                || mDProcessedSensorType == DProcessedSensor.TYPE_3D_COMPASS_AND_DEPRECIATED_ORIENTATION) {
            mDepreciatedOrientationValueTextView = (TextView) findViewById(R.id.textview_orientation_value);
            TextView depreciatedOrientationTextView = (TextView) findViewById(R.id.textview_orientation);
            mDepreciatedOrientationValueTextView.setVisibility(View.VISIBLE);
            depreciatedOrientationTextView.setVisibility(View.VISIBLE);
        }
    }

    public void onProcessedValueChanged(DSensorEvent dSensorEvent) {
        float p=0;
        if (dSensorEvent.sensorType == DSensor.TYPE_DEVICE_MAGNETIC_FIELD) {
            mDepreciatedOrientationValueTextView.setText(String.valueOf(Math.round(dSensorEvent.values[0])));
        } else {
            if (Float.isNaN(dSensorEvent.values[0])) {
                mCompassValueTextView.setText("Device is not flat no compass value");
            } else {
                int valueInDegree = (int) Math.round(Math.toDegrees(dSensorEvent.values[0]));
                if (valueInDegree < 0) {
                    valueInDegree = (valueInDegree + 360) % 360;
                }
                //mCompassValueTextView.setText(String.valueOf(valueInDegree));
                angle= valueInDegree;
//                cameraPosition = new CameraPosition.Builder()
//                        .target(latLng).zoom(16).bearing(angle).build();
//                mGoogleMap.animateCamera(CameraUpdateFactory
//                        .newCameraPosition(cameraPosition));
                Log.wtf(TAG, "OnProcessValueChange");
                rotateImg();
            }
        }
    }

    public void rotateImg(){
        float toBase=0;
        if(mylocation!=null && locationList.size()>0){
            toBase=mylocation.bearingTo(locationList.get(0));
            Log.wtf("bearing",String.valueOf(mylocation.bearingTo(locationList.get(0))));
        }
        nav.setRotation(angle+toBase);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int flag = DSensorManager.startDProcessedSensor(this, mDProcessedSensorType, this);
        if ((flag & DSensorManager.TYPE_MAGNETIC_FIELD_NOT_AVAILABLE) != 0) {
           // mCompassValueTextView.setText("error_no_magnetic_field_sensor");
        } else if ((flag & DSensorManager.TYPE_GRAVITY_NOT_AVAILABLE) != 0
                && (flag & DSensorManager.TYPE_ACCELEROMETER_NOT_AVAILABLE) != 0) {
          //  mCompassValueTextView.setText("error_no_accelerometer_sensor");
        }
    }
}
