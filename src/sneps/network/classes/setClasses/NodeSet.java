package sneps.network.classes.setClasses;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import sneps.network.Node;

public class NodeSet implements Iterable<Node>, Serializable {
	private Vector<Node> nodes;

	public NodeSet() {
		nodes = new Vector<Node>();
	}


	public Node getNode(int index) {
		return this.nodes.get(index);
	}

	public void addNode(Node node) {
		if (!this.nodes.contains(node))
			this.nodes.add(node);
	}

	public int size() {
		return this.nodes.size();
	}

	public void addAll(NodeSet nodeSet) {
		for (int i = 0; i < nodeSet.size(); i++) {
			this.addNode(nodeSet.getNode(i));
		}
	}

	public void removeNode(Node node) {
		this.nodes.remove(node);
	}

	public void clear() {
		this.nodes.clear();
	}
	
	public boolean isEmpty() {
		return this.nodes.isEmpty();
	}
	
	public boolean contains(Node node) {
		return this.nodes.contains(node);
	}

	public NodeSet Union(NodeSet ns) {
		NodeSet unionSet = new NodeSet();
		unionSet.addAll(this);
		unionSet.addAll(ns);
		return unionSet;
	}

	public NodeSet difference(NodeSet ns) {
		NodeSet intersectionSet = new NodeSet();
		for (int i = 0; i < ns.size(); i++) {
			if (!this.contains(ns.getNode(i)))
				intersectionSet.addNode(ns.getNode(i));
		}
		return intersectionSet;
	}

	public NodeSet Intersection(NodeSet ns) {
		NodeSet differenceSet = new NodeSet();
		for (int i = 0; i < this.size(); i++) {
			if (ns.contains(this.getNode(i)))
				differenceSet.addNode(this.getNode(i));
		}
		return differenceSet;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().getSimpleName().equals("NodeSet"))
			return false;

		NodeSet nodeSet = (NodeSet) obj;
		if (this.nodes.size() != nodeSet.size())
			return false;
		for (int i = 0; i < this.nodes.size(); i++) {
			if (!nodeSet.contains(this.nodes.get(i)))
				return false;
		}
		return true;
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	/**
	 * This method overrides the default toString method inherited from the
	 * Object class.
	 */
	@Override
	public String toString() {
		String s = "{";
		for (int i = 0; i < this.nodes.size(); i++) {
			s += this.nodes.get(i).toString();
			if (i < this.nodes.size() - 1)
				s += " ";
		}
		s += "}";
		return s;
	}
	
}
