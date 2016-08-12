package com.example.androidgeotest.activities.UtilityMapFunction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.example.androidgeotest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by r.sciamanna on 11/08/2016.
 */

public final class UtilityMap {

    public static GoogleMap disegna(Marker from, Location to, GoogleMap map, PolylineOptions polylineOptions){
        polylineOptions = new PolylineOptions();
        polylineOptions.add(from.getPosition());
        polylineOptions.add(new LatLng(to.getLatitude(), to.getLongitude()));
        polylineOptions.width(5).color(0x7F0000FF);
        map.addPolyline(polylineOptions);
        return map;
    }

    public static GoogleMap disegnaVerso(Context context,Marker from, Location to, GoogleMap map, PolylineOptions polylineOptions){
        double bearing = SphericalUtil.computeHeading(from.getPosition(),new LatLng(to.getLatitude(),to.getLongitude()));

        IconicsDrawable ic1 = new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_keyboard_arrow_up).color(Color
                .CYAN).sizeDp(22);
        Drawable arrow = ic1;
//        Drawable arrow = context.getResources().getDrawable(R.drawable.map_marker_plus);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(arrow.getIntrinsicWidth(), arrow.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        arrow.setBounds(-1, -1, arrow.getIntrinsicWidth(), arrow.getIntrinsicHeight());
        arrow.draw(canvas);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(41.906991, 12.453360))
//                .title("My Marker")
//                .icon(bd)
//        );

        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(to.getLatitude(),to.getLongitude()))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .icon(bd)
                .rotation((float) bearing);
        map.addMarker(marker);

        disegna(from,to,map,polylineOptions);
        return map;

    }
}
