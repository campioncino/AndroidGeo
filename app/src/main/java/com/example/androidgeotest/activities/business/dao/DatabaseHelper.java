package com.example.androidgeotest.activities.business.dao;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by r.sciamanna on 15/06/2016.
 */
public class DatabaseHelper {


    public static boolean isDatabaseExistent(Context context) {

        CustomSQLiteOpenHelper helper = OpenHelperManager.getHelper(context,
                CustomSQLiteOpenHelper.class);

        return helper.isDatabaseExistent(context);

    }


    public static void clearDatabase(Context context) {

        CustomSQLiteOpenHelper helper = OpenHelperManager.getHelper(context,
                CustomSQLiteOpenHelper.class);
        helper.clearDatabase(context);

    }

}