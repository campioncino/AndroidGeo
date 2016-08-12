package com.example.androidgeotest.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.example.androidgeotest.activities.Util.Logga;
import com.example.androidgeotest.activities.excursion.EscursioneActivity;
import com.example.androidgeotest.activities.excursion.ExcursionActivity;
import com.example.androidgeotest.activities.excursion.ExcursionActivity2;
import com.example.androidgeotest.activities.excursion.MyMarker;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * Created by r.sciamanna on 29/07/2016.
 */

public class DetailFragment extends Fragment {
    private TextView name;
    private TextView latitude;
    private TextView longitude;
    private TextView altitude;

    private Toolbar toolbar;
    private EscursioneActivity activity;
    private View view;

    public Button btnElimina;

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
        activity = (EscursioneActivity) getActivity();
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar_detail);
        toolbar.setTitle("Dettaglio Posizione");
        view = getView();
        latitude = (TextView) view.findViewById(R.id.id_lat_text);
        longitude = (TextView) view.findViewById(R.id.id_lon_text);
        altitude = (TextView) view.findViewById(R.id.id_altitude);
        btnElimina=(Button) view.findViewById(R.id.detail_delete_btn);
    }



    public void showDetail(final Location extendedMarker){
        latitude.setText(""+extendedMarker.getLatitude());
        longitude.setText("" +extendedMarker.getLongitude());
        altitude.setText(""+extendedMarker.getAltitude());
        btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logga.avd(getContext(),"elimina");
                activity.disegna(extendedMarker);
            }
        });
    }
}
