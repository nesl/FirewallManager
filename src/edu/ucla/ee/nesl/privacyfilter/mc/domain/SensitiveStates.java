package edu.ucla.ee.nesl.privacyfilter.mc.domain;

import edu.ucla.ee.nesl.privacyfilter.mc.util.Utils;

import java.util.Set;
import java.util.HashSet;

/**
 * User: nazir
 * Date: Jan 27, 2014
 * Time: 11:13:20 PM
 */

public class SensitiveStates {
    public Set<String> sensitiveNodes = new HashSet<String>();
    public int[][] sensitiveNodeInt = new int[Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];

}

