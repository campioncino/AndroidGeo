package com.example.androidgeotest.activities.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.androidgeotest.R;
import com.example.androidgeotest.activities.Code;
import com.example.androidgeotest.activities.MainMenuActivity;
import com.example.androidgeotest.activities.auth.GoogleSignInActivity;
import com.example.androidgeotest.activities.auth.GoogleSignInFragment;
import com.example.androidgeotest.activities.auth.SignInActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by r.sciamanna on 14/06/2016.
 */

public class LoginActivity extends AppCompatActivity {

//    private final static int AUTH_REQ_CODE = 003;
//
//    private final static int LOGGED_CODE = 001;
//    private final static int LOGGIN_CODE = 002;

    private FirebaseUser mAuthUser;

    public GoogleSignInAccount gAccount;

    public FirebaseUser getmAuthUser() {
        return mAuthUser;
    }

    public void setmAuthUser(FirebaseUser mAuthUser) {
        this.mAuthUser = mAuthUser;
    }

    private Fragment currentFragment;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_layout);
        setContentView(R.layout.content);
        Log.wtf("Login", "onCreate");
        mAuthUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mAuthUser != null) {
            Log.wtf("Login", mAuthUser.getDisplayName());
            Intent i = new Intent(this, MainMenuActivity.class);
            i.putExtra("fUser", mAuthUser.getDisplayName());
            i.putExtra("fEmail", mAuthUser.getEmail());
            startActivityForResult(i, Code.LOGGED_CODE);
        } else {
//            System.out.println("MAUTH = "+mAuth.getCurrentUser().toString());
            Log.wtf("Login", "firebase user assente");
//            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
//            currentFragment = new GoogleSignInFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.content_frame, currentFragment);
//            fragmentTransaction.commit();
            Intent i = new Intent(this, SignInActivity.class);
            startActivityForResult(i, Code.LOGGIN_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("Login,onResult", "request =" + requestCode + " result=" + resultCode);
        switch (requestCode) {
            case Code.LOGGED_CODE:
                Log.wtf("LoginMenu", "LOGGED CODE");
                finish();
                break;

            case 4444:
                Log.wtf("LoginMenu", "LOGGED CODE");
                finish();
                break;

            case Code.LOGGIN_CODE:
                Log.wtf("LoginMenu", "LOGGIN CODE");
                gAccount = data.getParcelableExtra("account");
                Log.wtf("login", gAccount.getDisplayName());
                Intent i = new Intent(this,MainMenuActivity.class);
                i.putExtra("gAccount",gAccount);
                startActivityForResult(i,4444);
                break;

            case Code.AUTH_REQ_CODE:
                Log.wtf("login", String.valueOf(resultCode));
                break;
        }

    }
}

