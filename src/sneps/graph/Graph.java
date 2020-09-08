package sneps.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import sneps.network.Dummy;

//import com.mxgraph.util.*;

import sneps.network.Network;
import sneps.network.Node;
public class Graph {
	
	private static ArrayList<ArrayList<Node>> nodesLBL;
	private static ArrayList<ArrayList<Edge>> edges;
	private static ArrayList<ArrayList<Vertex>> verticesLBL;
	private static ArrayList<ArrayList<Vertex>> dummyVerticesLBL; 
	private static ArrayList<LongSpanEdge> longSpanEdges;
	
	
	public static void constructGraph() {
		edges= new ArrayList<ArrayList<Edge>>();
		longSpanEdges= new ArrayList<LongSpanEdge>();
		nodesLBL= Network.getNodesLBL();
		Node node;
		verticesLBL = new ArrayList<ArrayList<Vertex>>();
		dummyVerticesLBL= new ArrayList<ArrayList<Vertex>>();
		for(int i=0;i<nodesLBL.size();i++) {
			verticesLBL.add(new ArrayList<Vertex>());
			dummyVerticesLBL.add(new ArrayList<Vertex>());
			for(int j=0; j<nodesLBL.get(i).size();j++) {
				node=nodesLBL.get(i).get(j);
				verticesLBL.get(i).add(new Vertex(node,new Point2D.Double(j+0.0,i+0.0),node.getAdjacentParents()));
				if(node instanceof Dummy)
					for (int c=0; c<longSpanEdges.size();c++) {
						if(longSpanEdges.get(i).getDummySet().contains(node))
							longSpanEdges.get(i).addDummyVertices(verticesLBL.get(i).get(j));
					}
			}
		}
		for(int i=0;i<(verticesLBL.size()-1);i++) {
			edges.add(new ArrayList<Edge>());
			for(int j=0;j<verticesLBL.get(i).size();j++) {
				Vertex current= verticesLBL.get(i).get(j); 
				 for(int k=0; k<verticesLBL.get(i).get(j).getParentIndices().size(); k++) {
					 int v= verticesLBL.get(i).get(j).getParentIndices().get(k);
					 Vertex parent= verticesLBL.get(i+1).get(v);
					 edges.get(i).add(new Edge(parent,current));
					 parent.addOutgoingEdge(edges.get(i).get(edges.size()-1));
					 current.addIncomingEdge(edges.get(i).get(edges.size()-1));
				 }
			}
		}
		minimizeEdgeCrossings();
		assignXcoordinates();
		constructEdges();
		
		//printGraph();
		
		
	}

	private static void printGraph() {
		int lastX;
		for(int i=verticesLBL.size()-1; i>=0; i--) {
			lastX=0;
			for(int j=0;j<verticesLBL.get(i).size(); j++) {
				for(int c=1;c<lastX;c++)
					System.out.print("           ");
				System.out.print(verticesLBL.get(i).get(j));
				lastX=(int) verticesLBL.get(i).get(j).getPosition().x;
			}
			System.out.println("");
		}
		
	}

	private static void minimizeEdgeCrossings() {
		boolean hasChanged = true;
		for (int c=0;hasChanged; c++) {
			hasChanged=false;
			for(int i=verticesLBL.size()-1;i>-1;i--) {
				for(int j=0; j<verticesLBL.get(i).size();j++)
					verticesLBL.get(i).get(j).computeBarycenterValue();
				Collections.sort(verticesLBL.get(i));
				for(int j=0; j<verticesLBL.get(i).size();j++)
					hasChanged|=verticesLBL.get(i).get(j).setX(j+0.0);
			}
		}
	}
	
	public static void addLongSpanEdge(LongSpanEdge longSpanEdge) {
		longSpanEdges.add(longSpanEdge);
	}
	
	public static void constructEdges() {
		for(int i=0; i<edges.size();i++) {
			for(int j=0; j<edges.get(i).size();j++) {
				edges.get(i).get(j).constructLine();
			}
		}
	}
	
