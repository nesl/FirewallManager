package edu.ucla.ee.nesl.privacyfilter.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;

import edu.ucla.ee.nesl.privacyfilter.filtermanager.models.Place;
import edu.ucla.ee.nesl.privacyfilter.mc.service.MarkovChainService;
import edu.ucla.ee.nesl.privacyfilter.mc.service.MarkovChainServiceImpl;

public class TraceMap {
	private static HashMap<String, Integer[]> path = new HashMap<String, Integer[]>();
	private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static HashMap<String, Integer> locationDict = new HashMap<String, Integer>();
	public static SparseArray<String> reverseLocationDict = new SparseArray<String>();
	private static SparseArray<LatLng> locationMap = new SparseArray<LatLng>();
	
	public static void addPlace(String str, LatLng loc) {
		locationMap.put(locationDict.get(str), loc);
	}
	
	public static boolean isPlaceExist(String str) {
		return (locationMap.indexOfKey(locationDict.get(str)) >= 0);
	}
	
	public static void addPlaceIndex(String str, int index) {
		locationDict.put(str, index);
		reverseLocationDict.put(index, str);
	}
	
	public static void removePlaceIndex(String str) {
		locationDict.remove(str);
	}
	
	private static int getPlaceIndex(String str) {
		if (locationDict.containsKey(str)) {
			return locationDict.get(str);
		}
		else {
			return -1;
		}
		
	}
	
	public static void initDict() {
		locationDict = new HashMap<String, Integer>();
		addPlaceIndex("Hospital", 0);
		addPlaceIndex("Lab", 1);
		addPlaceIndex("Starbucks", 2);
		addPlaceIndex("Ralphs", 3);
		addPlaceIndex("Home", 4);
		addPlaceIndex("Classroom", 5);
		addPlaceIndex("CVS", 6);
		addPlaceIndex("Nordstrom", 7);
		addPlaceIndex("Macys", 8);
		addPlaceIndex("Seascafe", 9);
	}
	
	public static void addPath(List<String> _path, String name) {
		if(locationDict == null) {
			initDict();
		}
		Integer[] index = new Integer[_path.size()];
		int i = 0;
		for (i = 0; i < index.length; i++) {
			index[i] = Integer.valueOf(getPlaceIndex(_path.get(i)));
		}
		path.put(name, index);
		
        MarkovChainService markovChainService = new MarkovChainServiceImpl();
        markovChainService.createUserMC(1);
        markovChainService.createSuperMC();
        markovChainService.createProjectedMC();
        markovChainService.loadProjectedMCFromFile(SD_CARD_PATH + "/mc/projMC_SS_02.csv");                                            
        markovChainService.createResidualMC();
        
        Integer[] alternativePath = markovChainService.findAlternativePath(index);
        path.put(name + "_fake", alternativePath);
        
        String str = "real path: ";
        for (i = 0; i < index.length; i++) {
        	str += index[i] + " -> ";
        }
        Log.i("TraceMap", str);
        
        String str1 = "fake path: ";
        for (i = 0; i < alternativePath.length; i++) {
        	str1 += alternativePath[i] + " -> ";
        }
        Log.i("TraceMap", str1);
        
        System.out.println("size of path=" + path.size());
	}
	
	public static List<String> getNameList() {
		ArrayList<String> list = new ArrayList<String>();
		for ( String key : path.keySet() ) {
			list.add(key);
		}
		return list;
	}
	
	public static Integer[] getIntPath(String name) {
		if (path.containsKey(name)) {
			return path.get(name); 
		}
		else {
			return null;
		}
	}
	
	public static List<LatLng> getLocPath(String name) {
		if (path.containsKey(name)) {
			ArrayList<LatLng> res = new ArrayList<LatLng>();
			Integer[] intPath = path.get(name);
			for (int i = 0; i < intPath.length; i++) {
				res.add(locationMap.get(intPath[i]));
			}
			return res; 
		}
		else {
			return null;
		}
	}
	
	public static List<String> getStringPath(String name) {
		if (path.containsKey(name)) {
			ArrayList<String> res = new ArrayList<String>();
			Integer[] intPath = path.get(name);
			for (int i = 0; i < intPath.length; i++) {
				res.add(reverseLocationDict.get(intPath[i]));
			}
			return res; 
		}
		else {
			return null;
		}
	}
	
	public static LatLng getLoc(String name) {
		return locationMap.get(locationDict.get(name));
	}
	
	public static List<Place> getPlaces() {
		ArrayList<Place> res = new ArrayList<Place>();
		for (String str:locationDict.keySet()) {
			Place plc = new Place(str, getLoc(str));
			res.add(plc);
		}
		return res;
	}
}
