package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarkerFragment extends Fragment {
	private View rootView;
	private Button button1, button2;
	private GoogleMap map;
	private MapMarkerFragment fragment;
	private LatLng newll;
	private ArrayList<LatLng> mTrace;
	private ArrayList<LatLng> mTag;
	private ArrayList<Marker> mMarker;
	private ArrayList<String> labels;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = this;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//super.onViewCreated(container, savedInstanceState);
		
		rootView = inflater.inflate(R.layout.fragment_map, container, false);
		
		mTrace = new ArrayList<LatLng>();
		mTag = new ArrayList<LatLng>();
		mMarker = new ArrayList<Marker>();
		
		mTrace.add(new LatLng(34.0222200, -118.423072));
		mTrace.add(new LatLng(34.037351, -118.442852));
		mTrace.add(new LatLng(34.069351, -118.445152));
		mTrace.add(new LatLng(34.052351, -118.433172));
		mTrace.add(new LatLng(34.037322, -118.428132));
		
		mTag.add(new LatLng(34.049351, -118.423852));
		mTag.add(new LatLng(34.058351, -118.438852));
		
		labels = new ArrayList<String>();
		labels.add("Home");
		labels.add("Work");
		
		map = ((SupportMapFragment)this.getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng ll) {
				newll = ll;
				AlertDialog.Builder alert = new AlertDialog.Builder(fragment.getActivity());

				alert.setTitle("New Place");
				alert.setMessage("Please name this new place:");

				final EditText input = new EditText(fragment.getActivity());
				alert.setView(input);

				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();
								mTag.add(newll);
								labels.add(value);
								mMarker.add(map.addMarker(new MarkerOptions().position(newll).draggable(true).visible(true).title(value)));
								//addItemsOnPlaceSpinner();
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});
				alert.show();
			}
		});
		setButtonListener();
		return rootView;
	}
	
	private void focusOnMarker() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker m:mMarker) {
			builder.include(m.getPosition());
		}
		LatLngBounds bounds = builder.build();
		int padding = 10; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		map.animateCamera(cu);
	}
	
	private void setButtonListener() {
		button1 = (Button) rootView.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				synchronized (mMarker) {
					map.clear();
					mMarker.clear();
					for (LatLng ll:mTrace) {
						mMarker.add(map.addMarker(new MarkerOptions().position(ll).draggable(true).visible(true)));
					}
					focusOnMarker();
				}
			}
			
		});
		
		button2 = (Button) rootView.findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int count = 0;
				synchronized (mMarker) {
					map.clear();
					mMarker.clear();
					for (LatLng ll:mTag) {
						mMarker.add(map.addMarker(new MarkerOptions().position(ll).draggable(true).visible(true).title(labels.get(count++))));
					}
					focusOnMarker();
				}
			}
			
		});
	}
}
