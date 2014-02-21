package edu.ucla.ee.nesl.privacyfilter.trace;

import java.util.HashMap;
import java.util.List;

import android.os.Environment;

import edu.ucla.ee.nesl.privacyfilter.mc.locationmap.LocationMap;
import edu.ucla.ee.nesl.privacyfilter.mc.service.MarkovChainService;
import edu.ucla.ee.nesl.privacyfilter.mc.service.MarkovChainServiceImpl;

public class TraceMap {
	private static HashMap<String, Integer[]> realPath = new HashMap<String, Integer[]>();
	private static HashMap<String, Integer[]> fakePath = new HashMap<String, Integer[]>();
	private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
	
	public static void addPath(List<String> path, String name) {
		Integer[] index = new Integer[path.size()];
		for (int i = 0; i < index.length; i++) {
			index[i] = LocationMap.getPlaceIndex(path.get(i));
		}
		realPath.put(name, index);
		
        MarkovChainService markovChainService = new MarkovChainServiceImpl();
        markovChainService.createUserMC(1);
        markovChainService.createSuperMC();
        markovChainService.createProjectedMC();
        markovChainService.loadProjectedMCFromFile(SD_CARD_PATH + "/mc/projMC_SS_02.csv");                                            
        markovChainService.createResidualMC();
        
        Integer[] alternativePath = markovChainService.findAlternativePath(index);
        fakePath.put(name + "_fake", alternativePath);
	}
}
