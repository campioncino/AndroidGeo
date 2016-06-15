package com.example.androidgeotest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.auth.GoogleSignInActivity;
import com.example.androidgeotest.activities.auth.GoogleSignInFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by r.sciamanna on 14/06/2016.
 */

public class LoginActivity extends AppCompatActivity {

    private final static int AUTH_REQ_CODE = 001;
    GoogleApiClient googleApiClient;


    String mAuthUser;

    private Fragment currentFragment;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        mAuthUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if(mAuthUser!=null){
            Intent i = new Intent(this,MainMenuActivity.class);
            i.putExtra("user",mAuthUser);
            startActivity(i);
        }else{
//            System.out.println("MAUTH = "+mAuth.getCurrentUser().toString());
            System.out.println("firebase user assente");
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
            currentFragment = new GoogleSignInFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.content_frame, currentFragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}

