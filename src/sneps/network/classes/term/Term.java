package sneps.network.classes.term;

import java.io.Serializable;
import java.util.Enumeration;

import sneps.network.cables.UpCable;
import sneps.network.cables.UpCableSet;
import sneps.network.classes.setClasses.NodeSet;

public abstract class Term implements Serializable {
	/**
	 * the label or the name of the node.
	 */
	private String identifier;
	
	/**
	 * the up cable set that contains the up cables of the nodes.
	 * (any non-isolated node in the network can have up cables).
	 */
	private UpCableSet upCableSet;
	
	/**
	 * a boolean to indicate whether or not the current node is a 
	 * 	temporary node. Nodes can be temporarily created for the 
	 * 	purpose of matching during inference.
	 */
	private boolean temp;

	/**
	 * The constructor of this class.
	 * 
	 * @param idenitifier
	 * 			the name of the node that will be created.
	 */
	public Term(String idenitifier){
		this.identifier = idenitifier;
		this.upCableSet = new UpCableSet();
		this.temp = false;
	}
	
	/**
	 * 
	 * @return the name of the current node.
	 */
	public String getIdentifier(){
		return this.identifier;
	}
	
	/**
	 * 
	 * @return the up cable set of the current node.
	 */
	public UpCableSet getUpCableSet(){
		return this.upCableSet;
	}
	
	/**
	 * 
	 * @return true if the node is a temporary node, and false otherwise.
	 */
	public boolean isTemp(){
		return this.temp;
	}
	
	/**
	 * 
	 * @param temp
	 * 			a boolean that indicates whether the node is a temporary node.
	 */
	public void setTemp(boolean temp){
		this.temp = temp;
	}
	
	/**
	 * 
	 * @return a node set containing the parent nodes of the current node if
	 * 	the up cable set is not empty, and return an empty node set otherwise.
	 * (the parent nodes includes the direct and indirect parent nodes.) 
	 */
	public NodeSet getParentNodes() {
		if (this.upCableSet.isEmpty())
			return new NodeSet();
		NodeSet allParents = new NodeSet();
		Enumeration<UpCable> upCables = this.upCableSet.getUpCables().elements();
		while(upCables.hasMoreElements()){
			NodeSet ns = upCables.nextElement().getNodeSet();
			allParents = allParents.Union(ns);
			for (int j = 0; j < ns.size(); j++){
				allParents = allParents.Union(ns.getNode(j).getParentNodes());
			}
		}
		return allParents;
	}
	
	
	/**
	 * This method overrides the default toString method inherited from the Object class.
	 * 
	 * @return a string representing the name of the current node.
	 */
	public String toString(){
		return this.getIdentifier();
	}
}
