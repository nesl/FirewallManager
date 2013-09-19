package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExternalContextFragment extends Fragment {
	private View rootView;
	private Fragment fragment;
	private TextView list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_external, container, false);
		list = (TextView) rootView.findViewById(R.id.fragment_external_list);
		list.setText("\tExternal Context\t\t\t\t\t\tTrigger Intent" +
			"\n\n\t" + "Indoor" + "\t\t\t\t\t" + "android.intent.action.INDOOR" + 
			"\n\n\t" + "Outdoor" + "\t\t\t\t\t" + "android.intent.action.OUTDOOR" + 
			"\n\n\t" + "Driving" + "\t\t\t\t\t" + "android.intent.action.DRIVING" + 
			"\n\n\t" + "Running" + "\t\t\t\t\t" + "android.intent.action.RUNNING");
		Button bt1 = (Button) rootView.findViewById(R.id.fragment_external_add);
		fragment = this;
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(fragment.getActivity());

				alert.setTitle("New external context");
				//alert.setMessage("Please name this new place:");
				LinearLayout ll1 = new LinearLayout(fragment.getActivity());
				ll1.setOrientation(1);
				TextView t1 = new TextView(fragment.getActivity());
				t1.setText("Name of external context:");
				final EditText input1 = new EditText(fragment.getActivity());
				TextView t2 = new TextView(fragment.getActivity());
				t2.setText("Action of trigger intent:");
				final EditText input2 = new EditText(fragment.getActivity());
				ll1.addView(t1);
				ll1.addView(input1);
				ll1.addView(t2);
				ll1.addView(input2);
				
				alert.setView(ll1);

				alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								list.setText(list.getText() + "\n\n\t" + input1.getText() + "\t\t\t\t\t\t" + input2.getText());
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
		return rootView;
	}
	
//	public static class InputDialog extends DialogFragment {
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//		    final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());    
//		
//		    LinearLayout lila1= new LinearLayout(this.getActivity());
//		    lila1.setOrientation(1);
//		    final EditText input = new EditText(this.getActivity()); 
//		    final EditText input1 = new EditText(this.getActivity());
//		    lila1.addView(input);
//		    lila1.addView(input1);
//		    alert.setView(lila1);
//		    
//		    alert.setTitle("Login");
//		
//		    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {             
//		        public void onClick(DialogInterface dialog, int whichButton) {              
//		            String value = input.getText().toString().trim();
//		        }
//		    });                 
//		    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {                           
//		        public void onClick(DialogInterface dialog, int whichButton) {          
//		            dialog.cancel();    
//		        }     
//		    });
//		    
//		    return alert.create();      
//	    } 
//	}
//	

}
