package com.example.androidgeotest.activities.business.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Entity implements Parcelable, EntityValidator {
    protected Long id;


    public Entity(Parcel in) {
        readFromParcel(in);
    }

    public Entity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
    }

    public void readFromParcel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Entity> CREATOR = new Parcelable.Creator<Entity>() {
        public Entity createFromParcel(Parcel source) {
            return new Entity(source);
        }

        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };

    public boolean isValid() {
        return true;
    }

}