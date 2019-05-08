package sneps.snip.classes;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import sneps.network.Node;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Support;

public class FlagNode {

	private Node node;
	private Support supports;
	private int flag;

	/**
	 * Create a new flag node
	 * 
	 * @param n       node
	 * @param support support
	 * @param f       true or false
	 */
	public FlagNode(Node n, Support support, int f) {
		node = n;
		supports = support;
		flag = f;
	}

	/**
	 * Return the node of the flag node
	 * 
	 * @return Node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Return the support of the flag node
	 * 
	 * @return support
	 */
	public Support getSupports() {
		return supports;
	}

	/**
	 * Return the flag of the flag node (1 is true, 2 is false, 3 is unknown and 4
	 * is requested)
	 * 
	 * @return Node
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * Check if this and fn are equal
	 * 
	 * @param fn flag node
	 * @return true or false
	 */
	public boolean isEqual(FlagNode fn) {
		return fn.node == node && fn.supports == supports && fn.flag == flag;
	}

	/**
	 * Set the value of the flag to x
	 * 
	 * @param x int
	 */
	public void setFlag(int x) {
		flag = x;
	}

}
