package com.example.androidgeotest.activities.Util;
/**
 * Created by r.sciamanna on 13/05/2016.
 */
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import android.location.LocationListener;

import com.google.android.gms.location.LocationServices;


public class GPSSender {
    static Context ctx;
    static LocationManager mlocationManager;
    static Location mLocation;
    static double longitude;
    static double latitude;
    static LocationListener locationListener;

    public GPSSender(Context ctx) {
        this.ctx = ctx;
        mlocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.i("LocationListener","Location changed!");
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.i("LocationListener","Longitude ="+longitude+ "- Latitudde = "+latitude);
                Location loc =getLastBestLocation();
                Log.wtf("Location","loc ="+loc.toString());

            }


            public void onStatusChanged(String provider, int status, Bundle extras) {
            }


            public void onProviderEnabled(String provider) {
            }


            public void onProviderDisabled(String provider) {
            }
        };


        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  locationListener);
    }

    static public void printLocation() {
        Log.i("TEST", "lat:"+latitude+" lon:"+longitude);
    }

    public Location getLastBestLocation() {
        Location locationGPS = mlocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mlocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {

            return locationGPS;
        }
        else {

            return locationNet;
        }
    }
}