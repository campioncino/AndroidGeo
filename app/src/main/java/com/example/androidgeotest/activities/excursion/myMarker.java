package com.example.androidgeotest.activities.excursion;

/**
 * Created by r.sciamanna on 30/06/2016.
 */


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class myMarker implements ClusterItem {
    public final String name;
    public final int profilePhoto;
    private final LatLng mPosition;

    public myMarker(LatLng position, String name, int pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}