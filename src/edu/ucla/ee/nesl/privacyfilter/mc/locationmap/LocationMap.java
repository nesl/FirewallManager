package edu.ucla.ee.nesl.privacyfilter.mc.locationmap;

import java.util.HashMap;

public class LocationMap {
	private static HashMap<String, Integer> locationMap = new HashMap<String, Integer>();
	
	public static void addPlace(String str, int index) {
		locationMap.put(str, index);
	}
	
	public static void removePlace(String str) {
		locationMap.remove(str);
	}
	
	public static int getPlaceIndex(String str) {
		return locationMap.get(str);
	}
	
	public static void initMap() {
		LocationMap.addPlace("Lab", 1);
		LocationMap.addPlace("Starbucks", 2);
		LocationMap.addPlace("Ralphs", 3);
		LocationMap.addPlace("CVS", 4);
		LocationMap.addPlace("Classroom", 5);
		LocationMap.addPlace("Home", 6);
		LocationMap.addPlace("Bombshelter", 7);
		LocationMap.addPlace("Ackerman", 8);
		LocationMap.addPlace("Seascafe", 9);
	}
}
