package sneps.network.classes.setClasses;

import java.util.ArrayList;
//import java.awt.Graphics;
//import java.awt.print.PageFormat;
//import java.awt.print.Printable;
//import java.awt.print.PrinterException;
//import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import sneps.network.Node;
import sneps.network.classes.term.Term;

public class NodeCollection  {

	public Vector<Node> Vector_Nodes;
	public LinkedList<Node> LinkedList_Nodes;
	//public ArrayList<Node> ArrayList_Nodes;
	
	public NodeCollection() {	
		Vector_Nodes = new Vector<Node>();
		LinkedList_Nodes = new LinkedList<Node>();
	}
/////////////////*/////////////////  Vector Methods Start
	public Node VectorgetNode(int index) {
		return this.Vector_Nodes.get(index);
	}
	public void VectoraddNode(Node node) {
		if (!this.Vector_Nodes.contains(node))
			this.Vector_Nodes.add(node);
	}
	public int Vectorsize() {
		return this.Vector_Nodes.size();
	}
	public void VectoraddAll(NodeCollection Vector_NodeCollection) {
		for (int i = 0; i < Vector_NodeCollection.Vectorsize(); i++) {
			this.VectoraddNode(Vector_NodeCollection.VectorgetNode(i));
		}
	}
	public void VectorremoveNode(Node node) {
		this.Vector_Nodes.remove(node);
	}

	public void Vectorclear() {
		this.Vector_Nodes.clear();
	}
	
	public boolean VectorisEmpty() {
		return this.Vector_Nodes.isEmpty();
	}
	
	public boolean Vectorcontains(Node node) {
		return this.Vector_Nodes.contains(node);
	}
	public Vector<Node> VectorUnion(NodeCollection Vector_NodeCollection) {
		NodeCollection unionCollection = new NodeCollection();
		unionCollection.VectoraddAll(this);
		unionCollection.VectoraddAll(Vector_NodeCollection);
		return unionCollection.Vector_Nodes;
	}
	public Vector<Node> VectorIntersection(NodeCollection Vector_NodeCollection) {
		NodeCollection intersectionCollection = new NodeCollection();
		for (int i = 0; i < Vector_NodeCollection.Vectorsize(); i++) {
			if (this.Vectorcontains(Vector_NodeCollection.VectorgetNode(i)))
				intersectionCollection.VectoraddNode(Vector_NodeCollection.VectorgetNode(i));
		}
		return intersectionCollection.Vector_Nodes;
	}
	public Vector<Node> Vectordifference(NodeCollection Vector_NodeCollection) {
		NodeCollection differenceCollection = new NodeCollection();
		for (int i = 0; i < this.Vectorsize(); i++) {
			if (!Vector_NodeCollection.Vectorcontains(this.VectorgetNode(i)))
				differenceCollection.VectoraddNode(this.VectorgetNode(i));
		}
		return differenceCollection.Vector_Nodes;
	}
	public boolean Vectorequals(Object obj) {
		if (!obj.getClass().getSimpleName().equals("NodeCollection"))
			return false;

		NodeCollection nodeCollection = (NodeCollection) obj;
		if (this.Vector_Nodes.size() != nodeCollection.Vectorsize())
			return false;
		for (int i = 0; i < this.Vector_Nodes.size(); i++) {
			if (!nodeCollection.Vectorcontains(this.Vector_Nodes.get(i)))
				return false;
		}
		return true;
	}
	public Iterator<Node> Vectoriterator() {
		return Vector_Nodes.iterator();
	}
	public String VectortoString() {
		String s = "{";
		for (int i = 0; i < this.Vector_Nodes.size(); i++) {
			s += this.Vector_Nodes.get(i).toString();
			if (i < this.Vector_Nodes.size() - 1)
				s += " ";
		}
		s += "}";
		return s;
	}
/////////////////*/////////////////  Vector Methods Ends
	/*
	 * 
	 */
/////////////////*/////////////////  LinkedList Methods Starts	
	public Node LinkedListgetNode(int index) {
		return this.LinkedList_Nodes.get(index);
	}
	public void LinkedListaddNode(Node node) {
		if (!this.LinkedList_Nodes.contains(node))
			this.LinkedList_Nodes.add(node);
	}
	public void LinkedListaddNode(int index ,Node node) {
		if (!this.LinkedList_Nodes.contains(node))
			this.LinkedList_Nodes.add(index ,node);
	}
	public int LinkedListsize() {
		return this.LinkedList_Nodes.size();
	}
	public void LinkedListaddAll(NodeCollection LinkedList_NodeCollection) {
		for (int i = 0; i < LinkedList_NodeCollection.LinkedListsize(); i++) {
			this.LinkedListaddNode(LinkedList_NodeCollection.LinkedListgetNode(i));
		}
	}
	public void LinkedListremoveNode(Node node) {
		this.LinkedList_Nodes.remove(node);
	}
	public void LinkedListclear() {
		this.LinkedList_Nodes.clear();
	}
	public boolean LinkedListisEmpty() {
		return this.LinkedList_Nodes.isEmpty();
	}
	public boolean LinkedListcontains(Node node) {
		return this.LinkedList_Nodes.contains(node);
	}
	public LinkedList<Node> LinkedListUnion(NodeCollection LinkedList_NodeCollection) {
		NodeCollection unionCollection = new NodeCollection();
		unionCollection.LinkedListaddAll(this);
		unionCollection.LinkedListaddAll(LinkedList_NodeCollection);
		return unionCollection.LinkedList_Nodes;
	}
	public LinkedList<Node> LinkedListIntersection(NodeCollection LinkedList_NodeCollection) {
		NodeCollection intersectionCollection = new NodeCollection();
		for (int i = 0; i < LinkedList_NodeCollection.LinkedListsize(); i++) {
			if (this.LinkedListcontains(LinkedList_NodeCollection.LinkedListgetNode(i)))
				intersectionCollection.LinkedListaddNode(LinkedList_NodeCollection.LinkedListgetNode(i));
		}
		return intersectionCollection.LinkedList_Nodes;
	}
	public LinkedList<Node> LinkedListdifference(NodeCollection LinkedList_NodeCollection) {
		NodeCollection differenceCollection = new NodeCollection();
		for (int i = 0; i < this.LinkedListsize(); i++) {
			if (!LinkedList_NodeCollection.LinkedListcontains(this.LinkedListgetNode(i)))
				differenceCollection.LinkedListaddNode(this.LinkedListgetNode(i));
		}
		return differenceCollection.LinkedList_Nodes;
	}
	public boolean LinkedListequals(Object obj) {
		if (!obj.getClass().getSimpleName().equals("NodeCollection"))
			return false;

		NodeCollection nodeCollection = (NodeCollection) obj;
		if (this.LinkedList_Nodes.size() != nodeCollection.LinkedListsize())
			return false;
		for (int i = 0; i < this.LinkedList_Nodes.size(); i++) {
			if (!nodeCollection.LinkedListcontains(this.LinkedList_Nodes.get(i)))
				return false;
		}
		return true;
	}
	public Iterator<Node> LinkedListiterator() {
		return LinkedList_Nodes.iterator();
	}
	public String LinkedListtoString() {
		String s = "{";
		for (int i = 0; i < this.LinkedList_Nodes.size(); i++) {
			s += this.LinkedList_Nodes.get(i).toString();
			if (i < this.LinkedList_Nodes.size() - 1)
				s += " ";
		}
		s += "}";
		return s;
	}
	
