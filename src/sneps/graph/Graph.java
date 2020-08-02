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
	private static ArrayList<ArrayList<Edge>> edges= new ArrayList<ArrayList<Edge>>();
	private static ArrayList<ArrayList<Vertex>> verticesLBL;
	private static ArrayList<ArrayList<Vertex>> dummyVerticesLBL; 
	private static ArrayList<LongSpanEdge> longSpanEdges= new ArrayList<LongSpanEdge>();
	
	
	public static void constructGraph() {
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
		
		//TODO handle remove dummy vertices with appropriate protocols
		//TODO specify final x-coordinates
		constructEdges();
		
		
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
	
	public static void clear() {
		longSpanEdges=new ArrayList<LongSpanEdge>();
	}
}