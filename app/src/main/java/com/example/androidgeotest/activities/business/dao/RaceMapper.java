package com.example.androidgeotest.activities.business.dao;

import android.content.Context;

import com.example.androidgeotest.activities.business.model.Entity;
import com.example.androidgeotest.activities.business.model.Race;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by r.sciamanna on 07/06/2016.
 */

public class RaceMapper extends EntityMapper {

    public RaceMapper(Context context, Class<? extends Entity> entityClass) {
        super(context, entityClass);
    }

    @Override
    public void initViewDao(Context context) {
        CustomSQLiteOpenHelper helper = OpenHelperManager.getHelper(context,
                CustomSQLiteOpenHelper.class);
        try {
            Dao<Race, Integer> truckExtDao = helper.getDao(Race.class);
            setWiewDao(truckExtDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
    }

    @Override
    public void initTableDao(Context context) {
        CustomSQLiteOpenHelper helper = OpenHelperManager.getHelper(context,
                CustomSQLiteOpenHelper.class);
        try {
            Dao<Race, Integer> truckDao = helper.getDao(Race.class);
            setTableDao(truckDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
    }
}
