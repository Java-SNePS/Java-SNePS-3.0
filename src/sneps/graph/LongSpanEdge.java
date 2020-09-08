package sneps.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;

import sneps.network.Dummy;
import sneps.network.Node;
import sneps.network.cables.UpCable;

public class LongSpanEdge  {
	
	private Node parent;
	private Node child;
	private String label;
	private ArrayList<Node> dummySet;
	private ArrayList<Edge> edges;
	private ArrayList<Vertex> dummyVertices;
	
	public LongSpanEdge(Node parent, Node child) {
		this.parent = parent;
		this.child = child;
		
		//find the relation name to label the Edge.
		Hashtable<String, UpCable> childUpCableSet=child.getUpCableSet().getUpCables();
		for(String relation: childUpCableSet.keySet())
			if(childUpCableSet.get(relation).getNodeSet().contains(parent))
				label=relation;
		dummySet= new ArrayList<Node>();
		dummySet.add(child);
		int levelDifference= parent.getLevel()-child.getLevel();
		Node previous;
		for(int i=1;1<levelDifference;i++) {
			previous = (Node)dummySet.get(i-1);
			dummySet.add(new Dummy(null, previous, i));
		}
		dummySet.remove(0);
		for(int i=0; i<(dummySet.size()-1);i++) {
			((Dummy) dummySet.get(i)).setParent(dummySet.get(i+1)); 
		}
		((Dummy) dummySet.get(levelDifference-2)).setParent(parent);
		dummyVertices= new ArrayList<Vertex>();
		edges=new ArrayList<Edge>();
		Graph.addLongSpanEdge(this);
	}

	/**
	 * @return the dummies
	 */
	public Node getFirstDummy() {
		return dummySet.get(0);
	}

	/**
	 * @return the dummySet
	 */
	public ArrayList<Node> getDummySet() {
		return dummySet;
	}

	/**
	 * @param dummyVertices the dummyVertices to set
	 */
	public void addDummyVertices(Vertex dummyVertex) {
		dummyVertices.add(dummyVertex);
	}

	/**
	 * @return the dummyVertices
	 */
	public ArrayList<Vertex> getDummyVertices() {
		return dummyVertices;
	}

	/**
	 * @return the edges
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	
}
