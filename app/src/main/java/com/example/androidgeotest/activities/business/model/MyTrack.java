package com.example.androidgeotest.activities.business.model;

import android.location.Location;

import java.util.Date;
import java.util.List;

/**
 * Created by r.sciamanna on 15/06/2016.
 */

public class MyTrack {

    private Integer raceId;

    private Date start;

    public Date stop;

    public float totalDistace;

    public double totalDuration;

    public List<Location> trip;

    public MyTrack() {
    }
}
