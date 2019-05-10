package sneps.snebr;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

public class BaseSupportGraph {
	
//	Adjacency lists representing the graph
//	2 lists because it is a bipartite graph
	private LinkedList<LinkedList<PropositionSet>> hypsAdjList;
	private LinkedList<LinkedList<PropositionSet>> supportsAdjList;
	private LinkedList<PropositionSet> allSupports;
	
	
	@SuppressWarnings("unchecked")
	public BaseSupportGraph() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		int noOfNodes = (Network.getPropositionNodes()).size();
		hypsAdjList = new LinkedList<LinkedList<PropositionSet>>();
		
	    for (int i = 0; i < noOfNodes; i++){
	    	hypsAdjList.add(new LinkedList<PropositionSet>());
	    }
		Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
		allSupports = new LinkedList<PropositionSet>();
		Set<String> nodeKeys = propositionNodes.keySet();
		int i = 0;
    	for(String key: nodeKeys){
    		LinkedList<PropositionSet> currentNodeRow = hypsAdjList.get(i);
    		currentNodeRow.add(new PropositionSet(propositionNodes.get(key).getId())); // adding the node in the 1st column
    		Hashtable<String, PropositionSet> justificationSupport = propositionNodes.get(key).getJustificationSupport();
    		Set<String> supportKeys = justificationSupport.keySet();
    		int j = 0;
    		for(String supportKey: supportKeys){
    			//hypsAdjList[i].add(justificationSupport.get(supportKey));
    			allSupports.add(justificationSupport.get(supportKey));
    			supportsAdjList.add(new LinkedList<PropositionSet>());
    			LinkedList<PropositionSet> currentSupportRow = supportsAdjList.get(j);
    			currentSupportRow.add(justificationSupport.get(supportKey));
    			currentSupportRow.add(new PropositionSet(propositionNodes.get(key).getId()));
    			j++;
    		}
    		i++;
    	}
    	for(Iterator iter = hypsAdjList.iterator(); iter.hasNext();){
    		LinkedList<PropositionSet> currentHypList = (LinkedList<PropositionSet>) iter.next();
    		PropositionSet currHyp = currentHypList.peekFirst();
        	for (Iterator it = allSupports.iterator(); it.hasNext();) {
                PropositionSet currentSupport = (PropositionSet) it.next();
                if(currHyp.isSubSet(currentSupport)){
                	currentHypList.add(currentSupport);
                }
             }
    	}
	}
	
	@SuppressWarnings("unchecked")
	public void removeFromHypList(Object O){
		//code for removing a node from the graph
		PropositionSet toBeDeleted = (PropositionSet) O;
		for(Iterator iter = hypsAdjList.iterator(); iter.hasNext();){
			LinkedList<PropositionSet> currentHypList = (LinkedList<PropositionSet>) iter.next();
			if((currentHypList.peek()).equals(toBeDeleted)){
				hypsAdjList.remove(currentHypList);
			}
		}
		
		for(Iterator iter = supportsAdjList.iterator(); iter.hasNext();){
			LinkedList<PropositionSet> currentRow = (LinkedList<PropositionSet>) iter.next();
			if((currentRow.get(1)).equals(toBeDeleted)){
				currentRow.remove(1);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeFromSupportList(Object O){
		//code for removing a node from the graph
		PropositionSet toBeDeleted = (PropositionSet) O;
		
		for(Iterator iter = supportsAdjList.iterator(); iter.hasNext();){
			LinkedList<PropositionSet> currentRow = (LinkedList<PropositionSet>) iter.next();
			if((currentRow.get(0)).equals(toBeDeleted)){
				supportsAdjList.remove(currentRow);
			}
		}
		
	}
	
	
	public LinkedList<LinkedList<PropositionSet>> getHypsAdjList() {
		return hypsAdjList;
	}
	

	public LinkedList<LinkedList<PropositionSet>> getSupportsAdjList() {
		return supportsAdjList;
	}
	

}
