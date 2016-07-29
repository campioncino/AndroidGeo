package com.example.androidgeotest.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.excursion.ExcursionActivity;
import com.example.androidgeotest.activities.excursion.ExcursionActivity2;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by r.sciamanna on 29/07/2016.
 */

public class DetailFragment extends Fragment {
    private TextView name;
    private TextView latitude;
    private TextView longitude;

    private ExcursionActivity2 activity;
    private View view;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.detail, container, false);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (ExcursionActivity2) getActivity();
        view = getView();
    }

    public void add(Marker marker){
        name = (TextView) view.findViewById(R.id.detail_Name);
        latitude = (TextView) view.findViewById(R.id.detail_Lat);
       longitude = (TextView) view.findViewById(R.id.detail_Lon);

        latitude.setText("Lat " + marker.getPosition().latitude);
        longitude.setText("Lon " + marker.getPosition().longitude);
        if (!marker.getTitle().toString().isEmpty()) {
            name.setText(marker.getTitle().toString());
        }
        final Marker mymarker = marker;

    }
}
