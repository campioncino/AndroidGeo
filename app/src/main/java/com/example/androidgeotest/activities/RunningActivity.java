package com.example.androidgeotest.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Chronometer.MyChronometer;

/**
 * Created by r.sciamanna on 11/05/2016.
 */




public class RunningActivity extends AppCompatActivity {


    private Fragment mapFragment;
    private Fragment chronoFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.running_activity_layout);
//        AndroidBug5497Workaround.assistActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chronoFragment = new MyChronometer();
        getSupportFragmentManager().beginTransaction().add(R.id.chrono_fragment,chronoFragment).commit();

        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_fragment, mapFragment).commit();

//        wizardRiepilogoService = new WizardRiepilogoService(this);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Attività");
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedAppbar);

        //setto l'appbar collapsed, così vedo una sola riga, mantenendo il dettaglio se scrollo
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        appbar.setExpanded(false);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                super.onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


}
