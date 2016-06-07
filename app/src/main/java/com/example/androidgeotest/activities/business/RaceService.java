package com.example.androidgeotest.activities.business;

import android.content.Context;

import com.example.androidgeotest.activities.business.dao.RaceMapper;
import com.example.androidgeotest.activities.business.model.Race;

/**
 * Created by r.sciamanna on 07/06/2016.
 */

public class RaceService extends EntityService {
    public RaceService(Context context) {
        super(context);
    }

    @Override
    protected void initMapper() {
        setMapper(new RaceMapper(getContext(), getViewEntityClass()));
    }

    @Override
    protected void initViewEntityClass() {
        setViewEntityClass(Race.class);
    }

    @Override
    protected void initTableEntityClass() {
        setTableEntityClass(Race.class);
    }
}
