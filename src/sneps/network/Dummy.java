package sneps.network;

import java.io.Serializable;
import java.util.ArrayList;

import sneps.network.classes.setClasses.NodeSet;

public class Dummy extends Node implements Serializable {
	
	private Node parent;
	private Node child;
	private static int count=0;

	public Dummy(Node parent, Node child, int level) {
		super();
		this.parent=parent;
		this.child=child;
		this.updateLevel(level);
		Network.addNodeLBL(this,level);
		count++;
	}
	
	public void createDummies(Node parent, Node child) {
		NodeSet dummies= new NodeSet();
		dummies.addNode(new Dummy(null,child,child.getLevel()+1));
		for(int i= 2+child.getLevel();i<parent.getLevel();i++) {
			dummies.addNode(new Dummy(null,dummies.getNode(i-1),i));
		}
			//TODO Create LongSpanEdge Class
	}
	
	/**
	 * This method gets the parent nodes that are in the ABOVE adjacent level
	 *
	 */
	@Override
	public ArrayList<Integer> getAdjacentParents() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(Network.getNodesLBL().get(getLevel()+1).indexOf(parent));
		return result;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getparent() {
		return parent;
	}

	public Node getchild() {
		return child;
	}
	
	
}
