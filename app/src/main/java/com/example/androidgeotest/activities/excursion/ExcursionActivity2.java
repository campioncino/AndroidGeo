package com.example.androidgeotest.activities.excursion;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Code;
import com.example.androidgeotest.activities.DetailFragment;
import com.example.androidgeotest.activities.DummyFragment;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.quentinklein.slt.LocationTracker;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;


/**
 * Created by r.sciamanna on 29/06/2016.
 */
public class ExcursionActivity2 extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {
    private static final int MENU_ADD = 100;
    private static final int MENU_CLEAR = 101;
    private FreezerRace myrace;
    private Fragment mapFragment;
    final static String TAG = ExcursionActivity2.class.getSimpleName();
    private Handler mHandler = new Handler();

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
    private Marker selectedMarker;
    private HashMap<MyMarker,Integer> markerList = new HashMap<>();
    private HashMap<Marker, MyMarker> mappaMarker = new HashMap<>();
    private DetailFragment detailFragment;

    public static int count;
    private PolylineOptions polylineOptions;
    private Polyline polyline;

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
    public ExcursionActivity2 activity;

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

    private SlidingUpPanelLayout slidingUpPanelLayout;

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
        setContentView(R.layout.excursion_layout_activity_3);
        initToolbar();
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        slidingUpPanelLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                System.out.println("on panel state changed :"+"new panel state ="+newState.toString()+ " | previousState ="+previousState.toString());
                if(previousState==PanelState.COLLAPSED){
                    fab.setVisibility(View.GONE);
                }
                if(previousState== PanelState.EXPANDED){
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        myExcursionPanel = findViewById(R.id.myExcursionPanelLayout);

        detailFragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_fragment, detailFragment).commit();


        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.map_marker_plus);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.wtf("fab", "fab pressed");
                doPickPoint2();
            }
        });
        altitude = (TextView) findViewById(R.id.id_altitude);
        latText = (TextView) findViewById(R.id.id_lat_text);
        lonText = (TextView) findViewById(R.id.id_lon_text);
        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mFragment.getMapAsync(this);

        activity = this;
    }


    private void initToolbar() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        MenuItem clearMap;
        IconicsDrawable ic1 = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_layers_clear)
                .color(Color.WHITE)
                .sizeDp(22);
        clearMap = menu.add(0, MENU_CLEAR, 0, "clear").setIcon(ic1);
        clearMap.setVisible(true);
//        MenuItem addPlace;
//        addPlace = menu.add(0, MENU_ADD, 0, "Save").setIcon(R.drawable.map_marker_plus);
//        MenuItemCompat.setShowAsAction(addPlace,
//                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setShowAsAction(clearMap,
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);


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

    private void doPickPoint2(){
        Log.wtf(TAG, "sono in pickpoint2");
        Log.wtf("doPickPoint", "sono in pickpoint");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            locationList.add(mLastLocation);
            MyMarker marker = new MyMarker(mLastLocation,count);
            markerList.put(marker,count);
            //

            Log.wtf("doPickPoint", "sono in pickpoint");
            placeMarker(this, count, mLastLocation, mGoogleMap,marker);
            count++;
        }
    }


    private void placeMarker(Context context, int count, Location location, GoogleMap mGoogleMap,MyMarker marker) {

        IconGenerator iconFactory = new IconGenerator(this);
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        Marker placed;
//        iconFactory.setRotation(0);
//        iconFactory.setContentRotation(0);
        if (count == 0) {
            iconFactory.setStyle(IconGenerator.STYLE_BLUE);
//            iconFactory.setBackground(new IconicsDrawable(this)
//                    .icon(GoogleMaterial.Icon.gmd_directions_car)
//                    .color(Color.BLUE)
//                    .sizeDp(24));

            placed=addIcon2(iconFactory, "P", latlng, mGoogleMap);
//            placeMArker(context,count,latLng,mGoogleMap);
        } else {
            iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
            placed=addIcon2(iconFactory, "" + count, latlng, mGoogleMap);
        }
        mappaMarker.put(placed,marker);
        mGoogleMap.setOnMarkerClickListener(this);


    }


    private void doPickPoint() {
        Log.wtf(TAG, "sono in pickpoint");
        Log.wtf("doPickPoint", "sono in pickpoint");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            locationList.add(mLastLocation);
            MyMarker marker = new MyMarker(mLastLocation,count);
            markerList.put(marker,count);
            //
            mappaMarker.put(currLocationMarker,marker);
            Log.wtf("doPickPoint", "sono in pickpoint");
            placeMArker(this, count, latLng, mGoogleMap);
            count++;
        }
    }

    private void clearAll(){
        Log.wtf(TAG,"cancella tutto");
        mGoogleMap.clear();
        count=0;
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
                finish();
                return true;
            case MENU_ADD:
                doPickPoint();
                break;
            case MENU_CLEAR:
                clearAll();
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

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            fab.setVisibility(View.VISIBLE);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

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
        polyline = mGoogleMap.addPolyline(polylineOptions);

//        mGoogleMap.addPolyline(polylineOptions);

    }

    public void clearDraw(Polyline pol){
        pol.remove();
    }

    public DetailFragment getDetailFragment() {
        return detailFragment;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.wtf(TAG, "marker clicked  " + marker.getTitle());
//        multipleChoice(this,marker);

        MyMarker myDataObj = mappaMarker.get(marker);
        getDetailFragment().showDetail(myDataObj);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//        showdialog(marker);
        return true;
    }

    /************ END MAPS *************/


    /***********
     * MAPS EXTENSION
     **********/
    private Marker addIcon2(IconGenerator iconFactory, CharSequence text, LatLng position, GoogleMap map) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                .title(position.latitude + "," + position.longitude)
                .alpha(count)
                .draggable(true);

        return map.addMarker(markerOptions);

    }


    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position, GoogleMap map) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                .title(position.latitude + "," + position.longitude)
                .alpha(count)
                .draggable(true);

        selectedMarker = map.addMarker(markerOptions);

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
        builder.setItems(new CharSequence[]
                        {"Elimina Punto", "button 2", "button 3", "button 4"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                Log.wtf("multiDialog","clicked 1");
                                if (count < 0) {
                                    count = 0;
                                }
                                marker.remove();
                                count=markerList.size();
                                marker.setVisible(false);
                                break;
                            case 1:
                                Log.wtf("multiDialog","clicked 2");
                                break;
                            case 2:
                                Log.wtf("multiDialog","clicked 3");
                                break;
                            case 3:
                                Log.wtf("multiDialog","clicked 4");
                                break;
                        }
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
//        builder.setNegativeButton("Elimina", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//                count--;
//                if (count < 0) {
//                    count = 0;
//                }
//                marker.remove();
//                marker.setVisible(false);
//            }
//        });

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

    public void multipleChoice(Context context,Marker marker){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle("MultiChoice");
        builder.setItems(new CharSequence[]
                        {"button 1", "button 2", "button 3", "button 4"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                Log.wtf("multiDialog","clicked 1");
                                break;
                            case 1:
                                Log.wtf("multiDialog","clicked 2");
                                break;
                            case 2:
                                Log.wtf("multiDialog","clicked 3");
                                break;
                            case 3:
                                Log.wtf("multiDialog","clicked 4");
                                break;
                        }
                    }
                });
        builder.create().show();
    }

}
