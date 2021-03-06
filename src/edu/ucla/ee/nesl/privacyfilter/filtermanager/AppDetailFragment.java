package edu.ucla.ee.nesl.privacyfilter.filtermanager;

// imports {{{

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.FirewallConfigManager;
import android.support.v4.app.Fragment;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import edu.ucla.ee.nesl.privacyfilter.filtermanager.algo.InferenceSensorMapper;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.io.protobuf.FirewallConfigMessages;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppFilterData;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppId;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.Inference;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.InferenceMethod;
import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.SensorType;
// }}}

/**
 * A fragment representing a single App detail screen.
 * This fragment is either contained in a {@link AppListActivity}
 * in two-pane mode (on tablets) or a {@link AppDetailActivity}
 * on handsets.
 */
public class AppDetailFragment extends Fragment {
	private Context context;
	
	// Android boilerplate {{{

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	/**
	 * The fragment argument representing the app ID that this fragment
	 * represents.
	 */

	public static final String ARG_APP_STR = "app_id_string";
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AppDetailFragment() {
		
	}

	// }}}
		// Convenience {{{

		private static float str2float (CharSequence chs) {
			String str = new String((String) chs.toString());
			if (str.equals("")) {
				return 0f;
			} else {
				return Float.parseFloat(str);
			}
		}

		private static int str2int (CharSequence chs) {
			String str = new String((String) chs.toString());
			if (str.equals("")) {
				return 0;
			} else {
				return Integer.parseInt(str);
			}
		}

		// }}}

	// SensorTypeRule {{{

	// represents a single stipulation about what to do with one sensor-type re the
	// app in question
	// i.e., one "action" so to speak
	//
	// also manages the underlying GUI elements

	public class SensorTypeRule {
		private static final int MAX_CONSTANTS = 5;

		// Protobuf enums {{{

		//private final FirewallConfigMessages.Action.ActionType[] PROTOBUF_ACTIONS = {
		//	FirewallConfigMessages.Action.ActionType.ACTION_PASSTHROUGH,
		//	FirewallConfigMessages.Action.ActionType.ACTION_SUPPRESS,
		//	FirewallConfigMessages.Action.ActionType.ACTION_CONSTANT,
		//	FirewallConfigMessages.Action.ActionType.ACTION_DELAY,
		//	FirewallConfigMessages.Action.ActionType.ACTION_PERTURB,
		//};
		private final FirewallConfigMessages.Action.ActionType[] PROTOBUF_ACTIONS = {
			FirewallConfigMessages.Action.ActionType.ACTION_PASSTHROUGH,
			FirewallConfigMessages.Action.ActionType.ACTION_SUPPRESS,
			FirewallConfigMessages.Action.ActionType.ACTION_CONSTANT,
			FirewallConfigMessages.Action.ActionType.ACTION_PERTURB,
		};

		private final FirewallConfigMessages.Perturb.DistributionType[] PROTOBUF_DISTRIBUTIONS = {
			FirewallConfigMessages.Perturb.DistributionType.GAUSSIAN,
			FirewallConfigMessages.Perturb.DistributionType.UNIFORM,
			FirewallConfigMessages.Perturb.DistributionType.EXPONENTIAL
		};

		// }}}

		private SensorType sensorType;

		// View members {{{

		private View ruleView;
		private Spinner ruleActionView;

		private ViewGroup[] constantViews;
		private TextView[] constantNameViews;
		private TextView[] constantUnitViews;
		private TextView[] constantValueViews;
		
		private ViewGroup constantLocationView;
		private Spinner constantLocationSpinner;

		//private ViewGroup delayView;
		//private TextView delayDaysView;
		//private TextView delayHoursView;
		//private TextView delayMinutesView;
		//private TextView delaySecondsView;
		//private TextView delayMillisecondsView;

		private ViewGroup perturbView;
		private Spinner perturbDistributionView;
		private TextView perturbMeanView;
		private TextView perturbVarianceView;
		private TextView perturbMinView;
		private TextView perturbMaxView;
		private TextView perturbLambdaView;

		private ViewGroup timingView;
		private CheckBox[] dayOfWeekViews;
		private TextView fromHourView;
		private TextView toHourView;
		private TextView fromMinuteView;
		private TextView toMinuteView;
		
		private ViewGroup locationView;
		private Spinner radiusSpinner, tagSpinner;
		
		private ViewGroup externalView;
		private Spinner externalSpinner;

		// }}}

		protected SensorTypeRule (SensorType sensorType, View ruleView) {
			this.sensorType = sensorType;

			setView(ruleView);
		}

		public SensorType getSensorType() {
			return sensorType;
		}

