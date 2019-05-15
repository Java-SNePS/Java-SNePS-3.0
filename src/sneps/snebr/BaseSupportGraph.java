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
	private LinkedList<LinkedList<PropositionSet>> hypsAdjList1;
	private LinkedList<LinkedList<PropositionSet>> supportsAdjList1;
	private LinkedList<LinkedList<GraphNode>> hypsAdjList;
	private LinkedList<LinkedList<GraphNode>> supportsAdjList;
	private LinkedList<PropositionSet> allSupports;
	
	
	@SuppressWarnings("unchecked")
	public BaseSupportGraph() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		int noOfNodes = (Network.getPropositionNodes()).size();
		hypsAdjList = new LinkedList<LinkedList<GraphNode>>();
		
	    for (int i = 0; i < noOfNodes; i++){
	    	hypsAdjList.add(new LinkedList<GraphNode>());
	    }
		Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
		allSupports = new LinkedList<PropositionSet>();
		Set<String> nodeKeys = propositionNodes.keySet();
		int i = 0;
    	for(String key: nodeKeys){
    		LinkedList<GraphNode> currentNodeRow = hypsAdjList.get(i);
    		currentNodeRow.add(new GraphNode(new PropositionSet(propositionNodes.get(key).getId()),0)); // adding the node in the 1st column
    		Hashtable<String, PropositionSet> justificationSupport = propositionNodes.get(key).getJustificationSupport();
    		Set<String> supportKeys = justificationSupport.keySet();
    		int j = 0;
    		for(String supportKey: supportKeys){
    			//hypsAdjList[i].add(justificationSupport.get(supportKey));
    			GraphNode currSupport = new GraphNode(justificationSupport.get(supportKey),1);
    			if(!(contains(allSupports, currSupport.getPropositionSet()))){
    				allSupports.add(currSupport.getPropositionSet());
    				supportsAdjList.add(new LinkedList<GraphNode>());
    				LinkedList<GraphNode> currentSupportRow = supportsAdjList.get(j);
    				currentSupportRow.add(currSupport);
    				currentSupportRow.add(new GraphNode(new PropositionSet(propositionNodes.get(key).getId()),0));
    				j++;
    			} else {
    				int suppIndex = indexOfSupportRow(currSupport);
    				LinkedList<GraphNode> currentSupportRow = supportsAdjList.get(suppIndex);
    				currentSupportRow.add(new GraphNode(new PropositionSet(propositionNodes.get(key).getId()),0));
    			}
    		}
    		i++;
    	}
    	for(Iterator iter = hypsAdjList.iterator(); iter.hasNext();){
    		LinkedList<GraphNode> currentHypList = (LinkedList<GraphNode>) iter.next();
    		GraphNode currHyp = currentHypList.peekFirst();
        	for (Iterator it = allSupports.iterator(); it.hasNext();) {
                GraphNode currentSupport = (GraphNode) it.next();
                if((currHyp.isSubSet(currentSupport)) && !(currHyp.equals(currentSupport))){
                	currentHypList.add(currentSupport);
                }
             }
    	}
	}
	

	
	@SuppressWarnings("unchecked")
	public void removeFromHypList(Object O){
		//code for removing a node from the graph
		GraphNode toBeDeleted = (GraphNode) O;
		for(Iterator iter = hypsAdjList.iterator(); iter.hasNext();){
			LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
			if((currentRow.peek()).equals(toBeDeleted)){
				hypsAdjList.remove(currentRow);
			}
		}
		
		for(Iterator iter = supportsAdjList.iterator(); iter.hasNext();){
			LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
			
			currentRow.removeFirst();
			if(currentRow.contains(toBeDeleted)){
				if (currentRow.size()>1){
					currentRow.remove(toBeDeleted);
				}
				else {
					//stopped here
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeFromSupportList(Object O){
		//code for removing a node from the graph
		PropositionSet toBeDeleted = (PropositionSet) O;
		
		for(Iterator iter = supportsAdjList.iterator(); iter.hasNext();){
			LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
			if((currentRow.get(0)).equals(toBeDeleted)){
				supportsAdjList.remove(currentRow);
			}
		}
		
	}
	
	public int indexOfSupportRow(GraphNode node) {
		int index = 0;
		int i = 0;
		for(Iterator iter = supportsAdjList.iterator(); iter.hasNext();){
			GraphNode curr = supportsAdjList.getFirst().getFirst();
			if(curr.equals(node)){
				index = i;
				break;
			}
			i++;
		}
		return index;
	}
	
	public static boolean contains(LinkedList<PropositionSet> list , PropositionSet propSet){
		for(Iterator it = list.iterator(); it.hasNext();){
			PropositionSet curr = (PropositionSet) it.next();
			if(curr.equals(propSet)){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	public void printBaseSupportGraph() {
		System.out.println("Hyps Adj List: ");
		for (Iterator i = hypsAdjList.iterator(); i.hasNext();){
			LinkedList<GraphNode> row = (LinkedList<GraphNode>) i.next();
			System.out.println(row);
		}
		
//		System.out.println("Supps Adj List: ");
//		for (Iterator i = supportsAdjList.iterator(); i.hasNext();){
//			LinkedList<GraphNode> row = (LinkedList<GraphNode>) i.next();
//			System.out.println(row);
//		}
	}



	public int getGraphSize() {
		return (hypsAdjList.size() + supportsAdjList.size());
	}
	
	
	public LinkedList<LinkedList<GraphNode>> getHypsAdjList() {
		return hypsAdjList;
	}
	

	public LinkedList<LinkedList<GraphNode>> getSupportsAdjList() {
		return supportsAdjList;
	}
	

}

