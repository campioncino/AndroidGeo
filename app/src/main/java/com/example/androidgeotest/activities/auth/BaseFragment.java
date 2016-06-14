package com.example.androidgeotest.activities.auth;

/**
 * Created by r.sciamanna on 14/06/2016.
 */
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.example.androidgeotest.R;

public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}