		private void setupPerturbSpinner () { // {{{
			perturbDistributionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
					perturbMeanView.setVisibility(View.GONE);
					perturbVarianceView.setVisibility(View.GONE);
					perturbMinView.setVisibility(View.GONE);
					perturbMaxView.setVisibility(View.GONE);
					perturbLambdaView.setVisibility(View.GONE);

					switch (position) {
						case 0: // gaussian
							perturbMeanView.setVisibility(View.VISIBLE);
							perturbVarianceView.setVisibility(View.VISIBLE);
							break;
						case 1: // uniform
							perturbMinView.setVisibility(View.VISIBLE);
							perturbMaxView.setVisibility(View.VISIBLE);
							break;
						case 2: // exponential
							perturbLambdaView.setVisibility(View.VISIBLE);
							break;
					}
				}

				public void onNothingSelected (AdapterView<?> parent) {
				}
			});
		} // }}}
		private void setupActionSpinner () { // {{{
			ruleActionView = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action);

			ruleActionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
					for (int constIdx = 0; constIdx < MAX_CONSTANTS; constIdx++) {
						constantViews[constIdx].setVisibility(View.GONE);
					}
					//delayView.setVisibility(View.GONE);
					perturbView.setVisibility(View.GONE);
					timingView.setVisibility(View.GONE);
					locationView.setVisibility(View.GONE);
					constantLocationView.setVisibility(View.GONE);
					externalView.setVisibility(View.GONE);

					switch (position) {
						case 0: // no action
							break;
						case 1: // suppress
							timingView.setVisibility(View.VISIBLE);
							locationView.setVisibility(View.VISIBLE);
							externalView.setVisibility(View.VISIBLE);
							break;
						case 2: // constant
							if (sensorType.getAndroidId() == SensorType.GPS_ID) {
								constantLocationView.setVisibility(View.VISIBLE);
							}
							else {
								for (int constIdx = 0; constIdx < MAX_CONSTANTS && constIdx < sensorType.getAndroidValueNames().length; constIdx++) {
									constantViews[constIdx].setVisibility(View.VISIBLE);
								}
							}
							timingView.setVisibility(View.VISIBLE);
							locationView.setVisibility(View.VISIBLE);
							externalView.setVisibility(View.VISIBLE);
							break;
//						case 3: // delay
//							timingView.setVisibility(View.VISIBLE);
//							locationView.setVisibility(View.VISIBLE);
//							externalView.setVisibility(View.VISIBLE);
//							break;
						//case 4: // perturb
						//	perturbView.setVisibility(View.VISIBLE);
						//	timingView.setVisibility(View.VISIBLE);
						//	break;
						case 3: // perturb
							perturbView.setVisibility(View.VISIBLE);
							timingView.setVisibility(View.VISIBLE);
							locationView.setVisibility(View.VISIBLE);
							externalView.setVisibility(View.VISIBLE);
							break;
					}
				}

				public void onNothingSelected (AdapterView<?> parent) {
				}
			});
		} // }}}
		
		private void setUpLocationSpinner() {
			List<String> list = new ArrayList<String>();
			list.add("None");
			list.add("100ft");
			list.add("1000ft");
			list.add("1mile");
			list.add("5mile");
			
			List<String> listloc = new ArrayList<String>();
			listloc.add("None");
			for (Map.Entry<String, LatLng> entry : AppListActivity.mapMarkers.entrySet()) {
				listloc.add(entry.getKey());
			}
			
			if (context != null) {
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
				radiusSpinner.setAdapter(dataAdapter);
				
				ArrayAdapter<String> dataAdapterLoc = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listloc);
				tagSpinner.setAdapter(dataAdapterLoc);
			}
		}
		
		private void setupLocationConstant() {
			constantLocationView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant_location);
			constantLocationSpinner = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant_location_spinner);
			
			List<String> listloc = new ArrayList<String>();
			for (Map.Entry<String, LatLng> entry : AppListActivity.mapMarkers.entrySet()) {
				listloc.add(entry.getKey());
			}
			
			if (context != null) {				
				ArrayAdapter<String> dataAdapterLoc = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listloc);
				constantLocationSpinner.setAdapter(dataAdapterLoc);
			}
			
			constantLocationView.setVisibility(View.GONE);
		}
		
		private void setUpExternalSpinner() {
			List<String> list = new ArrayList<String>();
			list.add("None");
			list.add("Indoor");
			list.add("Outdoor");
			list.add("Driving");
			list.add("Running");
			
			if (context != null) {
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
				externalSpinner.setAdapter(dataAdapter);
			}
		}

		protected void setView (View ruleView) { // {{{
			this.ruleView = ruleView;

			TextView name = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_name);
			//TextView count = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_count);

			name.setText(sensorType.getName() + "\n\tUse count: " + app.getSensorCount(sensorType.getAndroidId()));
			
			Log.i("frag", "sensorid=" + sensorType.getAndroidId());
			Log.i("frag", "sensorname=" + sensorType.getName());
			Log.i("frag", "constantNum=" + sensorType.getAndroidValueNames().length);

			// set up this action's arguments {{{

			this.constantViews = new ViewGroup[MAX_CONSTANTS];
			this.constantNameViews = new TextView[MAX_CONSTANTS];
			this.constantUnitViews = new TextView[MAX_CONSTANTS];
			this.constantValueViews = new TextView[MAX_CONSTANTS];

			this.constantViews[0] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0);
			this.constantViews[1] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1);
			this.constantViews[2] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2);
			this.constantViews[3] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3);
			this.constantViews[4] = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4);
			this.constantNameViews[0] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0_name);
			this.constantNameViews[1] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1_name);
			this.constantNameViews[2] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2_name);
			this.constantNameViews[3] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3_name);
			this.constantNameViews[4] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4_name);
			this.constantUnitViews[0] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0_unit);
			this.constantUnitViews[1] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1_unit);
			this.constantUnitViews[2] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2_unit);
			this.constantUnitViews[3] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3_unit);
			this.constantUnitViews[4] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4_unit);
			this.constantValueViews[0] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant0_value);
			this.constantValueViews[1] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant1_value);
			this.constantValueViews[2] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant2_value);
			this.constantValueViews[3] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant3_value);
			this.constantValueViews[4] = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_constant4_value);

			// set up constant names and units as well as default values... also handle setting types
			for (int constIdx = 0; constIdx < MAX_CONSTANTS && constIdx < sensorType.getAndroidValueNames().length; constIdx++) {
				this.constantNameViews[constIdx].setText(sensorType.getAndroidValueNames()[constIdx] + ":");
				this.constantUnitViews[constIdx].setText("(" + sensorType.getAndroidValueUnits()[constIdx] + ")");
				this.constantValueViews[constIdx].setText(Float.toString(sensorType.getDefaultValues()[constIdx]));
				this.constantValueViews[constIdx].setKeyListener(DigitsKeyListener.getInstance(true, true));
			}

			//this.delayView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay);
			//this.delayDaysView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_days);
			//this.delayHoursView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_hours);
			//this.delayMinutesView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_minutes);
			//this.delaySecondsView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_seconds);
			//this.delayMillisecondsView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_delay_milliseconds);

			this.perturbView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb);
			this.perturbDistributionView = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_distribution);
			this.perturbMeanView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_mean);
			this.perturbMeanView.setKeyListener(DigitsKeyListener.getInstance(true, true));
			this.perturbVarianceView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_variance);
			this.perturbVarianceView.setKeyListener(DigitsKeyListener.getInstance(true, true));
			this.perturbMinView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_min);
			this.perturbMinView.setKeyListener(DigitsKeyListener.getInstance(true, true));
			this.perturbMaxView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_max);
			this.perturbMaxView.setKeyListener(DigitsKeyListener.getInstance(true, true));
			this.perturbLambdaView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_perturb_lambda);
			this.perturbLambdaView.setKeyListener(DigitsKeyListener.getInstance(true, true));

			timingView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing);
			dayOfWeekViews = new CheckBox[7];
			dayOfWeekViews[0] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_sunday);
			dayOfWeekViews[1] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_monday);
			dayOfWeekViews[2] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_tuesday);
			dayOfWeekViews[3] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_wednesday);
			dayOfWeekViews[4] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_thursday);
			dayOfWeekViews[5] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_friday);
			dayOfWeekViews[6] = (CheckBox) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_saturday);
			fromHourView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_fromhour);
			toHourView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_tohour);
			fromMinuteView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_fromminutes);
			toMinuteView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_timing_tominutes);
			
			locationView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_location);
			radiusSpinner = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_location_spinner_radius);
			tagSpinner = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_location_spinner_tag);
			setUpLocationSpinner();
			
			externalView = (ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_external);
			externalSpinner = (Spinner) ruleView.findViewById(R.id.fragment_app_detail_sensor_action_arguments_external_spinner);
			setUpExternalSpinner();
			// }}}

			setupActionSpinner();
			setupPerturbSpinner();
			setupLocationConstant();
		} // }}}

		protected FirewallConfigMessages.Rule genRule () { // {{{

			// build param {{{

			FirewallConfigMessages.Param.Builder paramBuilder = FirewallConfigMessages.Param.newBuilder();

			// set sensorvalue {{{

			FirewallConfigMessages.SensorValue.Builder svBuilder = FirewallConfigMessages.SensorValue.newBuilder();

			
			
			if (sensorType.getAndroidId() == SensorType.GPS_ID) {
				String label = constantLocationSpinner.getSelectedItem().toString();
				LatLng ll = AppListActivity.mapMarkers.get(label);
				svBuilder.setVecVal(FirewallConfigMessages.VectorValue.newBuilder().setLat(ll.latitude).setLon(ll.longitude).build());
			}
			else {
				svBuilder.setVecVal(
						FirewallConfigMessages.VectorValue.newBuilder()
							.setX(str2float(constantValueViews[0].getText()))
							.setY(str2float(constantValueViews[1].getText()))
							.setZ(str2float(constantValueViews[2].getText()))
							.setTheta(str2float(constantValueViews[3].getText()))
							.setAccuracy(str2float(constantValueViews[4].getText()))
							.build()
					);
					svBuilder.setScalarVal(str2float((constantValueViews[0].getText())));
			}
			// TODO timestamp is currently unused by the service

			// protobuf's "default" section is being replaced with UI-imposed defaults

			paramBuilder.setConstantValue(svBuilder.build());

			// }}}
			// set delay {{{

			//float delay = 0f;

			//delay += str2float(delayMillisecondsView.getText()) / 1000;
			//delay += str2float(delaySecondsView.getText());
			//delay += str2float(delayMinutesView.getText()) * 60;
			//delay += str2float(delayHoursView.getText()) * 3600;
			//delay += str2float(delayDaysView.getText()) * 86400;

			//paramBuilder.setDelay(delay);
			
			// }}}
			// set perturb data {{{

			FirewallConfigMessages.Perturb.Builder perturbBuilder = FirewallConfigMessages.Perturb.newBuilder();

			perturbBuilder.setDistType(PROTOBUF_DISTRIBUTIONS[perturbDistributionView.getSelectedItemPosition()]);
			perturbBuilder.setMean(str2float(perturbMeanView.getText()));
			perturbBuilder.setVariance(str2float(perturbVarianceView.getText()));
			perturbBuilder.setUnifMin(str2float(perturbMinView.getText()));
			perturbBuilder.setUnifMax(str2float(perturbMaxView.getText()));
			perturbBuilder.setExpParam(str2float(perturbLambdaView.getText()));

			paramBuilder.setPerturb(perturbBuilder.build());
			
			// }}}

			FirewallConfigMessages.Param param = paramBuilder.build();

			// }}}
			// build action {{{

			FirewallConfigMessages.Action.Builder actionBuilder = FirewallConfigMessages.Action.newBuilder();
			actionBuilder.setActionType(PROTOBUF_ACTIONS[ruleActionView.getSelectedItemPosition()]);

			actionBuilder.setParam(param);

			FirewallConfigMessages.Action action = actionBuilder.build();

			// }}}
			// build timing {{{

			FirewallConfigMessages.DateTime.Builder dtBuilder = FirewallConfigMessages.DateTime.newBuilder();
			for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
				if (dayOfWeekViews[dayIdx].isChecked()) {
					dtBuilder.addDayOfWeek(dayIdx);
				}
			}

			String fromHr = fromHourView.getText().toString();
			String fromMin = fromMinuteView.getText().toString();
			String toHr = toHourView.getText().toString();
			String toMin = toMinuteView.getText().toString();

			// values left blank are presumed zero... unless the "end time" is blank
			// then we presume it to mean "until the end of the day"
			if (toHr.equals("") && toMin.equals("")) {
				toHr = "23";
				toMin = "59";
			}

			dtBuilder.setFromHr(str2int(fromHr));
			dtBuilder.setFromMin(str2int(fromMin));
			dtBuilder.setToHr(str2int(toHr));
			dtBuilder.setToMin(str2int(toMin));

			FirewallConfigMessages.DateTime dt = dtBuilder.build();

			// }}}
			// build rule {{{

			FirewallConfigMessages.Rule.Builder ruleBuilder = FirewallConfigMessages.Rule.newBuilder();
			ruleBuilder.setRuleName("FILTERMANAGER_RULE" + Integer.toString((new java.util.Random()).nextInt(10000)));
			if (sensorType.getAndroidId() == SensorType.GPS_ID) {
				ruleBuilder.setSensorType(30);
			}
			else{
				ruleBuilder.setSensorType(sensorType.getAndroidId());
			}
			ruleBuilder.setPkgName(app.getApplicationInfo().packageName);
			ruleBuilder.setPkgUid(app.getApplicationInfo().uid);

			ruleBuilder.setAction(action);
			ruleBuilder.setDateTime(dt);

			return ruleBuilder.build();

			// }}}

		} // }}}

		protected JSONObject saveGuiState () throws JSONException { // {{{
			JSONObject state = new JSONObject();

			// make note of sensor type
			state.put("android_sensor_id", sensorType.getAndroidId());

			// store selected action
			state.put("action", ruleActionView.getSelectedItemPosition());

			// store constants
			JSONArray constants = new JSONArray();
			for (TextView cView : constantValueViews) {
				constants.put(cView.getText().toString());
			}
			state.put("constants", constants);

			// store perturb data
			state.put("perturb_distribution", perturbDistributionView.getSelectedItemPosition());
			state.put("perturb_mean", perturbMeanView.getText().toString());
			state.put("perturb_variance", perturbVarianceView.getText().toString());
			state.put("perturb_min", perturbMinView.getText().toString());
			state.put("perturb_max", perturbMaxView.getText().toString());
			state.put("perturb_lambda", perturbLambdaView.getText().toString());

			// store timing data
			JSONArray daysOfWeek = new JSONArray();
			for (int day = 0 /* 0 = sunday */; day < 7; day++) {
				daysOfWeek.put(day, dayOfWeekViews[day].isChecked());
			}
			state.put("timing_daysofweek", daysOfWeek);
			state.put("timing_fromhour", fromHourView.getText().toString());
			state.put("timing_fromminute", fromMinuteView.getText().toString());
			state.put("timing_tohour", toHourView.getText().toString());
			state.put("timing_tominute", toMinuteView.getText().toString());
			
			state.put("constant_index", constantLocationSpinner.getSelectedItemPosition());

			return state;
		} // }}}
		protected void restoreGuiState (JSONObject state) throws JSONException { // {{{
			// restore selected action
			ruleActionView.setSelection(state.getInt("action"));

			// restore constants
			JSONArray constants = state.getJSONArray("constants");;
			for (int cIdx = 0; cIdx < constantValueViews.length; cIdx++) {
				constantValueViews[cIdx].setText(constants.getString(cIdx));
			}

			// restore perturb data
			perturbDistributionView.setSelection(state.getInt("perturb_distribution"));
			perturbMeanView.setText(state.getString("perturb_mean"));
			perturbVarianceView.setText(state.getString("perturb_variance"));
			perturbMinView.setText(state.getString("perturb_min"));
			perturbMaxView.setText(state.getString("perturb_max"));
			perturbLambdaView.setText(state.getString("perturb_lambda"));

			// restore timing data
			JSONArray daysOfWeek = state.getJSONArray("timing_daysofweek");
			for (int day = 0 /* 0 = sunday */; day < 7; day++) {
				dayOfWeekViews[day].setChecked(daysOfWeek.getBoolean(day));
			}
			fromHourView.setText(state.getString("timing_fromhour"));
			fromMinuteView.setText(state.getString("timing_fromminute"));
			toHourView.setText(state.getString("timing_tohour"));
			toMinuteView.setText(state.getString("timing_tominute"));
			constantLocationSpinner.setSelection(state.getInt("constant_index"));
		} // }}}

		public void makeAllowed () { // {{{
			this.ruleActionView.setSelection(0); // 0 for "normal", i.e. passthrough/allow
		} // }}}
		public void makeDisallowed () { // {{{
			this.ruleActionView.setSelection(1); // 1 for "suppress", i.e. disallow/block

			for (int day = 0; day < 7; day++) {
				this.dayOfWeekViews[day].setChecked(true);
			}

			this.fromHourView.setText("00");
			this.toHourView.setText("00");
			this.fromMinuteView.setText("23");
			this.toMinuteView.setText("59");
		} // }}}
	}

	// }}}
	// InferenceRule {{{

	// represents a single stipulation about what to do with one inference re the
	// app in question
	// i.e., one "blacklist/whitelist rule" so to speak
	//
	// also manages the underlying GUI elements

	public class InferenceRule {
		public static final int DISALLOW = 0;
		public static final int ALLOW = 1;
		private Inference inference;
		private int selectedAction = ALLOW;

		// View members {{{

		private View ruleView;
		private TextView actionView;
		private SeekBar priorityBar;
		private TextView priorityValue;

		// }}}

		protected InferenceRule (Inference inference, View ruleView) {
			this.inference = inference;

			setView(ruleView);
		}

		public Inference getInference() {
			return inference;
		}

		private void setupActionToggler () { // {{{
			this.actionView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_inference_action);

			this.actionView.setOnClickListener(new View.OnClickListener () {
				public void onClick (View v) {
					if (getSelectedAction() == DISALLOW) {
						setSelectedAction(ALLOW);
					} else {
						setSelectedAction(DISALLOW);
					}

					updateSensorRules();
				}
			});
		} // }}}

		protected void setView (View ruleView) { // {{{
			this.ruleView = ruleView;
			((TextView) ruleView.findViewById(R.id.fragment_app_detail_inference_name)).setText(inference.getName());
			String infDesc = "\t" + inference.getInferenceMethods().get(0).getDesc() + "\n\t";
			for (InferenceMethod method : inference.getInferenceMethods()) {
				if (method.getAccuracy() != -1.0 && method.getAccuracy() != 100.0) {
					infDesc += Double.toString(method.getAccuracy()) + "% accuracy using: ";
				}
				else {
					infDesc += "Using: ";
				}
				for (SensorType sensor : method.getSensorsRequired()) {
					infDesc += sensor.getName() + "; ";
				}
				TextView methTV = new TextView(getActivity());
				methTV.setText(infDesc);
				((ViewGroup) ruleView.findViewById(R.id.fragment_app_detail_inference_methods)).addView(methTV);
			}
			
			this.priorityBar = (SeekBar) ruleView.findViewById(R.id.fragment_app_detail_inferences_priority);
			this.priorityValue = (TextView) ruleView.findViewById(R.id.fragment_app_detail_inferences_priority_label);
			
			priorityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					priorityValue.setText("\tPriority: " + Integer.toString(priorityBar.getProgress() * 10) + "%");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					
				} 
				
			});
			priorityValue.setText(priorityValue.getText() + Integer.toString(priorityBar.getProgress() * 10) + "%");
			//this.priorityView = (TextView) ruleView.findViewById(R.id.fragment_app_detail_inference_priority);

			setupActionToggler();
		} // }}}

		public int getSelectedAction () { // {{{
			return selectedAction;
		} // }}}
		public void setSelectedAction (int action) { // {{{
			selectedAction = action;

			switch (action) {
				case ALLOW:
					actionView.setText(R.string.fragment_app_detail_inference_action_allow);
					break;
				case DISALLOW:
					actionView.setText(R.string.fragment_app_detail_inference_action_disallow);
					break;
			}

			// don't ALWAYS want to update sensor rules, so don't do it here
			// ---only do it when the user physically interacts with the GUI
			// otherwise we might screw something up when restoring the GUI
			// automatically from the JSON file, or some other situation
			// where we don't actually want the system to change the
			// sensor rules and their corresponding widgets
			// around behind the user's back
		} // }}}

		// returns the priority for this rule, or -1 if the user has not set a priority
		public int getPriority () { // {{{
			return priorityBar.getProgress() * 10;
//			if (priorityView.getText().equals("")) {
//				return -1;
//			} else {
//				return str2int(priorityView.getText());
//			}
		} // }}}

		protected JSONObject saveGuiState () throws JSONException { // {{{
			JSONObject state = new JSONObject();

			// make note of inference type
			state.put("inference_id", inference.getInferenceId());

			state.put("selected_action", getSelectedAction());
			state.put("priority", priorityBar.getProgress());

			return state;
		} // }}}
		protected void restoreGuiState (JSONObject state) throws JSONException { // {{{
			setSelectedAction(state.getInt("selected_action"));
			priorityBar.setProgress(state.getInt("priority"));
			//priorityView.setText(state.getString("priority"));
		} // }}}
	}

	// }}}

	private AppFilterData app; // the app whose privacy settings we're configuring on this screen
	private View rootView;
	private ArrayList<SensorTypeRule> sensorRules = new ArrayList<SensorTypeRule>();
	private ArrayList<InferenceRule> inferenceRules = new ArrayList<InferenceRule>();
	//private SeekBar toleranceSlider;

	// this method generates a protobuf in base64 string form representing the app
	public String genProtobuf64 () { // {{{
		FirewallConfigMessages.FirewallConfig.Builder fwBuilder = FirewallConfigMessages.FirewallConfig.newBuilder();
		
		for (int sTypeIdx = 0; sTypeIdx < sensorRules.size(); sTypeIdx++) {
			FirewallConfigMessages.Rule curRule = sensorRules.get(sTypeIdx).genRule();
			fwBuilder.addRule(curRule);
		}

		FirewallConfigMessages.FirewallConfig fwConfig = fwBuilder.build();

		String serializedFirewallConfigProto = Base64.encodeToString(fwConfig.toByteArray(), Base64.DEFAULT);

		return serializedFirewallConfigProto;
	} // }}}

	// this is the method that actually invokes the algo package
	// any time any of our InferenceRule objects detects the user has changed an interface rule
	// in the GUI, the InferenceRule object will call this method to ensure that the
	// underlying sensor rules (and their GUI) stay up to date
	public void updateSensorRules () { // {{{
		HashMap<Inference, InferenceSensorMapper.InferencePreference> inferencePreferences = new HashMap<Inference, InferenceSensorMapper.InferencePreference>();
		for (InferenceRule iRule : inferenceRules) {
			int action = (iRule.getSelectedAction() == InferenceRule.ALLOW) ? InferenceSensorMapper.ALLOW : InferenceSensorMapper.DISALLOW;
			int priority = iRule.getPriority();

			InferenceSensorMapper.InferencePreference preference = new InferenceSensorMapper.InferencePreference(action, priority);
			inferencePreferences.put(iRule.getInference(), preference);
		}

		HashMap<SensorType, Integer> sensorActionMap = InferenceSensorMapper.generateSensorMap(inferencePreferences, app.getSensorsUsed());

		for (SensorType sensor : sensorActionMap.keySet()) {
			SensorTypeRule sensorRule = null;

			for (SensorTypeRule curSensorRule : sensorRules) {
				if (curSensorRule.getSensorType().equals(sensor)) {
					sensorRule = curSensorRule;
				}
			}

			if (sensorActionMap.get(sensor) == InferenceSensorMapper.ALLOW) {
				sensorRule.makeAllowed();
			} else {
				sensorRule.makeDisallowed();
			}
		}
	} // }}}


	// these guys are convenience methods that are used in the set-up of the Fragment GUI
	private ViewGroup setupSensors () { // {{{
		ViewGroup sensorViews = (ViewGroup) rootView.findViewById(R.id.fragment_app_detail_sensors);

		LayoutInflater sensorInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// for each sensor type...
		for (SensorType sensorType : app.getSensorsUsed()) {
			//prepare a View for editing of the rule for this sensor
			ViewGroup ruleView = (ViewGroup) sensorInflater.inflate(R.layout.fragment_app_detail_sensor, sensorViews, false);

			// create a rule for this sensor type
			SensorTypeRule rule = new SensorTypeRule(sensorType, ruleView);

			sensorRules.add(rule);
			sensorViews.addView(ruleView);
		}

		if (app.getSensorsUsed().size() == 0) { // there aren't any known sensors being used
			TextView emptyMsg = new TextView(getActivity());
			emptyMsg.setText("We haven't observed this app using any sensors.");
			sensorViews.addView(emptyMsg);

			// hide the apply button
			((Button) rootView.findViewById(R.id.fragment_app_detail_apply_sensors)).setVisibility(View.GONE);
		}

		return sensorViews;
	} // }}}
	private ViewGroup setupInferences () { // {{{
		ViewGroup inferenceViews = (ViewGroup) rootView.findViewById(R.id.fragment_app_detail_inferences);

		LayoutInflater infInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// for each inference...
		for (Inference inference : app.getInferences()) {
			// create a View for editing the rule for this inference
			ViewGroup infView = (ViewGroup) infInflater.inflate(R.layout.fragment_app_detail_inference, inferenceViews, false);

			// create a rule for this inference
			InferenceRule rule = new InferenceRule(inference, infView);

			inferenceRules.add(rule);
			inferenceViews.addView(infView);
		}

		if (app.getInferences().size() == 0) { // there aren't any known inferences
			TextView emptyMsg = new TextView(getActivity());
			emptyMsg.setText("We don't know of any inferences this app can make.");
			inferenceViews.addView(emptyMsg);

			// hide the apply button
			((Button) rootView.findViewById(R.id.fragment_app_detail_apply_inferences)).setVisibility(View.GONE);
		}

		return inferenceViews;
	} // }}}
	private void sendFCMData (String base64Data) { // {{{
				FirewallConfigManager fwMgr = (FirewallConfigManager) getActivity().getSystemService(Context.FIREWALLCONFIG_SERVICE);
				fwMgr.setFirewallConfig(base64Data);
	} // }}}
	private void setupApplyButtons () { // {{{
		((Button) rootView.findViewById(R.id.fragment_app_detail_apply_sensors)).setOnClickListener(new View.OnClickListener () {
			public void onClick (View v) {
				sendFCMData(genProtobuf64());
				getActivity().finish();
			}
		});

		// the "inference" apply button really just calls the "sensor" apply button... there are two buttons simply because the layout requires it
		((Button) rootView.findViewById(R.id.fragment_app_detail_apply_inferences)).setOnClickListener(new View.OnClickListener () {
			public void onClick (View v) {
				String serializedFirewallConfigProto = genProtobuf64();

				FirewallConfigManager fwMgr = (FirewallConfigManager) getActivity().getSystemService(Context.FIREWALLCONFIG_SERVICE);
				fwMgr.setFirewallConfig(serializedFirewallConfigProto);

				getActivity().finish();
			}
		});
	} // }}}
	private void setupToggler () { // {{{
		TextView toggler = (TextView) rootView.findViewById(R.id.fragment_app_detail_viewtoggle);
		toggler.setOnClickListener(new View.OnClickListener () {
			public void onClick (View v) {
				TextView toggler = (TextView) rootView.findViewById(R.id.fragment_app_detail_viewtoggle);

				View inf = rootView.findViewById(R.id.fragment_app_detail_inferences_scroll);
				View sen = rootView.findViewById(R.id.fragment_app_detail_sensors_scroll);

				if (inf.getVisibility() == View.VISIBLE) {
					inf.setVisibility(View.GONE);
					sen.setVisibility(View.VISIBLE);
					toggler.setText(R.string.fragment_app_detail_viewtoggle_showingsensors);
				} else if (sen.getVisibility() == View.VISIBLE) {
					sen.setVisibility(View.GONE);
					inf.setVisibility(View.VISIBLE);
					toggler.setText(R.string.fragment_app_detail_viewtoggle_showinginferences);
				}
			}
		});
	} // }}}
