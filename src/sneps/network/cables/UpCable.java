/**
 * @className UpCable.java
 * 
 * @ClassDescription An up-cable is a structure that enables traversing the network upwards. 
 * 	This class extends the cable class and has no variables than those inherited from the super
 * 	class. 
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.cables;

import sneps.network.classes.Relation;

import java.io.Serializable;

import sneps.network.Node;
import sneps.network.classes.setClasses.NodeCollection;
import sneps.network.classes.setClasses.NodeSet;

public class UpCable extends Cable implements Serializable{

	/**
	 * The constructor of this class.
	 * 
	 * @param relation
	 * 			the relation that labels the arcs of this up cable.
	 */
	public UpCable(Relation relation) {
		super(relation, new NodeCollection());	
	}
	
	/**
	 * 
	 * @param node
	 * 			a node that will be added to the current up cable. Nodes can 
	 * 			be added or removed from the up cable after its creation.
	 */
	public void addNode(Node node){
		
		this.getNodeCollection().VectoraddNode(node);
	}
	
	public void addNodee(Node node){
			
			this.getNodeCollection().LinkedListaddNode(node);
		}

	/**
	 * 
	 * @param node
	 * 			a node that will be removed from the current up cable. Nodes can 
	 * 			be added or removed from the up cable after its creation.
	 */
	public void removeNode(Node node){
		this.getNodeCollection().VectorremoveNode(node);
	}
	
	public void removeNodee(Node node){
		this.getNodeCollection().LinkedListremoveNode(node);
	}
}
