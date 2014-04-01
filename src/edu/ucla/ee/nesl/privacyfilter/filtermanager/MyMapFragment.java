package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MyMapFragment extends SupportMapFragment {

    //private static final String LOG_TAG = "CustomMapFragment";

    public MyMapFragment() {
        super();

    }

    public static MyMapFragment newInstance() {
        MyMapFragment fragment = new MyMapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View v = super.onCreateView(arg0, arg1, arg2);
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof OnMapReadyListener) {
            ((OnMapReadyListener) fragment).onMapReady(getMap());
        }
        return v;
    }

    public static interface OnMapReadyListener {
        void onMapReady(GoogleMap map);
    }
}