	public static void assignXcoordinates() {

		//setting priorities
		int sumUpper;
		int sumLower;
		ArrayList<ArrayList<Integer>> prioritiesUpperLBL= new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> prioritiesLowerLBL= new ArrayList<ArrayList<Integer>>();
		for(int i=0; i< verticesLBL.size(); i++) {
			sumUpper=0;
			sumLower=0;
			prioritiesUpperLBL.add(new ArrayList<Integer>());
			prioritiesLowerLBL.add(new ArrayList<Integer>());
			for(int j=0; j<verticesLBL.get(i).size(); j++) {
				sumUpper+=verticesLBL.get(i).get(j).computePriorityUpper();
				sumLower+=verticesLBL.get(i).get(j).computePriorityLower();
				prioritiesUpperLBL.get(i).add(verticesLBL.get(i).get(j).getPriorityUpper());
				prioritiesLowerLBL.get(i).add(verticesLBL.get(i).get(j).getPriorityLower());
			}
			for(int j=0; j<verticesLBL.get(i).size(); j++)
				if(verticesLBL.get(i).get(j).getNode() instanceof Dummy) {
					verticesLBL.get(i).get(j).setPriorityUpper(sumUpper);
					verticesLBL.get(i).get(j).setPriorityLower(sumLower);
					prioritiesUpperLBL.get(i).set(j,verticesLBL.get(i).get(j).getPriorityUpper());
					prioritiesLowerLBL.get(i).set(j,verticesLBL.get(i).get(j).getPriorityLower());
				}
		}
		int max;
		ArrayList<Integer> copyPrioritiesUpper;
		ArrayList<Integer> occurrences;
		int steps;
		int step;
		int index;
		ArrayList<Vertex> moveableVertices;
		boolean higherPriority;

		//Down Procedure
		for(int i=verticesLBL.size()-1; i>-1; i--) {
			copyPrioritiesUpper=((ArrayList<Integer>) prioritiesUpperLBL.get(i).clone());
			while(!copyPrioritiesUpper.isEmpty()) {
				occurrences=new ArrayList<Integer>();
				max=Collections.max(copyPrioritiesUpper);
				copyPrioritiesUpper.remove(copyPrioritiesUpper.indexOf(max));
				for (int c=0;c<prioritiesUpperLBL.get(i).size();c++)
					if(max==prioritiesUpperLBL.get(i).get(c))
						occurrences.add(c);
				for(int j=0; j<occurrences.size();j++) {
					index=occurrences.get(j);
					steps=(int) Math.round(verticesLBL.get(i).get(index).computeBarycenterUpper());
					steps-=verticesLBL.get(i).get(index).getPosition().x;
					if(steps<0)
						step=-1;
					else
						step=1;
					moveableVertices=new ArrayList<Vertex>();
					for(int s=0;s<Math.abs(steps);s++) {
						higherPriority=false;
						for(int k=index;k<verticesLBL.get(i).size()-1 && k>0;k+=step) {
							if(!(verticesLBL.get(i).get(k+step).getPosition().x==verticesLBL.get(i).get(k).getPosition().x+step)) 
								break;
							if(verticesLBL.get(i).get(k+step).getPriorityUpper()>verticesLBL.get(i).get(k).getPriorityUpper()) {
								higherPriority=true;
								break;
							}
							moveableVertices.add(verticesLBL.get(i).get(k));
						}
						if(higherPriority)
							break;
						for(int m=0;m<moveableVertices.size();m++)
							moveableVertices.get(m).move(step);
					}
				}
			}
		}
		ArrayList<Integer> copyPrioritiesLower;

		//Up Procedure
		for(int i=1; i<verticesLBL.size(); i++) {
			copyPrioritiesLower=((ArrayList<Integer>) prioritiesLowerLBL.get(i).clone());
			while(!copyPrioritiesLower.isEmpty()) {
				occurrences=new ArrayList<Integer>();
				max=Collections.max(copyPrioritiesLower);
				copyPrioritiesLower.remove(copyPrioritiesLower.indexOf(max));
				for (int c=0;c<prioritiesLowerLBL.get(i).size();c++)
					if(max==prioritiesLowerLBL.get(i).get(c))
						occurrences.add(c);
				for(int j=0; j<occurrences.size();j++) {
					index=occurrences.get(j);
					steps=(int) Math.round(verticesLBL.get(i).get(index).computeBarycenterLower());
					steps-=verticesLBL.get(i).get(index).getPosition().x;
					if(steps<0)
						step=-1;
					else
						step=1;
					moveableVertices=new ArrayList<Vertex>();
					for(int s=0;s<Math.abs(steps);s++) {
						higherPriority=false;
						for(int k=index;k<verticesLBL.get(i).size()-1 && k>0;k+=step) {
							if(!(verticesLBL.get(i).get(k+step).getPosition().x== verticesLBL.get(i).get(k).getPosition().x+step)) 
								break;
							if(verticesLBL.get(i).get(k+step).getPriorityLower()>verticesLBL.get(i).get(k).getPriorityLower()) {
								higherPriority=true;
								break;
							}
							moveableVertices.add(verticesLBL.get(i).get(k));
						}
						if(higherPriority)
							break;
						for(int m=0;m<moveableVertices.size();m++)
							moveableVertices.get(m).move(step);
					}
				}
			}
		}

		//Down Procedure again
		for(int i=verticesLBL.size()-1; i>-1; i--) {
			copyPrioritiesUpper=((ArrayList<Integer>) prioritiesUpperLBL.get(i).clone());
			while(!copyPrioritiesUpper.isEmpty()) {
				occurrences=new ArrayList<Integer>();
				max=Collections.max(copyPrioritiesUpper);
				copyPrioritiesUpper.remove(copyPrioritiesUpper.indexOf(max));
				for (int c=0;c<prioritiesUpperLBL.get(i).size();c++)
					if(max==prioritiesUpperLBL.get(i).get(c))
						occurrences.add(c);
				for(int j=0; j<occurrences.size();j++) {
					index=occurrences.get(j);
					steps=(int) Math.round(verticesLBL.get(i).get(index).computeBarycenterUpper());
					steps-=verticesLBL.get(i).get(index).getPosition().x;
					if(steps<0)
						step=-1;
					else
						step=1;
					moveableVertices=new ArrayList<Vertex>();
					for(int s=0;s<Math.abs(steps);s++) {
						higherPriority=false;
						for(int k=index;k<verticesLBL.get(i).size()-1 && k>0;k+=step) {
							if(!(verticesLBL.get(i).get(k+step).getPosition().x== verticesLBL.get(i).get(k).getPosition().x+step)) 
								break;
							if(verticesLBL.get(i).get(k+step).getPriorityUpper()>verticesLBL.get(i).get(k).getPriorityUpper()) {
								higherPriority=true;
								break;
							}
							moveableVertices.add(verticesLBL.get(i).get(k));
						}
						if(higherPriority)
							break;
						for(int m=0;m<moveableVertices.size();m++)
							moveableVertices.get(m).move(step);
					}
				}
			}
		}

		//Shift all nodes if any node has a negative x-coordinate
		shiftVertices();
	}
	

	private static void shiftVertices() {
		int offset= -(int) Vertex.getMin_X();
		if(offset!=0)
			for(int i=0; i<verticesLBL.size();i++)
				for(int j=0; j<verticesLBL.get(i).size();j++)
					verticesLBL.get(i).get(j).move(offset);
	}
	
	public static void clear() {
	}
}
