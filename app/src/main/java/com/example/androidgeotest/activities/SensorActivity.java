package com.example.androidgeotest.activities;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by r.sciamanna on 04/07/2016.
 */

public class SensorActivity extends Activity implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private float[] mRotationMatrix = new float[16];
    private GoogleMap mymap;
    private double angle;
    public SensorActivity(GoogleMap map) {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mymap=map;
    }

//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(this);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this,
                mAccelerometer,
                SensorManager.SENSOR_STATUS_ACCURACY_LOW);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);

//            if (Math.abs(Math.toDegrees(orientation[0]) - angle) > 0.8) {
//                float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;
//                mymap.setMyLocationEnabled(bearing);
//            }
            angle = Math.toDegrees(orientation[0]);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