	public LinkedList<Node> LinkedListSubList (NodeCollection LinkedList_NodeCollection) {
		
		NodeCollection x = new NodeCollection();
		
		for(int i = 0; i < LinkedList_NodeCollection.LinkedListsize(); i++) {
			for(int j = 0; j < this.LinkedList_Nodes.size(); j++) {
				if((LinkedList_NodeCollection.LinkedListgetNode(i)) != (this.LinkedList_Nodes.get(j))) {
					
				}
			}
		}
		return x.LinkedList_Nodes;
		
	}
/////////////////*/////////////////  LinkedList Methods Ends
/*
* 
*/
	public int [] VectorToArray (Vector<Integer> a) {
		
		int length = a.size();
		int [] array = new int [length];
		for(int i=0; i<length;i++) {
			array[i] = a.get(i);
		}
		return array;
		
	}
	
	public int [] LinkedListToArray (LinkedList<Integer> a) {
		
		int length = a.size();
		int [] array = new int [length];
		for(int i=0; i<length;i++) {
			array[i] = a.get(i);
		}
		return array;
		
	}
	
	public static void main(String[] args) {
		
		NodeCollection nc = new NodeCollection();
		Term t = new Term("Tarek") {};
		Term tt = new Term("Mohamed") {};
		Term ttt = new Term("ElWasseif") {};
		Node x = new Node(t);
		Node xx = new Node(tt);
		Node xxx = new Node(ttt);
		nc.LinkedListaddNode(x);
		nc.LinkedListaddNode(xx);
		nc.LinkedListaddNode(xxx);
		
		NodeCollection ncc = new NodeCollection();
		Term t1 = new Term("Ahmed") {};
		Term tt2 = new Term("Hassan") {};
		Term ttt3 = new Term("Tarek") {};
		Node x1 = new Node(t1);
		Node xx2 = new Node(tt2);
		Node xxx3 = new Node(ttt3);
		ncc.LinkedListaddNode(x1);
		ncc.LinkedListaddNode(xx2);
		ncc.LinkedListaddNode(xxx3);
		
		//System.out.println("Union : " + nc.VectorUnion(ncc));
		//System.out.println("Intersection : " + nc.VectorIntersection(ncc));
		//System.out.println("Difference : " + nc.Vectordifference(ncc));
		
		//System.out.println("Union : " + nc.LinkedListUnion(ncc));
		//System.out.println("Intersection : " + nc.LinkedListIntersection(ncc));
		//System.out.println("Difference : " + nc.LinkedListdifference(ncc));
		
	}

}

