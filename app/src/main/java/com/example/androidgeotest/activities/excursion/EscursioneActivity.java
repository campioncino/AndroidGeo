package com.example.androidgeotest.activities.excursion;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Code;
import com.example.androidgeotest.activities.DetailFragment;
import com.example.androidgeotest.activities.Util.Logga;
import com.example.androidgeotest.activities.Util.MapUtils;
import com.example.androidgeotest.activities.Util.Utility;
import com.example.androidgeotest.activities.UtilityMapFunction.UtilityMap;
import com.example.androidgeotest.activities.business.model.FreezerLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by r.sciamanna on 09/08/2016.
 */

public class EscursioneActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener {


    private final String TAG = this.getClass().getSimpleName();
    private int count;

    private static final int MENU_ADD = 100;
    private static final int MENU_CLEAR = 101;

    public DatabaseReference rootRef;

    public EscursioneActivity activity;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;

    private TextView altitude;
    private TextView latText;
    private TextView lonText;
    private View myExcursionPanel;

    private DetailFragment detailFragment;
    private NavigationView navigationView;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private CameraPosition cameraPosition;
    /*TODO refactoring in mapFragment*/
    private SupportMapFragment mFragment;

    /* è il marker del punto attuale, quello che si sposta */
    private Marker currLocationMarker;
    /* lista di tutti i punti */
    private static List<Location> locationsList = new ArrayList<Location>();
    private static List<Marker> markersList = new ArrayList<>();

    private PolylineOptions polylineOptions;
    private Polyline polyline;

    private float zoomLevel = 15;
//    private int zoomLevel = 17;
    private boolean isZooming = false;

    private MyMarker markerExt;

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

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                System.out.println("on panel state changed :" + "new panel state =" + newState.toString() + " | previousState =" + previousState.toString());
                if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    fab.setVisibility(View.GONE);
                }
                if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
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
                aggiungiMarker();
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

        }
    }


    /*************
     * MARKER
     *********************/
    private void aggiungiMarker() {
        Logga.fvd(this, "sono in aggiungiMarker()");
//        String methodName =Thread.currentThread().getStackTrace()[2].getMethodName();
//        Logga.debug(TAG,methodName);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            locationsList.add(mLastLocation);
//            markerList.put(marker,count);
            //
//            mappaMarker.put(currLocationMarker,marker);
            Logga.isIn(this);
            createMarker(this, count, mLastLocation, mGoogleMap);
            count++;
        }
    }

    private void createMarker(Context context, int count, Location lastlocation, GoogleMap mGoogleMap) {
        IconGenerator iconFactory = new IconGenerator(this);
        Logga.isIn(this);
        Marker marker;
        if (count == 0) {
            iconFactory.setStyle(IconGenerator.STYLE_BLUE);
            Logga.fvd(this, " chiamo addIcon(P)");
            marker = addIcon(iconFactory, "P", lastlocation, mGoogleMap);
        } else {
            iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
            Log.i("createMarker", "chiamo addIcon(i)");
            marker = addIcon(iconFactory, "" + count, lastlocation, mGoogleMap);
        }
        markersList.add(marker);
        mGoogleMap.setOnMarkerClickListener(this);
    }

    private Marker addIcon(IconGenerator iconFactory, CharSequence text, Location position, GoogleMap map) {
        Log.i("addIcon", "sono in addIcon");
        LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(latLng).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
//                .title(position.getLatitude() + "," + position.getLongitude()+" altezza "+position.getAltitude())
                .title("" + count)
                .draggable(true);

        return map.addMarker(markerOptions);
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

        mGoogleMap.setOnCameraChangeListener(getCameraChangeListener());
        buildGoogleApiClient();

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleApiClient.connect();


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
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(latLng).zoom(zoomLevel).build();
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(latLng).build();


//            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoomLevel).build();
//            mGoogleMap.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(cameraPosition));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(latLng);
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
            mGoogleMap.moveCamera(center);

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
        Location mylocation = location;
        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                .target(latLng).zoom(zoomLevel).build();
//        cameraPosition = new CameraPosition.Builder()
//                .target(latLng).build() ;
        //zoom to current position:
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        FreezerLocation tmp = new FreezerLocation(location);
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }


    @Override
    public void onCameraChange(CameraPosition position) {
        Logga.isIn(this);
        if(zoomLevel != position.zoom)
        {
            isZooming = true;
        }

        zoomLevel = position.zoom;
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener()
    {
        Logga.isIn(this);
        return new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition position)
            {
                Log.d("Zoom", "Zoom: " + position.zoom);

                if(zoomLevel != position.zoom)
                {
                    isZooming = true;
                }

                zoomLevel = position.zoom;
            }
        };
    }

    public void disegna(Location location){
//        polyline=UtilityMap.disegna(currLocationMarker,location,mGoogleMap,polylineOptions);
        mGoogleMap=UtilityMap.disegnaVerso(this,currLocationMarker,location,mGoogleMap,polylineOptions);

    }


    public void draw(List<Location> locationList) {
//        mGoogleMap.clear();
        polylineOptions = new PolylineOptions();
        for (Location loc : locationList) {
            polylineOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            Log.i("polyline", loc.toString());
        }
        polylineOptions.width(5).color(Color.BLUE);
        polyline = mGoogleMap.addPolyline(polylineOptions);

//        mGoogleMap.addPolyline(polylineOptions);

    }

    public void clearDraw(Polyline pol) {
        pol.remove();
    }

    public DetailFragment getDetailFragment() {
        return detailFragment;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Logga.avd(this, marker.getTitle());
        System.out.println(marker.getTitle());
        final Iterator iterator;
        int index = -1;
        for (int i = 0; i < markersList.size(); i++) {
            if(markersList.get(i).getTitle().equals(marker.getTitle())){
                index=i;
            }
        }
//        Logga.avd(this,""+locationsList.get(index).getLatitude());
        if(index>-1){
            getDetailFragment().showDetail(locationsList.get(index));
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }

//        showdialog(marker);
        return true;
    }

    /************
     * END MAPS
     *************/


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

    @Override
    public void onClick(View view) {
        Logga.avd(this,""+view.getId());
//        switch (view.getId()) {
//            case R.id.big_start_button:
//                doPickPoint();
//                break;
//            case R.id.pause_button:
//                Log.i(TAG, "" + view.getId());
//                break;
//            case R.id.restart_button:
//                Log.i(TAG, "" + view.getId());
//                break;
//            case R.id.stop_button:
//                Log.i(TAG, "" + view.getId());
//                break;
//        }
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
}
