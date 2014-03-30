package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;

public class PlaybackListAdapter extends ArrayAdapter<SensorType> implements OnClickListener {
	private final Context context;
	private final ArrayList<SensorType> sensorList;
	private int curPosition;
	private PlaybackListAdapter self;
	
	public PlaybackListAdapter(Context context, List<SensorType> sensors) {
		super(context, R.layout.playback_list_entry, sensors);
		this.context = context;
		this.sensorList = (ArrayList<SensorType>) sensors;
		self = this;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.playback_list_entry, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.playback_list_entry_name);
        TextView subTitleView = (TextView) rowView.findViewById(R.id.playback_list_entry_filename);
        Button bt = (Button) rowView.findViewById(R.id.loc_list_entry_select);
        titleView.setText(sensorList.get(position).getName());
        String fileName = sensorList.get(position).getFile();
        subTitleView.setText(fileName == null ? "No file selected." : fileName);
        bt.setText("Select File");
        bt.setTag(position);
        bt.setOnClickListener(this);
        return rowView;
	}

	@Override
	public void onClick(View v) {
		curPosition = (Integer) v.getTag();
		FileChooserDialog dialog = new FileChooserDialog(context);
		dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
	         public void onFileSelected(Dialog source, File file) {
	             source.hide();
	             sensorList.get(curPosition).setFile(file.getAbsolutePath() + file.getName());
	             self.notifyDataSetChanged();
	             Toast toast = Toast.makeText(source.getContext(), "File selected: " + file.getName(), Toast.LENGTH_LONG);
	             toast.show();
	         }
	         public void onFileSelected(Dialog source, File folder, String name) {
	             source.hide();
	             sensorList.get(curPosition).setFile(folder.getName() + "/" + name);
	             self.notifyDataSetChanged();
	             Toast toast = Toast.makeText(source.getContext(), "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
	             toast.show();
	         }
	     });
	    dialog.show();
	}
}
