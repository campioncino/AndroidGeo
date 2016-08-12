package com.example.androidgeotest.activities.excursion;

/**
 * Created by r.sciamanna on 30/06/2016.
 */


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

public class MyMarker extends Location{
    private  String description;

    private int progressiveValue;

//
//    public MyMarker(String provider) {
//        super(provider);
//    }
//
//    public MyMarker(Location l) {
//        super(l);
//    }

    public MyMarker(Location l, int val) {
        super(l);
        this.progressiveValue=val;
    }
    public MyMarker(Location l, int val, String description) {
        super(l);
        this.progressiveValue=val;
        this.description = description;
    }

    public int getProgressiveValue() {
        return progressiveValue;
    }
}