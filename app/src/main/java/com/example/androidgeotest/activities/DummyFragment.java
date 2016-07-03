package com.example.androidgeotest.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.example.androidgeotest.R;

/**
 * Created by izs on 05/04/16.
 */
public class DummyFragment extends Fragment {

    private CoordinatorLayout coordinatorLayout;
    private Button downloadButton;
    private ProgressDialog progressDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Download Dati Utente");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dummy_page, container, false);

        //chiudo la tastiera
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorlayout);
        downloadButton = (Button) v.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("DummyFragment","SCHIACCIATO");
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
    }
}
