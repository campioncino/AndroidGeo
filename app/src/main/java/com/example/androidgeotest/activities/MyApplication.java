package com.example.androidgeotest.activities;

/**
 * Created by r.sciamanna on 20/06/2016.
 */

import android.app.Application;

import fr.xebia.android.freezer.Freezer;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Freezer.onCreate(this);
    }

}