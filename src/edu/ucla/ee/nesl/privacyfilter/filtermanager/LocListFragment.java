package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.Place;
import edu.ucla.ee.nesl.privacyfilter.trace.TraceMap;

public class LocListFragment extends ListFragment {
	private LocListAdapter mAdapter;
	//private View editView;
	private int mPosition;
	private Spinner mSpinner;
	private Context context;
	private TextView mText;
	
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Place> placeList = (ArrayList<Place>) TraceMap.getPlaces();
        mAdapter = new LocListAdapter(getActivity(), placeList);
        setListAdapter(mAdapter);
	}

	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		mPosition = position;
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		LayoutInflater sensorInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View editView = sensorInflater.inflate(R.layout.place_edit_dialog, null);
		mSpinner = (Spinner) editView.findViewById(R.id.place_edit_dialog_sensitive_spinner);
		mText = (TextView) editView.findViewById(R.id.place_edit_dialog_sensitive_label);
		mText.setText(mAdapter.getItem(mPosition).getName() + " sensitive?");
		dialog.setView(editView);
		
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAdapter.getItem(mPosition).setSensitive((mSpinner.getSelectedItemPosition() == 0));
				mAdapter.getItem(mPosition).setEffectTime("All Time");
				mAdapter.notifyDataSetChanged();
			}

		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}

		});
		dialog.show();
	}
}
