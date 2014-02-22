package edu.ucla.ee.nesl.privacyfilter.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import edu.ucla.ee.nesl.privacyfilter.mc.service.MarkovChainService;
import edu.ucla.ee.nesl.privacyfilter.mc.service.MarkovChainServiceImpl;
import edu.ucla.ee.nesl.privacyfilter.mc.util.Utils;

public class TraceMap {
	private static HashMap<String, Integer[]> path = new HashMap<String, Integer[]>();
	//private static HashMap<String, Integer[]> fakePath = new HashMap<String, Integer[]>();
	private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	private static HashMap<String, Integer> locationMap;
	
	public static void addPlace(String str, int index) {
		locationMap.put(str, index);
	}
	
	public static void removePlace(String str) {
		locationMap.remove(str);
	}
	
	private static int getPlaceIndex(String str) {
		if (locationMap.containsKey(str)) {
			return locationMap.get(str);
		}
		else {
			return -1;
		}
		
	}
	
	private static void initMap() {
		locationMap = new HashMap<String, Integer>();
		addPlace("Hospital", 0);
		addPlace("Lab", 1);
		addPlace("Starbucks", 2);
		addPlace("Ralphs", 3);
		addPlace("Home", 4);
		addPlace("Classroom", 5);
		addPlace("CVS", 6);
		addPlace("Bombshelter", 7);
		addPlace("Ackerman", 8);
		addPlace("Seascafe", 9);
	}
	
	public static void addPath(List<String> _path, String name) {
		if(locationMap == null) {
			initMap();
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
	}
	
	public static List<String> getNameList() {
		ArrayList<String> list = new ArrayList<String>();
		for ( String key : path.keySet() ) {
			list.add(key);
		}
		return list;
	}
	
	public static Integer[] getPath(String name) {
		return path.get(name); 
	}
}
