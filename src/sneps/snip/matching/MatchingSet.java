package sneps.snip.matching;

import java.util.HashSet;
import java.util.LinkedList;

import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Molecular;
import sneps.network.Node;


public class MatchingSet {
	HashSet<Node> VariableBaseNodes;
	LinkedList<Molecular> Molecular;
	
	public MatchingSet(){

		VariableBaseNodes=new HashSet<Node>();

		Molecular=new LinkedList<Molecular>();

	}
	
	/*public boolean add(Node n) {

		if(n.getTerm()instanceof Molecular){
			
         Molecular k=(Molecular)n.getTerm();
         
		for (int i = 0; i < Molecular.size(); i++) 

			if(Matcher.sameFunction(k, Molecular.get(i)))

				return false;

		 Molecular.add(k);

			return true;

			}

		else

			return VariableBaseNodes.add(n);
			}*/
	
	/*public boolean remove(Node n) {

		if(n.getTerm()instanceof Molecular){
			
        Molecular w=(Molecular)n.getTerm();
        
			for (int i = 0; i < Molecular.size(); i++) 

				if(Matcher.sameFunction(w, Molecular.get(i)))

				{Molecular.remove(w);

				return true;}

			return false;

				

		}

		else

			return VariableBaseNodes.remove(n);

	}
	
	public boolean replace(Node n1,Node n2){

		return remove(n1)&&add(n2);

	}*/



	public int size(){

		return VariableBaseNodes.size()+Molecular.size();

	}
	
	public Node[] toArray(){

		Node[] nodes=new Node[VariableBaseNodes.size()+Molecular.size()];

		Node[] vbNodes=new Node[VariableBaseNodes.size()];

		Node[] mNodes=new Node[Molecular.size()];

		VariableBaseNodes.toArray(vbNodes);

		Molecular.toArray(mNodes);

		for (int i = 0; i < mNodes.length+vbNodes.length; i++) {

		if(i<mNodes.length)

			nodes[i]=mNodes[i];

		else

			nodes[i]=vbNodes[i-mNodes.length];

		}

		

		return nodes;

	}

	

	public String toString(){

		return VariableBaseNodes.toString()+Molecular.toString();

	}

	

	/*public void add(NodeSet ns){

		for (int i = 0; i < ns.size(); i++) 

			add(ns.getNode(i));

		

	}*/


}
