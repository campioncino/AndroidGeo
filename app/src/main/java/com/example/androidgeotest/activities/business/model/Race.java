package com.example.androidgeotest.activities.business.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by r.sciamanna on 06/06/2016.
 */
@DatabaseTable(tableName = "RACE")
public class Race extends Entity{

    @DatabaseField(columnName = "RACE_ID", generatedId = true)
    public Integer raceId;

    @DatabaseField(columnName = "START")
    public Date start;

    @DatabaseField(columnName = "STOP")
    public Date stop;

    @DatabaseField(columnName = "DISTANCE")
    public float totalDistace;

    @DatabaseField(columnName = "DURATION")
    public double totalDuration;

    @DatabaseField(columnName = "RACE_TRACK")
    public String trip;

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer raceId) {
        this.raceId = raceId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public float getTotalDistace() {
        return totalDistace;
    }

    public void setTotalDistace(float totalDistace) {
        this.totalDistace = totalDistace;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public Race() {
        super();
    }

    public Race(Parcel in) {
        super(in);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeValue(this.raceId);
        dest.writeValue(this.trip);
        dest.writeValue(this.start);
        dest.writeValue(this.stop);
        dest.writeValue(this.totalDistace);
        dest.writeValue(this.totalDuration);

    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        this.raceId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.trip = (String) in.readValue(String.class.getClassLoader());
        this.start = (Date) in.readValue(Date.class.getClassLoader());
        this.stop = (Date) in.readValue(Date.class.getClassLoader());
        this.totalDistace = (float) in.readValue(Double.class.getClassLoader());
        this.totalDuration = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Race> CREATOR = new Parcelable.Creator<Race>() {
        public Race createFromParcel(Parcel source) {
            return new Race(source);
        }

        public Race[] newArray(int size) {
            return new Race[size];
        }
    };


}
