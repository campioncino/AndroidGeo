package com.example.androidgeotest.activities.Util;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roberto on 03/07/16.
 */

public class MapUtils {

    public void draw(GoogleMap mGoogleMap,List<Location> locationList,PolylineOptions polylineOptions){

        polylineOptions = new PolylineOptions();
        for(Location loc : locationList){
            polylineOptions.add(new LatLng(loc.getLatitude(),loc.getLongitude()));
            Log.wtf("polyline", loc.toString());
        }
        polylineOptions.width(5).color(Color.BLUE);
        mGoogleMap.addPolyline(polylineOptions);
    }

    public void drawPrimaryLinePath(GoogleMap mGoogleMap, List<Location> listLocsToDraw )
    {
        if ( mGoogleMap == null )
        {
            return;
        }

        if ( listLocsToDraw.size() < 2 )
        {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        options.color( Color.parseColor( "#CC0000FF" ) );
        options.width( 5 );
        options.visible( true );

        for ( Location locRecorded : listLocsToDraw )
        {
            options.add( new LatLng( locRecorded.getLatitude(),
                    locRecorded.getLongitude() ) );
        }

        mGoogleMap.addPolyline( options );

    }

    public static float calculateBearing(Location l1,Location l2){
       return l1.bearingTo(l2);
    }

}
