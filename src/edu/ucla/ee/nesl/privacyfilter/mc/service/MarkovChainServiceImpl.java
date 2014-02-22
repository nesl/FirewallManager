package edu.ucla.ee.nesl.privacyfilter.mc.service;

import edu.ucla.ee.nesl.privacyfilter.mc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;

import edu.ucla.ee.nesl.privacyfilter.mc.domain.MCNode;

/**
 * User: nazir
 * Since: Jan 28, 2014
 */
public class MarkovChainServiceImpl implements MarkovChainService {

    public MarkovChainServiceImpl() {

    }

    public MarkovChainServiceImpl(File dataDirectory) {

    }

    //TODO
    public void createUserMC(int userId) {

    }

    //TODO
    public void createSuperMC() {
    }

    //TODO
    public void createProjectedMC() {
    }

    //TODO
    public void createResidualMC() {
        createResidualMC(projectedGraph.AdjMatrix, sensitiveStates.sensitiveNodeInt);
    }

    //TODO
    public void createResidualMC(double[][][] projectedG, int[][] sensitiveStates) {
        Queue<MCNode> Q = new LinkedList<MCNode>();

        int[][] inDegree = new int[Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];
        int[][] outDegree = new int[Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];

     
        for (int t = 0; t < Utils.TOTAL_TIME_SLOT; t++)
            for (int u = 0; u < Utils.MAX_NODE; u++)
                for (int v = 0; v < Utils.MAX_NODE; v++)
                    if (projectedG[u][v][t] > 0) {
                        residualGraph.AdjMatrix[u][v][t] = projectedG[u][v][t];
//                        inDegree[v][t + 1]++;
                        outDegree[u][t]++;
                    }

        for (int i = 0; i < Utils.MAX_NODE; i++)
            for (int t = 0; t < Utils.TOTAL_TIME_SLOT; t++) {
                if (sensitiveStates[i][t] == 1) {
                    MCNode nd = new MCNode(i, t);
                    Q.add(nd);
                }
            }
        while (!Q.isEmpty()) {
            MCNode nd = Q.remove();

            for (int u = 0; u < Utils.MAX_NODE; u++) {
                if (projectedG[u][nd.x][nd.t - 1] > 0) {
                    residualGraph.AdjMatrix[u][nd.x][nd.t - 1] = 0;
                    outDegree[u][nd.t - 1]--;
                    if (outDegree[u][nd.t - 1] == 0) {
                        Q.add(new MCNode(u, nd.t - 1));
                    }
                }
//                if (projectedG[nd.x][u][nd.t] > 0) {
//                    residualGraph.AdjMatrix[nd.x][u][nd.t] = 0;
//                    inDegree[u][nd.t + 1]++;
//                    if (inDegree[u][nd.t + 1] == 0) {
//                        Q.add(new MCNode(u, nd.t + 1));
//                    }
//                }
            }
        }
        System.out.println("ResidualMC");
        printMC(residualGraph.AdjMatrix);

    }

    public Integer[] findAlternativePath(Integer[] actualPath) {
        Integer[] alternativePath = new Integer[Utils.TOTAL_TIME_SLOT];
        alternativePath[1]=actualPath[1];
        System.out.println(Utils.TOTAL_TIME_SLOT);
        for (int t = 2; t < Utils.TOTAL_TIME_SLOT; t++) {
            int currentNode = t < actualPath.length ? actualPath[t] : Integer.valueOf(0);
            if (sensitiveStates.sensitiveNodeInt[currentNode][t] == 1) {
                alternativePath[t] = getSafeNode(alternativePath[t - 1], currentNode, t);

            } else if (residualGraph.AdjMatrix[alternativePath[t - 1]][currentNode][t-1] > 0) {
                alternativePath[t] = currentNode;
            } else {
                alternativePath[t] = getSafeNode(alternativePath[t - 1], currentNode, t);

            }
        }

        return alternativePath;  //To change body of implemented methods use File | Settings | File Templates.
    }
    Integer[] resultAltPath = null;
    double maxPathProbability = 0;

    int probablePoint = -1;

    private int getSafeNode(int previousNode, int currentSensitiveNode, int t) {

        Integer[] path = new Integer[Utils.TOTAL_TIME_SLOT];
        path[t - 1] = previousNode;

        for (int depth = t + 1; depth < Utils.TOTAL_TIME_SLOT; depth++) {
            probablePoint = -1;
            maxPathProbability=0;
            probableIntersectionPoint(currentSensitiveNode, t, depth, 1);
            int v=probablePoint;
            resultAltPath = null;
            maxPathProbability =0;
            findShortestPath(v, t - 1, depth, path, 1);

            if (resultAltPath != null) {
                return resultAltPath[t];
            }
        }
        return 0;
    }

