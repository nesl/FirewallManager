package edu.ucla.ee.nesl.privacyfilter.mc.domain;

import edu.ucla.ee.nesl.privacyfilter.mc.util.Utils;

import java.util.Map;
import java.util.HashMap;

/**
 * User: nazir
 * Date: Jan 27, 2014
 * Time: 10:28:01 PM
 */
public class MarkovChain {
    public double[][][] AdjMatrix = new double[Utils.MAX_NODE][Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];
    public Map<Integer, String> indexToLocationName = new HashMap<Integer, String>();
    public Map<String, Integer> nameToLocationIndex = new HashMap<String, Integer>();

}
