package sneps.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import sneps.network.Node;

public class Vertex implements Comparable<Vertex> {

	private Point2D.Double position;
	private Node node;
	private int outDegree = 0;
	private int inDegree = 0;
	private ArrayList<Edge> outgoingEdges; 
	private ArrayList<Edge> incomingEdges;
	private Double bary;
	private String label;
	private ArrayList<Integer> parentIndices;
	private Double baryLower;
	private Double baryUpper;
	private int priorityLower;
	private int priorityUpper;
	private static double max_X=0;
	private static double min_X=0;

	public Vertex(Node node, Point2D.Double position, ArrayList<Integer> parentIndices) {
		this.node=node;
		this.position=position;
		this.parentIndices=parentIndices;
		label = node.getIdentifier();
		outgoingEdges= new ArrayList<Edge>();
		incomingEdges= new ArrayList<Edge>();
		if(position.x>max_X)
			max_X=position.x;
		
		
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	
	public void computeBarycenterValue() {
		double barycenterIn=0;
		double barycenterOut=0;
		for(int i=0; i<inDegree; i++)
			barycenterIn+=(incomingEdges.get(i).getParentX()/(inDegree+0.0));
		for(int i=0; i<outDegree; i++)
			barycenterOut+=(outgoingEdges.get(i).getChildX()/(outDegree+0.0));
		bary= barycenterIn+barycenterOut;
		if(inDegree==0 || outDegree==0)
			return;
		bary/=2;
	}
	
	public Double computeBarycenterUpper() {
		baryUpper=0.0;
		for(int i=0; i<inDegree; i++)
			baryUpper+=(incomingEdges
					.get(i).getParentX()/(inDegree+0.0));
		return baryUpper;
	}
			
			
	public Double computeBarycenterLower(){
		baryLower=0.0;
		for(int i=0; i<outDegree; i++)
			baryLower+=(outgoingEdges.get(i).getChildX()/(outDegree+0.0));
		return baryLower;
	}
	
	public int computePriorityUpper() {    
		priorityUpper=inDegree;
		return priorityUpper;
	}
	
	public int computePriorityLower() {    
		priorityLower=outDegree;
		return priorityLower;
	}
	

	/**
	 * @param priorityLower the priorityLower to set
	 */
	public void setPriorityLower(int priorityLower) {
		this.priorityLower = priorityLower;
	}

	/**
	 * @param priorityUpper the priorityUpper to set
	 */
	public void setPriorityUpper(int priorityUpper) {
		this.priorityUpper = priorityUpper;
	}

	/**
	 * @return the baryLower
	 */
	public Double getBaryLower() {
		return baryLower;
	}

	/**
	 * @return the baryUpper
	 */
	public Double getBaryUpper() {
		return baryUpper;
	}

	/**
	 * @return the priorityLower
	 */
	public int getPriorityLower() {
		return priorityLower;
	}

	/**
	 * @return the priorityUpper
	 */
	public int getPriorityUpper() {
		return priorityUpper;
	}
	
	public void move(int displacement) {
		position.x+=displacement;
		if(position.x>max_X)
			max_X=position.x;
		if(position.x<min_X)
			min_X=position.x;
	}

	public boolean setX(double x) {
		if(position.x!=x) {
			position.x=x;
			return true;
		}
		return false;
	}
	
	/**
	 * @return the barycenter value
	 */
	public Double getBary() {
		return bary;
	}
	
	public void addOutgoingEdge(Edge out) {
		outgoingEdges.add(out);
		outDegree++;
	}
	
	public void addIncomingEdge(Edge in) {
		incomingEdges.add(in);
		inDegree++;
	}

	/**
	 * @return the outDegree
	 */
	public int getOutDegree() {
		return outDegree;
	}

	/**
	 * @return the inDegree
	 */
	public int getInDegree() {
		return inDegree;
	}

	/**
	 * @return the position
	 */
	public Point2D.Double getPosition() {
		return position;
	}

	/**
	 * @return the parentIndices
	 */
	public ArrayList<Integer> getParentIndices() {
		return parentIndices;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @return the max_X
	 */
	public static double getMax_X() {
		return max_X;
	}

	/**
	 * @return the min_X
	 */
	public static double getMin_X() {
		return min_X;
	}

	@Override
	public int compareTo(Vertex v) {
		if( bary == v.getBary())
			return 0;
		if( bary < v.getBary())
			return -1;
		return 1;
	}
	
	public String toString() {
		//return " ["+position.x+","+ position.y+"] ";
		return label;
	}
		
}
