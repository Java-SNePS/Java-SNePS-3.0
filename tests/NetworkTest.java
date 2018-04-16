package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sneps.exceptions.CustomException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Base;

import static org.junit.Assert.*;

public class NetworkTest {
     Semantic semantic;
    final static String semanticType = "PropositionNode";

    @Before
    public void setUp(){
    	semantic = new Semantic(semanticType);
    	
    }

    @Test
    public void buildBaseNode() throws CustomException {
        Network.buildBaseNode("n0", semantic);
        Node n0 =  Network.getNode("n0");
        assertTrue(Network.getNodeById(0) instanceof PropositionNode);
        assertEquals(n0, Network.getNodeById(0));
        assertTrue(n0.getTerm() instanceof Base);
    }
    
    @Test
    public void buildMolNode() {
        
    }

   

    @After
    public void removeNodes() throws CustomException {
        Network.removeNode(Network.getNode("n0"));
    }

}