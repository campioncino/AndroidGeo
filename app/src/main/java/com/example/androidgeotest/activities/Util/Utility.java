package com.example.androidgeotest.activities.Util;

/**
 * Created by r.sciamanna on 04/07/2016.
 */

public class Utility {

    public static int convertDegrees(float bearing) {
        int valueInDegree = (int) Math.round(Math.toDegrees(bearing));
        if (valueInDegree < 0) {
            valueInDegree = (valueInDegree + 360) % 360;
        }
        return valueInDegree;
    }
}
