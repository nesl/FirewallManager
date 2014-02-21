package edu.ucla.ee.nesl.privacyfilter.mc.service;

import edu.ucla.ee.nesl.privacyfilter.mc.domain.MarkovChain;
import edu.ucla.ee.nesl.privacyfilter.mc.domain.SensitiveStates;

/**
 * User: nazir
 * Date: Jan 28, 2014
 * Time: 8:38:49 PM
 */
public interface MarkovChainService {

    public MarkovChain userMC = new MarkovChain();
    public MarkovChain superMC = new MarkovChain();
    public MarkovChain projectedGraph = new MarkovChain();
    public MarkovChain residualGraph = new MarkovChain();

    public SensitiveStates sensitiveStates = new SensitiveStates();

    void createUserMC(int userId);
    void createSuperMC();   //
    void createProjectedMC();
    void createResidualMC();

    Integer[] findAlternativePath(Integer[] actualPath);

    void loadProjectedMCFromFile(String fileName);
}
