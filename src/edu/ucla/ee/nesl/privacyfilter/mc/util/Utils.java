package edu.ucla.ee.nesl.privacyfilter.mc.util;

import edu.ucla.ee.nesl.privacyfilter.mc.domain.MarkovChain;

import java.io.File;
import java.util.Scanner;

/**
 * User: nazir
 * Date: Jan 27, 2014
 * Time: 10:30:20 PM
 */
public class Utils {
    public static int MAX_NODE = 15;                          // total location
    public static int TIME_INTERVAL = 5;                      // 5 mins
    public static int TOTAL_TIME_SLOT = 24*60/ TIME_INTERVAL;

    // just generate a dummy path for testing
    //TODO
    public static int[] generatePath() {
        return new int[]{0, 2, 2, 2, 2, 2, 2, 2, 2};
    }

    //TODO
    public static void PrintPath(String msg, int[] path) {
        System.out.print(msg+": ");
        for(int i=0; i<path.length; i++)
            System.out.print(path[i]+" -> ");
        System.out.println();


        
    }

    public static void createGraph(File inputFile) throws Exception{
        MarkovChain projectedGraph = new MarkovChain();
        double[][][] G = projectedGraph.AdjMatrix;

        Scanner input = new Scanner(inputFile);
        while (input.hasNext()) {
            String s = input.next();
            String[] tok = s.split(",");
            int from = Integer.parseInt(tok[0]);
            int to = Integer.parseInt(tok[1]);
            int t = Integer.parseInt(tok[3]);
            double p = Double.parseDouble(tok[2]);
            G[from][to][t] = p;
        }


    }

    static public void printMC(int[][][] MC){
        for(int u=0; u<MAX_NODE; u++)
            for(int v=0; v<MAX_NODE; v++)
                for(int t=0; t<TOTAL_TIME_SLOT; t++)
                    System.out.println(u+ " " + v+" "+t+" "+MC[u][v][t]);

    }

    
}
