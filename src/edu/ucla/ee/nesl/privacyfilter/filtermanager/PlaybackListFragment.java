package edu.ucla.ee.nesl.privacyfilter.filtermanager;
import java.util.ArrayList;

import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;


public class PlaybackListFragment extends ListFragment {
	private PlaybackListAdapter mAdapter;
	private Context context;
	private ArrayList<SensorType> sensorList;


	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	private void initSensorList() {
		sensorList = new ArrayList<SensorType>();
		for (int i = 1; i <= 10; i++) {
			sensorList.add(SensorType.defineFromAndroid(i, context));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this.getContext();
        initSensorList();
        mAdapter = new PlaybackListAdapter(getActivity(), sensorList);
        setListAdapter(mAdapter);
	}
}
