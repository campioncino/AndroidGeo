package com.example.androidgeotest.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidgeotest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;


public class MainMenuActivity extends AppCompatActivity{

    private NavigationView navigationView;
    private TextView headerUsername;

    public Drawer drawer = null;

    public PrimaryDrawerItem itemUno = null;
    public PrimaryDrawerItem itemDue = null;
    public PrimaryDrawerItem itemTre = null;

    private Fragment currentFragment;

    private LocationManager locationManager;
    private String provider;

    private Location defaultLocation;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainmenu);
        initToolbar();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        currentFragment = new DummyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.content_frame, currentFragment);
        fragmentTransaction.commit();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
//        headerUsername = (TextView) headerView.findViewById(R.id.menu_username);
//        headerUsername.setText(MyApplication.account.getAccCodice());


        // Create the AccountHeader
//        AccountHeader headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(R.drawable.background)
//                .addProfiles(
//                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(GoogleMaterial.Icon.gmd_wb_sunny)
//                )
//                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
//                    @Override
//                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
//                        return false;
//                    }
//                })
//                .build();

        itemUno = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName("GpsActivity")
                .withIcon(GoogleMaterial.Icon.gmd_favorite)
                .withIconTintingEnabled(true)
                .withBadgeStyle(new BadgeStyle()
                        .withColorRes(R.color.md_red_700)
                        .withTextColor(ContextCompat.getColor(this, R.color.md_white_1000)));

        itemDue = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName("DUE")
                .withIcon(GoogleMaterial.Icon.gmd_view_week)
                .withIconColorRes(R.color.md_white_1000)
                .withSelectedIconColor(ContextCompat.getColor(this, R.color.md_amber_300))
                .withIconTintingEnabled(true)
                .withBadgeStyle(new BadgeStyle()
                        .withTextColor(ContextCompat.getColor(this, R.color.accent))
                        .withColorRes(R.color.md_white_1000));

        itemTre = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName("TRE")
                .withIcon(GoogleMaterial.Icon.gmd_star_border)
                .withIconColorRes(R.color.md_white_1000)
                .withSelectedIconColor(ContextCompat.getColor(this, R.color.md_teal_400))
                .withIconTintingEnabled(true)
                .withBadgeStyle(new BadgeStyle()
                        .withTextColor(ContextCompat.getColor(this, R.color.accent))
                        .withColorRes(R.color.md_white_1000));


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar((Toolbar) findViewById(R.id.toolbar))
                .withHasStableIds(true)
                .withHeader(headerView)
//                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("sezione Uno").withEnabled(false),
                        itemUno,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("sezione due").withEnabled(false),
                        itemDue,
                        itemTre

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                openMyGpsActivity();
                            } else if (drawerItem.getIdentifier() == 2) {
                                openDue();
                            } else if (drawerItem.getIdentifier() == 3) {
                                openTre();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withSelectedItem(1)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    public void onDrawerOpened(View drawerView) {
                        //avviene solo dopo che è stato aperto ~ effetto fadeout
//                        refreshRicetteCounter();
                    }

                    public void onDrawerClosed(View drawerView) {

                    }

                    public void onDrawerSlide(View drawerView, float slideOffset) {
//                        refreshRicetteCounter();
                    }
                })
                .build();
//        openRegistroRicette();
        drawer.openDrawer();

    }



//    public void refreshRicetteCounter() {
//        RicettaExt ricetta = new RicettaExt();
//        ricetta.setCodiceLingua("it");
//        ricetta.setStatoricettaDescrizione(PageCostantiTO.STATORICETTA_INIZIALE);
//        String ricetteCounter = null;
//        try {
//            ricetteCounter = String.valueOf(ricettaService.search(ricetta).size());
//        } catch (CrudException e) {
//            e.printStackTrace();
//        }
//        if (ricetteCounter != null && Integer.valueOf(ricetteCounter) >= 0) {
//            drawer.updateBadge(1, new StringHolder(ricetteCounter));
//            if (Integer.valueOf(ricetteCounter) == 0) {
//                drawer.updateBadge(1, null);
//            }
//        }
//
//    }


    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_menu)
                    .color(Color.RED)
                    .sizeDp(24));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("USCIRE?")
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
//        refreshRicetteCounter();
//        TODO decommentare per attivare la data dell'ultimo aggiornamento
//        itemAggiornDati = (PrimaryDrawerItem) drawer.getDrawerItem(5);
//        itemAggiornDati.withDescription("del DATA ULTIMO AGGIORNAMENTO!");
    }

    private void openMyGpsActivity() {
        Intent i = new Intent(this, MyGpsActivity.class);

        i.putExtra("defaultLocation", defaultLocation);
        startActivity(i);
    }

    private void openDue() {
        Intent i = new Intent(this, ShowLocationActivity.class);
        startActivity(i);
//        currentFragment = new MyMapFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.content_frame,currentFragment);
//        fragmentTransaction.commit();
//        Intent i = new Intent(this, PrescrizioneVeterinariaWrapperActivity.class);
//        startActivityForResult(i, Code.REQUEST_WIZARD);
    }

    private void openTre() {

    }

    private Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case Code.REQUEST_WIZARD:
//                // VADO AL REGISTRO
//                toRegistro = true;
//                break;
//            default:
//                if (getCurrentFragment() != null) {
//                    getCurrentFragment().onActivityResult(requestCode, resultCode, data);
//                }
//                break;
//        }
    }

}
