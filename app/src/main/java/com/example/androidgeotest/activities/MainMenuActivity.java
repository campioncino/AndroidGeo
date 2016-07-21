package com.example.androidgeotest.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.example.androidgeotest.activities.auth.GoogleSignInFragment;
import com.example.androidgeotest.activities.excursion.ExcursionActivity;
import com.example.androidgeotest.activities.excursion.ExcursionActivity2;
import com.example.androidgeotest.activities.running.RunningActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import fr.xebia.android.freezer.Freezer;


public class MainMenuActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private TextView headerUsername;

    public Drawer drawer = null;

    public final int GPS_REQUEST_CODE = 123;

    public PrimaryDrawerItem itemUno = null;
    public PrimaryDrawerItem itemDue = null;
    public PrimaryDrawerItem itemTre = null;

    private Fragment currentFragment;

    private LocationManager locationManager;
    private String provider;

    private Location defaultLocation;
    private CoordinatorLayout coordinatorLayout;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //freezer dao
        Freezer.onCreate(getApplication());

        setContentView(R.layout.mainmenu);
        initToolbar();



        checkGpsEnabled();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        currentFragment = new DummyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.content_frame, currentFragment);
        fragmentTransaction.commit();

        GoogleSignInAccount gAccount = getIntent().getParcelableExtra("gAccount");
        String fUser = getIntent().getStringExtra("fUser");
        String fEmail = getIntent().getStringExtra("fEmail");
        String user = "";/*getIntent().getStringExtra("user");*/
        String email = "";/*getIntent().getStringExtra("email");*/
        if (gAccount != null) {
            user = gAccount.getDisplayName();
            email = gAccount.getEmail();
        }
        if (fUser != null) {
            user = fUser;
        }
        if (fEmail != null) {
            email = fEmail;
        }

        Log.wtf("MainMenu", user + " " + email);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

//        headerUsername = (TextView) headerView.findViewById(R.id.menu_username);
//        headerUsername.setText(username);

//         Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .addProfiles(
                        new ProfileDrawerItem().withName(user).withEmail(email).withIcon(GoogleMaterial.Icon.gmd_wb_sunny)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        openLogin();
                        return false;
                    }
                })
                .build();

        itemUno = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName("GpsActivity")
                .withIcon(GoogleMaterial.Icon.gmd_directions_run)
                .withIconTintingEnabled(true)
                .withBadgeStyle(new BadgeStyle()
                        .withColorRes(R.color.md_red_700)
                        .withTextColor(ContextCompat.getColor(this, R.color.md_white_1000)));

        itemDue = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName("Exploration")
                .withIcon(GoogleMaterial.Icon.gmd_terrain)
                .withIconColorRes(R.color.green900)
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
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
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
                                openUno();
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
                    }

                    public void onDrawerClosed(View drawerView) {

                    }

                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .build();
        drawer.openDrawer();

    }


    //GPS ALERT DIALOG
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, GPS_REQUEST_CODE);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


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
    protected void onResume() {
        super.onResume();
//        checkGpsEnabled();
    }

    private void openUno() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.wtf("MainActivity", "Location is Enabled");
            Intent i = new Intent(this, RunningActivity.class);

            startActivity(i);
        } else {
//            Log.wtf("MainActivity","Location is NOT Enabled");
            checkGpsEnabled();
        }
    }

    private void openDue() {
        Intent i = new Intent(this, ExcursionActivity.class);
        startActivity(i);
//        Intent i = new Intent(this, TestMapActivity.class);
//        Intent i = new Intent(this, MapLocationActivity.class);
//
//        startActivity(i);
//        currentFragment = new MyMapFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.content_frame,currentFragment);
//        fragmentTransaction.commit();
//        Intent i = new Intent(this, PrescrizioneVeterinariaWrapperActivity.class);
//        startActivityForResult(i, Code.REQUEST_WIZARD);
    }

    private void openTre() {
        Intent i = new Intent(this, ExcursionActivity2.class);
        startActivity(i);
//        Intent i = new Intent(this, CompassActivity.class);
//        startActivityForResult(i,12345);
//        openLogin();
//         Intent i = new Intent(this, GoogleSignInActivity.class);
//        startActivity(i);
//        Intent i = new Intent(this, FusedLocationActivity.class);
//
//        i.putExtra("defaultLocation", defaultLocation);
//        startActivity(i);
    }

    public void openLogin() {
        currentFragment = new GoogleSignInFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction().replace(R.id.content_frame, currentFragment);
        fragmentTransaction.commit();
    }

//    private Fragment getCurrentFragment() {
//        return currentFragment;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("MainMenu,onResult", "request =" + requestCode + " result=" + resultCode);
        switch (requestCode) {
            case GPS_REQUEST_CODE:
                if (resultCode == 0) {
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Log.wtf("MainMenu", "activity result code =" + resultCode);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "GPS is MANDATORY", Toast.LENGTH_SHORT).show();
                        showGPSDisabledAlertToUser();
                    }
                }
                break;
            case 12345:
                Log.wtf("MainMenu","result 12345");
                break;
            default:
                System.out.println("on activity result:\nresultCode:" + resultCode + "\nrequestCode:" + requestCode);
                break;
        }

//        if(requestCode == GPS_REQUEST_CODE && resultCode == 0){
//            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            Log.wtf("MainMenu","activity result code ="+resultCode);
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this,"GPS is MANDATORY",Toast.LENGTH_SHORT).show();
//                showGPSDisabledAlertToUser();
//            }
//        }
//        if(requestCode == 205609){
//            Log.wtf("MainMenuActivity","requestCode ="+requestCode);
//        }

    }

    private void checkGpsEnabled() {
        //check if gps is enabled or not
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
    }

}
