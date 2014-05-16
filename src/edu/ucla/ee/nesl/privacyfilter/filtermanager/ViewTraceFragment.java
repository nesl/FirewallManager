package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import edu.ucla.ee.nesl.privacyfilter.trace.TraceMap;

public class ViewTraceFragment extends Fragment {
	private View rootView;
	private Button button1, button2;
	private GoogleMap map;
	private ArrayList<Marker> mMarker;
	private Spinner traceSpinner;
	private ArrayList<Polyline> mLine;
	private Context context;
	private ViewTraceFragment fragment;
	private LatLng newll;
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = this;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_viewtrace, container, false);
		mMarker = new ArrayList<Marker>();
		mLine = new ArrayList<Polyline>();
		traceSpinner = (Spinner) rootView.findViewById(R.id.view_trace_spinner);
		//setupSpinner();
		map = ((SupportMapFragment)this.getActivity().getSupportFragmentManager().findFragmentById(R.id.map1)).getMap();
		
		
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

								mMarker.add(map.addMarker(new MarkerOptions().position(newll).draggable(true).visible(true).title(value)));
								Log.d("MarkerMap", "add new entry " + value + " lat=" + newll.latitude + " lon=" + newll.longitude);
								
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
	
	public void onResume() {
		super.onResume();
		//setupSpinner();
	}
	
	private void setupSpinner() {
		List<String> list = new ArrayList<String>(TraceMap.getNameList());		
		System.out.println("List size=" + TraceMap.getNameList().size());
		if (context != null) {
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
			traceSpinner.setAdapter(dataAdapter);
		}
	}
	
	private void focusOnMarker() {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(34.0722, -118.4441));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		map.moveCamera(center);
		map.animateCamera(zoom);
	}
	
	private void setButtonListener() {
		button1 = (Button) rootView.findViewById(R.id.view_button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				synchronized (mMarker) {
					if (traceSpinner.getSelectedItemPosition() >= 0) {
						
						ArrayList<LatLng> mTrace = new ArrayList<LatLng>();
						//ArrayList<String> mLabel = new ArrayList<String>();
						
//						mTrace.add(new LatLng(34.228126, -118.562307));
//						mTrace.add(new LatLng(34.22039, -118.562307));
//						mTrace.add(new LatLng(34.220532, -118.553638));
//						mTrace.add(new LatLng(34.221455, -118.506088));
//						mTrace.add(new LatLng(34.221313, -118.473473));
//						mTrace.add(new LatLng(34.257358, -118.472271));
//						mTrace.add(new LatLng(34.257216, -118.456221));
						
						mTrace.add(new LatLng(34.228126, -118.562307));
						mTrace.add(new LatLng(34.22039, -118.562307));
						mTrace.add(new LatLng(34.220532, -118.553638));
						mTrace.add(new LatLng(34.201155, -118.553638));
						
						// reroute to avoid hospital
						mTrace.add(new LatLng(34.201029, -118.527203));
						mTrace.add(new LatLng(34.186760, -118.527288));
						mTrace.add(new LatLng(34.186760, -118.501196));
						mTrace.add(new LatLng(34.201242, -118.501282));
											
						mTrace.add(new LatLng(34.201297, -118.475018));
						mTrace.add(new LatLng(34.257358, -118.472271));
						mTrace.add(new LatLng(34.257216, -118.456221));
						
						// Mark hospital and coffee shop
						mMarker.add(map.addMarker(new MarkerOptions().position(new LatLng(34.201242, -118.512783)).draggable(true).visible(true).title("Hospital")));
						
						//String pathName = (String) traceSpinner.getSelectedItem();
						//List<LatLng> selectedPath = TraceMap.getLocPath(pathName);
						//List<String> pathLabel = TraceMap.getStringPath(pathName);
						for (int i = 1; i < mTrace.size(); i++) {
							//LatLng newll = mTrace.get(i);
							//mMarker.add(map.addMarker(new MarkerOptions().position(newll).draggable(true).visible(true)));
							//if (i > 0) {
								Polyline line = map.addPolyline(new PolylineOptions()
							     .add(mTrace.get(i- 1), mTrace.get(i))
							     .width(5)
							     .color(Color.BLUE));
								 mLine.add(line);
							//}
						}
					}
				}
			}
			
		});
		
		button2 = (Button) rootView.findViewById(R.id.view_button2);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				synchronized (mMarker) {
					map.clear();
					mMarker.clear();
					mLine.clear();
					focusOnMarker();
					//setupSpinner();
				}
			}
			
		});
	}
}
