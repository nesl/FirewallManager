package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.AppId;

/**
 * An activity representing a list of Apps. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link AppDetailActivity} representing item details. On tablets, the activity
 * presents the list of items and item details side-by-side using two vertical
 * panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AppListFragment} and the item details (if present) is a
 * {@link AppDetailFragment}.
 * <p>
 * This activity also implements the required {@link AppListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class AppListActivity extends FragmentActivity implements
		AppListFragment.Callbacks {

	public static final boolean CLEAR_PREFS_ON_STARTUP = false;
	public static final String PREFS_NAME = "prefs";
	//public static HashMap<String, LatLng> mapMarkers;
	public static Context context;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);
		context = this;

		// mapMarkers = new HashMap<String, LatLng>();
		// mapMarkers.put("Home", new LatLng(34.049351, -118.423852));
		// mapMarkers.put("Work", new LatLng(34.058351, -118.438852));

		if (CLEAR_PREFS_ON_STARTUP) {
			SharedPreferences prefs = getSharedPreferences("app_gui_states",
					MODE_PRIVATE);
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.clear();
			prefsEditor.apply();
		}

		if (findViewById(R.id.app_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((AppListFragment) getSupportFragmentManager().findFragmentById(
					R.id.app_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link AppListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(AppId appId) {
		String uniqueAppString = appId.generateUniqueString();
		Log.i("AppListActivity", uniqueAppString);

		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			Bundle arguments = new Bundle();
			arguments.putString(AppDetailFragment.ARG_APP_STR, uniqueAppString);
			AppDetailFragment fragment = new AppDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.app_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					AppDetailActivityWithMap.class);
			detailIntent.putExtra(AppDetailFragment.ARG_APP_STR,
					uniqueAppString);
			startActivity(detailIntent);
		}
	}

	public static void updateState() {
//		SharedPreferences settings = PreferenceManager
//				.getDefaultSharedPreferences(context);
//		SharedPreferences.Editor editor = settings.edit();
//		HashSet<String> places = new HashSet<String>();
//
//		for (Map.Entry<String, LatLng> entry : AppListActivity.mapMarkers.entrySet()) {
//			places.add(entry.getKey());
//			editor.putFloat(entry.getKey() + "1",
//					(float) entry.getValue().latitude);
//			editor.putFloat(entry.getKey() + "2",
//					(float) entry.getValue().longitude);
//		}
//		editor.putStringSet("Places", places);
//		editor.commit();
	}

	public void onResume() {
		super.onResume();
//		SharedPreferences settings = PreferenceManager
//				.getDefaultSharedPreferences(context);
//		HashSet<String> places = (HashSet<String>) settings.getStringSet("Places", null);
//		if (places != null) {
//			Log.i("tag", "Places is not null");
//			for (String str : places) {
//				LatLng ll = new LatLng(settings.getFloat(str + "1", 0),
//						settings.getFloat(str + "2", 0));
//				mapMarkers.put(str, ll);
//			}
//		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		//TODO: save state in TraceMap
//		Bundle extras = new Bundle();
//		extras.putSerializable("HashMap", mapMarkers);
//
//		savedInstanceState.putBundle("HashMap", extras);
//		Log.i("act", "save state");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
//		Bundle extras = savedInstanceState.getBundle("HashMap");
//		mapMarkers = (HashMap<String, LatLng>) extras
//				.getSerializable("HashMap");
//		Log.i("act", "read state");
	}
}
