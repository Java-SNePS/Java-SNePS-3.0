package tests;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sneps.exceptions.CustomException;
import sneps.exceptions.NodeCannotBeRemovedException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.RCFP;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Base;
import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.LinkedList;

public class NetworkTestN {
     Semantic semantic;
    final static String semanticType = "Proposition";


    @Before
    public void setUp() throws CustomException{
    	semantic = new Semantic(semanticType);
    }

    @Test
    public void buildBaseNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException{
        Network.buildBaseNode("n0", semantic);
        Node n0 =  Network.getNode("n0");
        //Hashtable<String, PropositionNode>  propositionNodes = Network.getPropositionNodes();
        assertTrue(Network.getNodeById(0) instanceof PropositionNode);
        assertEquals(n0, Network.getNodeById(0));
        assertTrue(n0.getTerm() instanceof Base);


    }

    @After
    public void removeNodes() throws NodeCannotBeRemovedException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, NotAPropositionNodeException {
        Network.removeNode(Network.getNode("n0"));
    }

}