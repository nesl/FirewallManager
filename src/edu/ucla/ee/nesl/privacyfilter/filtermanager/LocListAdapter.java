package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.Place;

public class LocListAdapter extends ArrayAdapter<Place> {
	private final Context context;
	private final ArrayList<Place> placeList;

	public LocListAdapter(Context context, List<Place> locs) {
		super(context, R.layout.location_list_entry, locs);
		this.context = context;
		this.placeList = (ArrayList<Place>) locs;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.location_list_entry, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.loc_list_entry_title);
        TextView subTitleView = (TextView) rowView.findViewById(R.id.loc_list_entry_subtitle);
        TextView locView = (TextView) rowView.findViewById(R.id.loc_list_entry_loc);
        
        titleView.setText(placeList.get(position).getName());
        subTitleView.setText((placeList.get(position).isSensitive() ? "Sensitive. " : "Non-sensitive. ") + placeList.get(position).getEffectTime());
        locView.setText("Lat=XX.XX, Long=XX.XX");
        
        return rowView;
	}
}
