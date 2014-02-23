package edu.ucla.ee.nesl.privacyfilter.filtermanager.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


public class MyMapFragment extends SupportMapFragment {
	private OnGoogleMapFragmentListener mCallback;
	
	public static interface OnGoogleMapFragmentListener {
        void onMapReady(GoogleMap map);
    }
	
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
        if (mCallback != null) {
            mCallback.onMapReady(getMap());
        }
        return v;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnGoogleMapFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName() + " must implement OnGoogleMapFragmentListener");
        }
    }
    
}
