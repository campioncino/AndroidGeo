package com.example.androidgeotest.activities.business.model;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import fr.xebia.android.freezer.annotations.Model;

/**
 * Created by r.sciamanna on 20/06/2016.
 */
@Model
public class FreezerLocation implements Parcelable {


    public String mProvider;
    public long mTime = 0;
    public long mElapsedRealtimeNanos = 0;
    public double mLatitude = 0.0;
    public double mLongitude = 0.0;
    public boolean mHasAltitude = false;
    public double mAltitude = 0.0f;
    public boolean mHasSpeed = false;
    public float mSpeed = 0.0f;
    public boolean mHasBearing = false;
    public float mBearing = 0.0f;
    public boolean mHasAccuracy = false;
    public float mAccuracy = 0.0f;

    public double mLat1 = 0.0;
    public double mLon1 = 0.0;
    public double mLat2 = 0.0;
    public double mLon2 = 0.0;
    public float mDistance = 0.0f;
    public float mInitialBearing = 0.0f;

    public FreezerLocation(Location location) {
        set(location);
    }

    public FreezerLocation() {
        super();
    }

    public FreezerLocation(Parcel in) {
        readFromParcel(in);
    }


    public void set(Location l) {
    mProvider = l.getProvider();
    mTime = l.getTime();
    mElapsedRealtimeNanos = l.getElapsedRealtimeNanos();
    mLatitude = l.getLatitude();
    mLongitude = l.getLongitude();
    mHasAltitude = l.hasAltitude();
    mAltitude = l.getAltitude();
    mHasSpeed = l.hasSpeed();
    mSpeed = l.getSpeed();
    mHasBearing = l.hasBearing();
    mBearing = l.getBearing();
    mHasAccuracy = l.hasAccuracy();
    mAccuracy = l.getAccuracy();
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeValue(this.mProvider);
        dest.writeValue(this.mTime);
        dest.writeValue(this.mElapsedRealtimeNanos );
        dest.writeValue(this.mLatitude);
        dest.writeValue(this.mLongitude);
        dest.writeValue(this.mHasAltitude );
        dest.writeValue(this.mAltitude );
        dest.writeValue(this.mHasSpeed );
        dest.writeValue(this.mSpeed );
        dest.writeValue(this.mHasBearing );
        dest.writeValue(this.mBearing );
        dest.writeValue(this.mHasAccuracy );
        dest.writeValue(this.mAccuracy );

    }

    public void readFromParcel(Parcel in) {
        this.mProvider = (String) in.readValue(String.class.getClassLoader());
        this.mTime = (Long) in.readValue(Long.class.getClassLoader());
        this.mElapsedRealtimeNanos = (Long) in.readValue(Long.class.getClassLoader());
        this.mLatitude= (Double) in.readValue(Double.class.getClassLoader());
        this.mLongitude= (Double) in.readValue(Double.class.getClassLoader());
        this.mHasAltitude = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mAltitude = (Double) in.readValue(Double.class.getClassLoader());
        this.mHasSpeed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mSpeed = (float) in.readValue(float.class.getClassLoader());
        this.mHasBearing = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mBearing = (float) in.readValue(float.class.getClassLoader());
        this.mHasAccuracy = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mAccuracy = (float) in.readValue(float.class.getClassLoader());
    }


    public static final Parcelable.Creator<FreezerLocation> CREATOR = new Parcelable.Creator<FreezerLocation>() {
        public FreezerLocation createFromParcel(Parcel source) {
            return new FreezerLocation(source);
        }

        public FreezerLocation[] newArray(int size) {
            return new FreezerLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
