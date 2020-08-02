package sneps.graph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;

import sneps.network.Dummy;
import sneps.network.Node;
import sneps.network.cables.UpCable;
import sneps.network.cables.UpCableSet;
import sneps.network.classes.setClasses.NodeSet;

public class Edge {
	
	private Node parentNode;
	private Node childNode;
	private String label;
	private Line2D.Double line;
	private Vertex parent;
	private Vertex child;
	
	public Edge(Vertex pV, Vertex cV) {
		parent=pV;
		child=cV;
		parentNode = parent.getNode();
		childNode = child.getNode();
		
		if(!(childNode instanceof Dummy || parentNode instanceof Dummy)) {
		//find the relation name to label the Edge.
		Hashtable<String, UpCable> childUpCableSet=childNode.getUpCableSet().getUpCables();
		for(String relation: childUpCableSet.keySet())
			if(childUpCableSet.get(relation).getNodeSet().contains(parentNode))
				label=relation;
		}
	}
	
	public void constructLine() {
		line=new Line2D.Double(parent.getPosition(),child.getPosition());
	}

	/**
	 * @return the line
	 */
	public Line2D.Double getLine() {
		return line;
	}
	
	
	/**
	 * @return the parent Vertex
	 */
	public Vertex getParent() {
		return parent;
	}

	/**
	 * @return the child Vertex
	 */
	public Vertex getChild() {
		return child;
	}
	
	/**
	 * @return the label. The relation Name.
	 */
	public String getLabel() {
		return label;
	}
	
	public boolean intersects(Edge e) {
		return line.intersectsLine((Line2D.Double)e.getLine());
	}
	
	public static int countNumberOfCrossing (ArrayList<Edge> edges) {
		int count=0;
		for(int i=0; i<edges.size();i++)
			for(int j=i+1;j<edges.size();j++)
				if(edges.get(i).intersects(edges.get(j)))
					count++;
		return count; 
	}

	/**
	 * @return the parent Vertex
	 */
	public double getParentX() {
		return parent.getPosition().getX();
	}

	/**
	 * @return the child Vertex
	 */
	public double getChildX() {
		return child.getPosition().getX();
	}
	
}
