package com.example.androidgeotest.activities.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by r.sciamanna on 05/07/2016.
 */

public class MyDialog extends AlertDialog{
    public MyDialog(Context context) {
        super(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title");

        builder.create().show();
    }

}
