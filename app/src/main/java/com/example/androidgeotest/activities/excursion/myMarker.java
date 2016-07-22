package com.example.androidgeotest.activities.excursion;

/**
 * Created by r.sciamanna on 30/06/2016.
 */


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyMarker {
    private final String description;
    private final Location loc;
    private int progressiveValue;

    public MyMarker(String description, Location loc, Integer val) {
        this.description = description;
        this.loc = loc;
        this.progressiveValue=val;
    }

    public MyMarker(Location loc, Integer val) {
        this.description = "";
        this.loc = loc;
        this.progressiveValue=val;
    }

    public String getDescription() {
        return description;
    }

    public Location getLoc() {
        return loc;
    }

    public int getProgressiveValue() {
        return progressiveValue;
    }

    public void setProgressiveValue(int progressiveValue) {
        this.progressiveValue = progressiveValue;
    }
}