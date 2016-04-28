package com.example.androidgeotest.activities;

import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.ProviderError;
import fr.quentinklein.slt.TrackerSettings;

/**
 * Created by r.sciamanna on 27/04/2016.
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;

    private final int[] MAP_TYPES = { GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE };
    private int curMapTypeIndex = 0;


    private LocationTracker locationTracker;
    private static List<Location> locationList = new ArrayList<Location>();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        initListeners();
    }


    private void initListeners() {
        startListeningLocation();
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener( this );
        getMap().setOnMapClickListener(this);
    }


    private void removeListeners() {
        if( getMap() != null ) {
            getMap().setOnMarkerClickListener( null );
            getMap().setOnMapLongClickListener(null);
            getMap().setOnInfoWindowClickListener(null);
            getMap().setOnMapClickListener(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();
    }

    private void initCamera( Location location ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( location.getLatitude(), location.getLongitude() ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        getMap().animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );

        getMap().setMapType( MAP_TYPES[curMapTypeIndex] );
        getMap().setTrafficEnabled( true );
        getMap().setMyLocationEnabled( true );
        getMap().getUiSettings().setZoomControlsEnabled( true );
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = getLastLocation();
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient );
        System.out.println("Current Location \nlat = "+mCurrentLocation.getLatitude()+"\nlon = "+mCurrentLocation.getLongitude());
        initCamera( mCurrentLocation );
    }

    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Create a default location if the Google API Client fails. Placing location at Googleplex
        mCurrentLocation = new Location( "" );
        mCurrentLocation.setLatitude( 37.422535 );
        mCurrentLocation.setLongitude( -122.084804 );
        initCamera(mCurrentLocation);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText( getActivity(), "Clicked on marker", Toast.LENGTH_SHORT ).show();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng( latLng ) );

        options.icon( BitmapDescriptorFactory.defaultMarker( ) );
        getMap().addMarker( options );
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng(latLng) );

        options.icon( BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher ) ) );

        getMap().addMarker(options);
    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getActivity() );

        String address = "";
        try {
            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
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

    private void startListeningLocation() {
        if (locationTracker == null) {

            TrackerSettings settings = new TrackerSettings()
                    .setUseGPS(true)
                    .setUseNetwork(true)
                    .setUsePassive(false)
                    .setTimeBetweenUpdates(5000);

            locationTracker = new LocationTracker(getActivity(), settings) {
                @Override
                public void onLocationFound(Location location) {
                    // Do some stuff when a new GPS Location has been found
                    System.out.println("Location");
                    System.out.println(location.getLatitude() + ", " + location.getLongitude());
                    locationList.add(location);
//                    try {
//                        ObjectMapper mapper = new ObjectMapper();
//                        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept("extras");
//                        FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);
//                        mapper.setFilterProvider(filters);
//                        System.out.println(mapper.writeValueAsString(location));
//                    } catch (JsonProcessingException e) {
////                        e.printStackTrace();
//                    }
                }

                @Override
                public void onTimeout() {
                    System.out.println("Timeout");
                }

                @Override
                public void onProviderError(@NonNull ProviderError providerError) {
//                    System.out.println("PROVIDER ERROR " + providerError.getProvider());
                    super.onProviderError(providerError);
                }


            };
        }
        if (!locationTracker.isListening()) {
            locationTracker.startListening();
        }
    }

    private void stopListeningLocation() {
        if (locationTracker != null && locationTracker.isListening()) {
            locationTracker.stopListening();
        }
    }

}

