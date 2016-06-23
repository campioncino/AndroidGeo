package com.example.androidgeotest.activities.business.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.xebia.android.freezer.annotations.Id;
import fr.xebia.android.freezer.annotations.Model;

/**
 * Created by r.sciamanna on 20/06/2016.
 */
@Model
public class FreezerRace implements Parcelable {

    @Id
    public Integer raceId;
    public Date start;
    public Date stop;
    public Long totalDistace;
    public Long totalDuration;

    public List<FreezerLocation> locations ;

    public FreezerRace() {
        super();
    }

    public FreezerRace(List<FreezerLocation> locations){
        super();
        this.raceId = locations.get(0).
    }

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

    public Long getTotalDistace() {
        return totalDistace;
    }

    public void setTotalDistace(Long totalDistace) {
        this.totalDistace = totalDistace;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<FreezerLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<FreezerLocation> locations) {
        this.locations = locations;
    }

    public FreezerRace(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeValue(this.raceId);
        dest.writeValue(this.start);
        dest.writeValue(this.stop);
        dest.writeValue(this.totalDistace);
        dest.writeValue(this.totalDuration);
        dest.writeTypedList(this.locations);

    }

    public void readFromParcel(Parcel in) {
        this.raceId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.start = (Date) in.readValue(Date.class.getClassLoader());
        this.stop = (Date) in.readValue(Date.class.getClassLoader());
        this.totalDistace = (Long) in.readValue(Long.class.getClassLoader());
        this.totalDuration = (Long) in.readValue(Long.class.getClassLoader());
        in.readTypedList(this.locations,FreezerLocation.CREATOR);
    }


    public static final Creator<FreezerRace> CREATOR = new Creator<FreezerRace>() {
        public FreezerRace createFromParcel(Parcel source) {
            return new FreezerRace(source);
        }

        public FreezerRace[] newArray(int size) {
            return new FreezerRace[size];
        }
    };
}
