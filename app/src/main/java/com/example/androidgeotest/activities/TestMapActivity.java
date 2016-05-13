package com.example.androidgeotest.activities;

import android.location.Location;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Util.GPSSender;
import com.example.androidgeotest.activities.Util.MyApplication;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TestMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener, com.google.android.gms.location.LocationListener {

    private final String TAG = TestMapActivity.class.getSimpleName();
    private GPSSender gpsSender;
    public static final LatLng TERAMOLATLNG = new LatLng(42.661143, 13.698664);

    public static final CameraPosition TERAMO =
            new CameraPosition.Builder().target(TERAMOLATLNG)
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();

    private GoogleMap mMap;
    private CoordinatorLayout corCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);

        corCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gpsSender = new GPSSender(this);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, GoogleMap googleMap) {
        googleMap.moveCamera(update);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TERAMOLATLNG, 10));
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnCameraChangeListener(this);

    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            MyApplication.showErrorSnackbar(corCoordinatorLayout, "Map not ready");
            return false;
        }
        return true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
//        CameraPosition currentCameraPosition = mMap.getCameraPosition();
        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update) {

        mMap.moveCamera(update);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.wtf(TAG, "Click position = " + latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.wtf(TAG, "Click position = " + latLng);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
        Log.wtf(TAG, "Location is changed in " + location.toString());
    }
}