    void probableIntersectionPoint(int currentNode, int t, int maxTime, double p) {
        if (t == maxTime) {
            if(p>maxPathProbability) {
                probablePoint = currentNode;
            }
        }

        for (int locIndex = 0; locIndex < Utils.MAX_NODE; locIndex++) {
            if (projectedGraph.AdjMatrix[currentNode][locIndex][t] > 0) {
                probableIntersectionPoint(locIndex, t + 1, maxTime, p * projectedGraph.AdjMatrix[currentNode][locIndex][t]);
            }
        }   
    }


/*
    int probableIntersectionPointPre(int currentNode, int t, int maxTime, double p) {
        if (t == maxTime) {
            return currentNode;
        }
        double maxProbability = 0;
        int probLoc = 0;
        for (int locIndex = 0; locIndex < Utils.MAX_NODE; locIndex++) {
            if (projectedGraph.AdjMatrix[currentNode][locIndex][t] > maxProbability) {
                maxProbability = projectedGraph.AdjMatrix[currentNode][locIndex][t];
                probLoc = locIndex;
            }
        }
        return probableIntersectionPointPre(probLoc, t + 1, maxTime, p * maxProbability);
    }
*/




    private void findShortestPath(int destinationNode
            , int t                   // current time
            , int maxTime             // time when we merge with destinationNode
            , Integer[] currentPath       // current path
            , double p) {

        int currentNode = currentPath[t];
        if (t == maxTime) {
            if (currentNode == destinationNode) {

                if (p > maxPathProbability) {
                    maxPathProbability = p;
                    resultAltPath= currentPath.clone();
                }
            }
            return;
        }

        for (int locIndex = 0; locIndex < Utils.MAX_NODE; locIndex++) {
            if (residualGraph.AdjMatrix[currentNode][locIndex][t] > 0) {
                currentPath[t + 1] = locIndex;
                findShortestPath(destinationNode, t + 1, maxTime, currentPath
                        , p * residualGraph.AdjMatrix[currentNode][locIndex][t]);
            }
        }

    }

/*
    private void findShortestPathPre(int destinationNode
            , int t                   // current time
            , int maxTime             // time when we merge with destinationNode
            , Integer[] currentPath       // current path
            , double p, Double maxP   //
            , Integer[] resultPath) {
        int currentNode = currentPath[t];
//        System.out.println("<<"+currentNode+","+t+","+maxTime+","+p+","+maxP+","+">>");
        if (t == maxTime) {
            if (currentNode == destinationNode) {


                if (p > maxPathProbability) {

                    maxP = p;
                    maxPathProbability = p;
                    resultPath = currentPath.clone();
                    resultAltPath= currentPath.clone();
                }
            }
            return;
        }

        for (int locIndex = 0; locIndex < Utils.MAX_NODE; locIndex++) {
            if (residualGraph.AdjMatrix[currentNode][locIndex][t] > 0) {
                currentPath[t + 1] = locIndex;
                findShortestPathPre(destinationNode, t + 1, maxTime, currentPath, p * residualGraph.AdjMatrix[currentNode][locIndex][t]
                        , maxP, resultPath);
            }
        }

    }
*/


    public void loadProjectedMCFromFile(String fileName) {

//        double[][][] projG = new double[Utils.MAX_NODE][Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];
        try {
            File file = new File(fileName);
            Scanner sc = new Scanner(file);
            String tokens[] = sc.nextLine().split(",");
            int N = Integer.parseInt(tokens[0]);
            int T = Integer.parseInt(tokens[1]);
            Utils.MAX_NODE = N;
            Utils.TOTAL_TIME_SLOT = T + 1;
            double[][][] projG = new double[Utils.MAX_NODE][Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];
            int M = Integer.parseInt(tokens[2]);
            for (int i = 0; i < M; i++) {
                tokens = sc.nextLine().split(",");
                int u = Integer.parseInt(tokens[0]);
                int v = Integer.parseInt(tokens[1]);
                int t = Integer.parseInt(tokens[2]);
                double p = Double.parseDouble(tokens[3]);
                projG[u][v][t] = p;
            }
            int[][] SS = new int[Utils.MAX_NODE][Utils.TOTAL_TIME_SLOT];
            tokens = sc.nextLine().split(",");
            int K = Integer.parseInt(tokens[0]);
            for (int j = 0; j < K; j++) {
                tokens = sc.nextLine().split(",");


                int u = Integer.parseInt(tokens[0]);
                int t = Integer.parseInt(tokens[1]);
                SS[u][t] = 1;
            }
            projectedGraph.AdjMatrix = projG;
            sensitiveStates.sensitiveNodeInt = SS;
        } catch (FileNotFoundException e) {

        }
        System.out.println("Done");
        System.out.println("projectedGraphMC");
        printMC(projectedGraph.AdjMatrix);
    }

    public void printMC(double[][][] MC) {
        for (int t = 0; t < Utils.TOTAL_TIME_SLOT; t++)
            for (int u = 0; u < Utils.MAX_NODE; u++)
                for (int v = 0; v < Utils.MAX_NODE; v++)

                    if (MC[u][v][t] > 0)
                        System.out.println(u + " " + v + " " + t + " " + MC[u][v][t]);

    }
}
