package com.example.androidgeotest.activities.Util;

/**
 * Created by r.sciamanna on 29/04/2016.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.ProviderError;
import fr.quentinklein.slt.TrackerSettings;





public class MyApplication extends Application {



//	public static Consumer consumer;

    private static Location defaultLocation;

    public static Location getDefaultLocation() {
        return defaultLocation;
    }


    private LocationTracker locationTracker;

    private static List<Location> locationList = new ArrayList<Location>();

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("ON CREATE");
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                startListeningLocation();
            }

            public void onActivityStarted(Activity activity) {

            }

            public void onActivityResumed(Activity activity) {
                startListeningLocation();
            }

            public void onActivityPaused(Activity activity) {
                stopListeningLocation();
            }

            public void onActivityStopped(Activity activity) {

            }

            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    public static void showErrorSnackbar(View coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }

    public static void showErrorSnackbar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }

    public static void showErrorSnackbarShort(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }

    public static void showErrorSnackbar(CoordinatorLayout coordinatorLayout, String message, String actionMessage, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.setAction(actionMessage, onClickListener);
        snackbar.show();
    }

    public static void showMessageSnackbar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.show();
    }

    public static void showMessageSnackbarShort(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.show();
    }

    public static void showMessageSnackbar(CoordinatorLayout coordinatorLayout, String message, String actionMessage, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.setAction(actionMessage, onClickListener);
        snackbar.show();
    }

    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void storeUsername(Activity activity, String username) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.apply();
    }

    public static String retrieveUsername(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        return prefs.getString("username", null);
    }


    private void startListeningLocation() {
        Log.wtf("MyApplication","startListeningLocation");
        if (locationTracker == null) {

            TrackerSettings settings = new TrackerSettings()
                    .setUseGPS(true)
                    .setUseNetwork(true)
                    .setUsePassive(false)
                    .setTimeBetweenUpdates(5000);

            locationTracker = new LocationTracker(this, settings) {
                @Override
                public void onLocationFound(Location location) {
                    // Do some stuff when a new GPS Location has been found
                    System.out.println("Location");
                    System.out.println(location.getLatitude() + ", " + location.getLongitude());
                    locationList.add(location);
                    defaultLocation=location;
//                    try {
//                        ObjectMapper mapper = new ObjectMapper();
//                        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept("extras");
//                        FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);
//                        mapper.setFilterProvider(filters);
//                        System.out.println(mapper.writeValueAsString(location));
//                    } catch (JsonProcessingException e) {
////                        e.printStackTrace();
//                    }
                }

                @Override
                public void onTimeout() {
                    System.out.println("Timeout");
                }

                @Override
                public void onProviderError(@NonNull ProviderError providerError) {
//                    System.out.println("PROVIDER ERROR " + providerError.getProvider());
                    super.onProviderError(providerError);
                }


            };
        }
        if (!locationTracker.isListening()) {
            locationTracker.startListening();
        }
    }

    private void stopListeningLocation() {
        if (locationTracker != null && locationTracker.isListening()) {
            locationTracker.stopListening();
        }
    }


    public static List<Location> getLocations() {
        return locationList;
    }

    public static void clearLocations() {
        locationList.clear();
    }

    public static Location getLastLocation() {
        if (!locationList.isEmpty()) {
            return locationList.get(locationList.size() - 1);
        }
        return null;
    }

    public static Location getFirstLocation() {
        if (!locationList.isEmpty()) {
            return locationList.get(0);
        }
        return null;
    }


}

