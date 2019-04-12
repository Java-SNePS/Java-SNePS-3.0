package sneps.snebr;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

public class BaseSupportGraph {
	
//	Adjacency lists representing the graph
	private LinkedList<PropositionSet>[] hypsAdjList;
	private LinkedList<PropositionSet>[] supportsAdjList;
	
	
	@SuppressWarnings("unchecked")
	protected BaseSupportGraph() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		int noOfNodes = (Network.getPropositionNodes()).size();
		hypsAdjList = new LinkedList[noOfNodes];
	    for (int i = 0; i < hypsAdjList.length; i++){
	    	hypsAdjList[i] = new LinkedList<PropositionSet>();
	    }
		Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
		Set<String> nodeKeys = propositionNodes.keySet();
		int i = 0;
    	for(String key: nodeKeys){
    		hypsAdjList[i].add(new PropositionSet(propositionNodes.get(key).getId()));
    		Hashtable<String, PropositionSet> justificationSupport = propositionNodes.get(key).getJustificationSupport();
    		Set<String> supportKeys = justificationSupport.keySet();
    		int j = 0;
    		for(String supportKey: supportKeys){
    			hypsAdjList[i].add(justificationSupport.get(supportKey));
    			supportsAdjList[j].add(justificationSupport.get(supportKey));
    			supportsAdjList[j].add(new PropositionSet(propositionNodes.get(key).getId()));
    			j++;
    		}
    		i++;
    	}
	}
	
	
	public LinkedList<PropositionSet>[] getHypsAdjList() {
		return hypsAdjList;
	}
	

	public LinkedList<PropositionSet>[] getSupportsAdjList() {
		return supportsAdjList;
	}
	

}
