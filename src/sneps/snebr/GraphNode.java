package sneps.snebr;

import sneps.exceptions.NodeNotFoundException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.setClasses.PropositionSet;

public class GraphNode {
	
	private static int idGenerator = 0;
	private int id;
	private int type; //PropositionNode (0) or Support (1)
	private PropositionSet propositionSet;
	private int propositionNodeId;
	
	public GraphNode(PropositionSet propSet, int type) {
		this.id = ++idGenerator;
		this.type = type;
		this.propositionSet = propSet;
	}
	
	public boolean isSubSet(GraphNode N1) {
		return this.propositionSet.isSubSet(N1.getPropositionSet());
	}
	
	public boolean deeplyEquals(Object o) {
		GraphNode n = (GraphNode) o;

		return (this.id == n.getGraphId()) && (this.propositionSet.equals(n.getPropositionSet()));
		
	}
	
	public boolean equals(Object o) {
		GraphNode n = (GraphNode) o;

		return this.propositionSet.equals(n.getPropositionSet());
		
	}
	
	public int getPropositionNodeId() throws NodeNotFoundException, NotAPropositionNodeException, NodeNotFoundInNetworkException{
		int nodeId = 0;
		if(this.type == 0) {
			int [] props = PropositionSet.getPropsSafely(propositionSet);
			nodeId = props[0];
			return nodeId;
		} else {
			throw new NodeNotFoundException("Node not found");
		}
		
	}

	public int getGraphId() {
		return id;
	}
	
	public int getType() {
		return type;
	}

	public PropositionSet getPropositionSet() {
		return propositionSet;
	}


	@Override
	public String toString() {
		return "GraphNode [id=" + id + ", propositionSet=" + propositionSet
				+ "]";
	}
	
	

}
