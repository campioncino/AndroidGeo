package com.example.androidgeotest.activities.excursion;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Code;
import com.example.androidgeotest.activities.Util.MapUtils;
import com.example.androidgeotest.activities.Util.MyApplication;
import com.example.androidgeotest.activities.Util.MyDialog;
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
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.sql.SQLOutput;
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
public class ExcursionActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

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
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mFragment;
    private Marker currLocationMarker;

    public static int count;
    private PolylineOptions polylineOptions;

    private Bundle mbundle;
    private int viewId;
    private TextView altitude;
    private TextView latText;
    private TextView lonText;
    private View myExcursionPanel;

    private FloatingActionButton fab;

    private CoordinatorLayout coordinatorLayout;

    private NavigationView navigationView;
    public Drawer drawer = null;

    public PrimaryDrawerItem itemUno = null;
    public PrimaryDrawerItem itemDue = null;
    public PrimaryDrawerItem itemTre = null;
    /***
     * sensor value
     */
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

//        setContentView(R.layout.excursion_layout_activity);
//        AndroidBug5497Workaround.assistActivity(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//
//        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.excursion_layout_activity_2);
        initToolbar();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        myExcursionPanel = findViewById(R.id.myExcursionPanelLayout);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        initDrawer(savedInstanceState);
//        mapFragment = new MapFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.map_fragment, mapFragment).commit();
//        CollapsingToolbarLayout collapsingToolbar =
//                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle("Escursione");
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedAppbar);

        //setto l'appbar collapsed, così vedo una sola riga, mantenendo il dettaglio se scrollo
//        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
//        appbar.setExpanded(false);

//        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//        params.setScrollFlags(0);  // clear all scroll flags

//
//        btnStart = (Button) findViewById(R.id.big_start_button);
//        btnStart.setOnClickListener(this);
//
//        btnStop = (Button) findViewById(R.id.stop_button);
//        btnStop.setOnClickListener(this);
//        btnStop.setEnabled(false);
//
//        btnPause = (Button) findViewById(R.id.pause_button);
//        btnPause.setOnClickListener(this);
//        btnPause.setEnabled(false);
//
//        btnReStart = (Button) findViewById(R.id.restart_button);
//        btnReStart.setOnClickListener(this);
//        btnReStart.setEnabled(false);
//
//        btnLock = (IconicsButton) findViewById(R.id.lock_button);
//        btnLock.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.wtf("fab", "fab pressed");
            }
        });
        altitude = (TextView) findViewById(R.id.id_altitude);
        latText = (TextView) findViewById(R.id.id_lat_text);
        lonText = (TextView) findViewById(R.id.id_lon_text);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mFragment.getMapAsync(this);
    }

    public void initDrawer(Bundle savedInstanceState){
        itemUno = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName("GpsActivity")
                .withIcon(GoogleMaterial.Icon.gmd_directions_run)
                .withIconTintingEnabled(true)
                .withBadgeStyle(new BadgeStyle()
                        .withColorRes(R.color.md_red_700)
                        .withTextColor(ContextCompat.getColor(this, R.color.md_white_1000)));
        itemDue=itemUno;
        itemTre=itemUno;
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar((Toolbar) findViewById(R.id.toolbar))
                .withHasStableIds(true)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("sezione Uno").withEnabled(false),
                        itemUno,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("sezione due").withEnabled(false),
                        itemDue,
                        itemTre

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Log.wtf("Drawer","item1");
                            } else if (drawerItem.getIdentifier() == 2) {
                                Log.wtf("Drawer","item2");
                            } else if (drawerItem.getIdentifier() == 3) {
                                Log.wtf("Drawer","item3");
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withSelectedItem(1)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    public void onDrawerOpened(View drawerView) {
                    }

                    public void onDrawerClosed(View drawerView) {

                    }

                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .build();
        drawer.openDrawer();

    }


    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeAsUpIndicator(new IconicsDrawable(this)
//                    .icon(GoogleMaterial.Icon.gmd_navigate_before)
//                    .color(Color.RED)
//                    .sizeDp(24));
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_excursion, menu);
        return true;
    }

    /************
     * CHRONO
     ****************/


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.big_start_button:
                doPickPoint();
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
               Log.wtf("HOME","home button pressed");
                return true;
            case R.id.miCompose:
                Log.wtf("HOME","home button pressed");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                Log.wtf(TAG, "home");
//                mGoogleMap.setMyLocationEnabled(true);
//                super.onBackPressed();
//                break;
//            default:
//                Log.wtf(TAG, "default");
//                return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }


    /************
     * MAPS
     ****************/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

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
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(latLng).zoom(16).build();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); //3 seconds
        mLocationRequest.setFastestInterval(5000); //5 seconds
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
        mylocation = location;
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
        currLocationMarker.setDraggable(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

//
        /* aggiorno i dati */
        altitude.setText(String.valueOf(location.getAltitude()));
        latText.setText(String.valueOf(location.getLatitude()));
        lonText.setText(String.valueOf(location.getLongitude()));
        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();

        cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(18).build();
        cameraPosition = new CameraPosition.Builder()
                .target(latLng).build();
        //zoom to current position:
        FreezerLocation tmp = new FreezerLocation(location);
//        mGoogleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));
        if (amIrunning) {
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.wtf(TAG, "marker clicked  " + marker.getTitle());
        showdialog(marker);
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

        map.addMarker(markerOptions).setDraggable(true);
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


    @Override
    public void onDestroy() {
        SupportMapFragment f = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        Log.wtf(TAG, "onDestry");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (f.isResumed()) {
            Log.wtf(TAG, "is resumed");
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showMyDialog(Marker marker) {
        final Marker myMark = marker;
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorlayout);
        MyDialog.Builder builder = new MyDialog.Builder(this);
        builder.show();
    }


    public void showdialog(final Marker marker) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        final EditText text1 = (EditText) view.findViewById(R.id.text1);
        final TextView lat = (TextView) view.findViewById(R.id.latTex);
        final TextView lon = (TextView) view.findViewById(R.id.lonTex);

        lat.setText("Lat " + marker.getPosition().latitude);
        lon.setText("Lon " + marker.getPosition().longitude);
        if (!marker.getTitle().toString().isEmpty()) {
            text1.setText(marker.getTitle().toString());
        }

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorlayout);
        final Marker mymarker = marker;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Elimina", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                count--;
                if (count < 0) {
                    count = 0;
                }
                marker.remove();
                marker.setVisible(false);
            }
        });

        builder.setNeutralButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle("DIALOG");
        final AlertDialog dialog = builder.create();
        dialog.show();


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String nomePdi = text1.getText().toString();
                if (nomePdi.isEmpty()) {
                    MyApplication.showErrorSnackbar(coordinatorLayout, "porc");
                } else {
                    marker.setTitle(nomePdi);
                    dialog.dismiss();
//                    count--;


                }
            }
        });

    }

}
