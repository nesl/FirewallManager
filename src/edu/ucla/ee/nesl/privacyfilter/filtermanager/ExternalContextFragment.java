package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ExternalContextFragment extends Fragment {
	private View rootView;
	private TextView list;
	private EditText input1, input2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_external, container, false);
		input1 = (EditText) rootView.findViewById(R.id.fragment_external_input1);
		input2 = (EditText) rootView.findViewById(R.id.fragment_external_input2);
		list = (TextView) rootView.findViewById(R.id.fragment_external_list);
		list.setText("\tExternal Context\t\t\t\t\t\tTrigger Intent" +
			"\n\n\t" + "Indoor" + "\t\t\t\t\t" + "android.intent.action.INDOOR" + 
			"\n\n\t" + "Outdoor" + "\t\t\t\t\t" + "android.intent.action.OUTDOOR" + 
			"\n\n\t" + "Driving" + "\t\t\t\t\t" + "android.intent.action.DRIVING" + 
			"\n\n\t" + "Running" + "\t\t\t\t\t" + "android.intent.action.RUNNING");
		Button bt1 = (Button) rootView.findViewById(R.id.fragment_external_add);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				list.setText(list.getText() + "\n\n\t" + input1.getText() + "\t\t\t\t\t\t" + input2.getText());
			}
			
		});
		return rootView;
	}
}
