package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import edu.ucla.ee.nesl.privacyfilter.trace.TraceMap;

public class MapMarkerFragment extends Fragment {
	private View rootView;
	private Button button1, button2;
	private GoogleMap map;
	private MapMarkerFragment fragment;
	private LatLng newll;
//	private ArrayList<LatLng> mTrace;
	private ArrayList<Marker> mMarker;
	private ArrayList<Polyline> mLine;
	private ArrayList<String> mLabel;
	
	
	private ListView listView;
    private ArrayAdapter<String> mAdapter;
    static int count = 0;
    static int index = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = this;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//super.onViewCreated(container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_map, container, false);
		
//		mTrace = new ArrayList<LatLng>();
		mMarker = new ArrayList<Marker>();
		mLine = new ArrayList<Polyline>();
		mLabel = new ArrayList<String>();
//		mTag = new ArrayList<LatLng>();
		
//		mTrace.add(new LatLng(34.0222200, -118.423072));
//		mTrace.add(new LatLng(34.037351, -118.442852));
//		mTrace.add(new LatLng(34.069351, -118.445152));
//		mTrace.add(new LatLng(34.052351, -118.433172));
//		mTrace.add(new LatLng(34.037322, -118.428132));		
//		mTag.add(new LatLng(34.049351, -118.423852));
//		mTag.add(new LatLng(34.058351, -118.438852));		
//		labels = new ArrayList<String>();
//		labels.add("Home");
//		labels.add("Work");
		
		listView = (ListView) rootView.findViewById(R.id.loc_list);
		mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
		listView.setAdapter(mAdapter);
		
		listView.setClickable(true);
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				index = arg2;
				new AlertDialog.Builder(rootView.getContext())
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Confirm Place Delete")
		        .setMessage("Do you really want to delete this place form your trace?")
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
						Object o = listView.getItemAtPosition(index);
						//listView.removeViewAt(arg2);
						mAdapter.remove((String) o);
						count--;
		            }
		            
		        })
		        .setNegativeButton("No", null)
		        .show();

				return true;
			}
		});
		
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
								//mTag.add(newll);
								//labels.add(value);
								AppListActivity.mapMarkers.put(value, newll);
								AppListActivity.updateState();
								mMarker.add(map.addMarker(new MarkerOptions().position(newll).draggable(true).visible(true).title(value)));
								Log.d("MarkerMap", "add new entry " + value + " lat=" + newll.latitude + " lon=" + newll.longitude);
								
								// connect this one with the previous marker
								if (count > 0) {
									 Polyline line = map.addPolyline(new PolylineOptions()
								     .add(mMarker.get(count - 1).getPosition(), mMarker.get(count).getPosition())
								     .width(5)
								     .color(Color.BLUE));
									 mLine.add(line);
								}
								//addItemsOnPlaceSpinner();
								count++;
								mLabel.add(value);
								mAdapter.add("Place #" + count + "\nName= " + value + "\nLat=" + newll.latitude + "\nLon=" + newll.longitude);
								mAdapter.notifyDataSetChanged();
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
		focusOnMarker();
		return rootView;
	}
	
	private void focusOnMarker() {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(34.0722, -118.4441));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		map.moveCamera(center);
		map.animateCamera(zoom);
	}
	
	private void setButtonListener() {
		button1 = (Button) rootView.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				synchronized (mMarker) {
					AlertDialog.Builder alert = new AlertDialog.Builder(fragment.getActivity());

					alert.setTitle("Save Trace");
					alert.setMessage("Please name this new trace:");

					final EditText input = new EditText(fragment.getActivity());
					alert.setView(input);

					alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString();
							TraceMap.addPath(mLabel, value);
							AlertDialog.Builder saveAlert  = new AlertDialog.Builder(fragment.getActivity());                      
						    saveAlert.setTitle("Path saved"); 
						    saveAlert.setMessage("The real path is saved as " + value
						    		+ ",\nand the fake path is saved as " + value + "_fake"); 
						    saveAlert.setPositiveButton("OK", null);
						    saveAlert.setCancelable(false);
						    saveAlert.create().show();
						}
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Cancelled.
						}
					});
					
					alert.show();
				}
			}
			
		});
		
		button2 = (Button) rootView.findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				synchronized (mAdapter) {
					synchronized (mLine) {
						synchronized (mMarker) {
							map.clear();
							mMarker.clear();
							mLine.clear();
							mAdapter.clear();
							mLabel.clear();
							focusOnMarker();
						}
					}
				}
			}
			
		});
	}
}