//	private SeekBar setupSensorToleranceSlider() { // {{{
//		SeekBar toleranceSlider = (SeekBar) rootView.findViewById(R.id.fragment_app_detail_inferences_sensortolerance);
//
//		toleranceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener () {
//			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				((TextView) rootView.findViewById(R.id.fragment_app_detail_inferences_sensortolerance_label)).setText("Sensor tolerance: " + Integer.toString(progress));
//			}
//
//			public void onStartTrackingTouch(SeekBar seekBar) {
//			}
//
//			public void onStopTrackingTouch(SeekBar seekBar) {
//			}
//		});
//
//		return toleranceSlider;
//	} // }}}

	// onCreate {{{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_APP_STR)) {
			AppId appId = new AppId(getArguments().getString(ARG_APP_STR));

			app = new AppFilterData(getActivity(), appId.getApplicationInfo(getActivity().getPackageManager(), 0));
		}
	}

	// }}}
	// onCreateView {{{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_app_detail, container, false);

		if (app == null) {
			Log.wtf(getClass().toString(), "Was instructed to show detail on an app but app was given as null");
		}

		((TextView) rootView.findViewById(R.id.fragment_app_detail_title)).setText(app.toString());
		((ImageView) rootView.findViewById(R.id.fragment_app_detail_icon)).setImageDrawable(app.getIcon());
		//((TextView) rootView.findViewById(R.id.fragment_app_detail_subtitle)).setText("Sensors: Acl Gyr Loc Mic");

		
		
		setupSensors();
		setupInferences();
		setupApplyButtons();
		setupToggler();
		//toleranceSlider = setupSensorToleranceSlider();

		return rootView;
	}

	// }}}
	
	public JSONObject storeGuiState () throws JSONException { // {{{
		JSONObject state = new JSONObject();

		// record MY version name (version name of this instance of FilterManager)
		// for compatability reasons in the gui state string
		String fmVersionName = "(undefined package version)";
		try {
			fmVersionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException pmE) {
			Log.wtf(getClass().toString(), "My own package was not found in the package manager");
			return null;
		}
		
		state.put("filtermanager_version", fmVersionName);

		JSONArray serializedSensorRules = new JSONArray();
		for (SensorTypeRule sRule : sensorRules) {
			serializedSensorRules.put(sRule.saveGuiState());
		}
		state.put("sensor_rules", serializedSensorRules);

		JSONArray serializedInferenceRules = new JSONArray();
		for (InferenceRule iRule : inferenceRules) {
			serializedInferenceRules.put(iRule.saveGuiState());
		}
		state.put("inference_rules", serializedInferenceRules);

		//state.put("sensor_tolerance", toleranceSlider.getProgress());

		return state;
	} // }}}
	public void restoreGuiState (JSONObject state) throws JSONException { // {{{
		// check version for compatibility
		String fmVersionName = "(undefined package version)";
		try {
			fmVersionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException pmE) {
			Log.wtf(getClass().toString(), "My own package was not found in the package manager");
			return;
		}

		if (state.getString("filtermanager_version").equals("(undefined package version)") || (! state.getString("filtermanager_version").equals(fmVersionName))) {
			Log.e(getClass().toString(), "Stored GUI state is from an out-of-date version or is unrecognized");
			return;
		}

		JSONArray serializedSensorRules = state.getJSONArray("sensor_rules");
		for (int sRuleIdx = 0; sRuleIdx < serializedSensorRules.length(); sRuleIdx++) {
			// make sure we have the right sensor before restoring the rule gui state...
			for (int ruleIdx = 0; ruleIdx < sensorRules.size(); ruleIdx++) {
				if (sensorRules.get(ruleIdx).getSensorType().getAndroidId() == serializedSensorRules.getJSONObject(sRuleIdx).getInt("android_sensor_id")) {
					sensorRules.get(ruleIdx).restoreGuiState(serializedSensorRules.getJSONObject(sRuleIdx));
				}
			}
		}

		JSONArray serializedInferenceRules = state.getJSONArray("inference_rules");
		for (int iRuleIdx = 0; iRuleIdx < serializedInferenceRules.length(); iRuleIdx++) {
			// make sure we have the right infernece before restoring the rule gui state...
			for (int ruleIdx = 0; ruleIdx < inferenceRules.size(); ruleIdx++) {
				if (inferenceRules.get(ruleIdx).getInference().getInferenceId() == serializedInferenceRules.getJSONObject(iRuleIdx).getInt("inference_id")) {
					inferenceRules.get(ruleIdx).restoreGuiState(serializedInferenceRules.getJSONObject(iRuleIdx));
				}
			}
		}

		//toleranceSlider.setProgress(state.getInt("sensor_tolerance"));
	} // }}}

	public void onPause() { // {{{
		super.onPause();
		
		String guiStateString = "";

		try {
			guiStateString = storeGuiState().toString();
		} catch (JSONException jsE) {
			Log.e(getClass().toString(), "Error saving GUI state");
			return;
		}

		SharedPreferences prefs = getActivity().getSharedPreferences("app_gui_states", Context.MODE_PRIVATE);

		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(app.getPackageName(), guiStateString);
		prefsEditor.apply();
	} // }}}
	public void onResume() { // {{{
		super.onResume();
		
		SharedPreferences prefs = getActivity().getSharedPreferences("app_gui_states", Context.MODE_PRIVATE);
		String guiStateString = prefs.getString(app.getPackageName(), "");
		if (guiStateString.equals("")) {
			return;
		}

		try {
			JSONObject guiState = new JSONObject(guiStateString);
			restoreGuiState(guiState);
		} catch (JSONException jsE) {
			Log.e(getClass().toString(), "Error reading saved GUI state");
			return;
		}
	} // }}}
}
