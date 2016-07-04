package com.example.androidgeotest.activities.excursion;

/**
 * Created by r.sciamanna on 04/07/2016.
 */

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.androidgeotest.R;
import com.hoan.dsensor_master.DProcessedSensor;
import com.hoan.dsensor_master.DSensor;
import com.hoan.dsensor_master.DSensorEvent;
import com.hoan.dsensor_master.DSensorManager;
import com.hoan.dsensor_master.interfaces.DProcessedEventListener;


public class CompassActivity extends AppCompatActivity implements DProcessedEventListener {
    public static final String DPROCESSEDSENSOR_TYPE = "DProcessedSensorType";

    private TextView mCompassValueTextView;
    private TextView mDepreciatedOrientationValueTextView;

    private int mDProcessedSensorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCompassValueTextView = (TextView) findViewById(R.id.textview_compass_value);

        Intent intent = getIntent();
        mDProcessedSensorType = intent == null ? DProcessedSensor.TYPE_3D_COMPASS
                : intent.getIntExtra(DPROCESSEDSENSOR_TYPE, DProcessedSensor.TYPE_3D_COMPASS);
        if (mDProcessedSensorType == DProcessedSensor.TYPE_COMPASS_FLAT_ONLY_AND_DEPRECIATED_ORIENTATION
                || mDProcessedSensorType == DProcessedSensor.TYPE_3D_COMPASS_AND_DEPRECIATED_ORIENTATION) {
            mDepreciatedOrientationValueTextView = (TextView) findViewById(R.id.textview_orientation_value);
            TextView depreciatedOrientationTextView = (TextView) findViewById(R.id.textview_orientation);
            mDepreciatedOrientationValueTextView.setVisibility(View.VISIBLE);
            depreciatedOrientationTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int flag = DSensorManager.startDProcessedSensor(this, mDProcessedSensorType, this);
        if ((flag & DSensorManager.TYPE_MAGNETIC_FIELD_NOT_AVAILABLE) != 0) {
            mCompassValueTextView.setText("error_no_magnetic_field_sensor");
        } else if ((flag & DSensorManager.TYPE_GRAVITY_NOT_AVAILABLE) != 0
                && (flag & DSensorManager.TYPE_ACCELEROMETER_NOT_AVAILABLE) != 0) {
            mCompassValueTextView.setText("error_no_accelerometer_sensor");
        }
    }

    @Override
    protected void onPause() {
        DSensorManager.stopDSensor();

        super.onPause();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_portrait:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                return true;
//
//            case R.id.action_landscape:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                return true;
//
//            case R.id.action_reverse_landscape:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                return true;
//
//            case R.id.action_reverse_portrait:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_compass, menu);
//        disableMenuItem(menu);
        return true;
    }

    @Override
    public void onProcessedValueChanged(DSensorEvent dSensorEvent) {
        if (dSensorEvent.sensorType == DSensor.TYPE_DEVICE_MAGNETIC_FIELD) {
            mDepreciatedOrientationValueTextView.setText(String.valueOf(Math.round(dSensorEvent.values[0])));
        } else {
            if (Float.isNaN(dSensorEvent.values[0])) {
                mCompassValueTextView.setText("Device is not flat no compass value");
            } else {
                int valueInDegree = (int) Math.round(Math.toDegrees(dSensorEvent.values[0]));
                if (valueInDegree < 0) {
                    valueInDegree = (valueInDegree + 360) % 360;
                }
                mCompassValueTextView.setText(String.valueOf(valueInDegree));
            }
        }
    }

//    private void disableMenuItem(Menu menu) {
//        MenuItem item;
//        switch (DeviceInfo.getOrientation(this)) {
//            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
//                item = menu.findItem(R.id.action_portrait);
//                break;
//
//            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
//                item = menu.findItem(R.id.action_landscape);
//                break;
//
//            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
//                item = menu.findItem(R.id.action_reverse_landscape);
//                break;
//
//            default:
//                item = menu.findItem(R.id.action_reverse_portrait);
//                break;
//        }
//        item.setEnabled(false);
//    }


}