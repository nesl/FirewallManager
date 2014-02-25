/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.ucla.ee.nesl.privacyfilter.filtermanager;

import edu.ucla.ee.nesl.privacyfilter.trace.TraceMap;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppDetailActivityWithMap extends FragmentActivity implements ActionBar.TabListener {
	
	private static AppDetailActivityWithMap activity;
	private static AppDetailFragment fragment0 = null;
	private static AnnotateFragment fragment1 = null;
	private static ViewTraceFragment fragment2 = null;
	private static LocListFragment fragment3 = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        TraceMap.initDict();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                	
                case 0:
                	if (fragment1 == null) {
                		fragment1 = new AnnotateFragment();
//                		fragment1.setContext(activity);
                	}
                	//fragment1 = new AnnotateFragment();
                	//activity.getSupportFragmentManager().beginTransaction().remove(fragment1).add(R.id.app_detail_container, fragment1).commit();
                	return fragment1;
                case 1:
                	if (fragment3 == null) {
                		fragment3 = new LocListFragment();
                		fragment3.setContext(activity);
                	}
//                	activity.getSupportFragmentManager().beginTransaction().remove(fragment3).add(R.id.app_detail_container, fragment3).commit();
                	return fragment3;
                case 2:
            		Bundle arguments = new Bundle();
        			arguments.putString(AppDetailFragment.ARG_APP_STR, activity.getIntent().getStringExtra(AppDetailFragment.ARG_APP_STR));
        			fragment0 = new AppDetailFragment();
        			fragment0.setArguments(arguments);
        			fragment0.setContext(activity);        			
//        			activity.getSupportFragmentManager().beginTransaction().remove(fragment0).add(R.id.app_detail_container, fragment0).commit();
                    return fragment0;   
                case 3:
                	if (fragment2 == null) {
                		fragment2 = new ViewTraceFragment();
                		fragment2.setContext(activity);
                	}
//                	fragment2 = new ViewTraceFragment();
//                	activity.getSupportFragmentManager().beginTransaction().remove(fragment2).add(R.id.app_detail_container, fragment2).commit();
                	return fragment2;
                	
                default:
                	return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
            	return "Annotate Trace";
            }
            else if (position == 1) {
            	return "Define Sensitive Place";
            }
            else if (position == 2) {
            	return "Playback Config";
            }
            else {
            	return "View Trace";
            }
        }
    }


    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_annotate, container, false);
            return rootView;
        }
    }
}
