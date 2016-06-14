package com.example.androidgeotest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.auth.GoogleSignInActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by r.sciamanna on 14/06/2016.
 */

public class Main extends AppCompatActivity {

    private final static int AUTH_REQ_CODE = 001;
    GoogleApiClient googleApiClient;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("User", "passo?");
        setContentView(R.layout.content);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(this, GoogleSignInActivity.class);
            startActivityForResult(i, AUTH_REQ_CODE);
        } else {
            Log.wtf("User", mAuth.getCurrentUser().toString());
            Intent i = new Intent(this, MainMenuActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult....requestCode="+requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == AUTH_REQ_CODE) {
            String result ="requestCode =" + requestCode + " resultCode =" + resultCode;
            Log.wtf("result",result);
        } else {
            System.out.println("ooops....");
        }
    }

}

