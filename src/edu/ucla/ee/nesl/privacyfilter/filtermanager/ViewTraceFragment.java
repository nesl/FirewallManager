package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_viewtrace, container, false);
		mMarker = new ArrayList<Marker>();
		mLine = new ArrayList<Polyline>();
		traceSpinner = (Spinner) rootView.findViewById(R.id.view_trace_spinner);
		setupSpinner();
		map = ((SupportMapFragment)this.getActivity().getSupportFragmentManager().findFragmentById(R.id.map1)).getMap();
		setButtonListener();
		focusOnMarker();
		return rootView;
	}
	
	public void onResume() {
		super.onResume();
		setupSpinner();
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
						String pathName = (String) traceSpinner.getSelectedItem();
						List<LatLng> selectedPath = TraceMap.getLocPath(pathName);
						List<String> pathLabel = TraceMap.getStringPath(pathName);
						for (int i = 0; i < selectedPath.size(); i++) {
							LatLng newll = selectedPath.get(i);
							mMarker.add(map.addMarker(new MarkerOptions().position(newll).draggable(true).visible(true).title(pathLabel.get(i))));
							if (i > 0) {
								Polyline line = map.addPolyline(new PolylineOptions()
							     .add(mMarker.get(i- 1).getPosition(), mMarker.get(i).getPosition())
							     .width(5)
							     .color(Color.BLUE));
								 mLine.add(line);
							}

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
					setupSpinner();
				}
			}
			
		});
	}
}
