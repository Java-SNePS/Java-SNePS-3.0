package sneps.snip.classes;

import java.util.Set;

import sneps.network.Node;
import sneps.snebr.Support;



public class FlagNode {
	private Node node;
	private Set<Support> supports;
	private int flag;

	/**
	 * Create a new flag node
	 * 
	 * @param n
	 *            node
	 * @param set
	 *            support
	 * @param f
	 *            true or false
	 */
	public FlagNode(Node n, Set<Support> set, int f) {
		node = n;
		supports = set;
		flag = f;
	}


	public boolean isEqual(FlagNode fn) {
		return fn.node == node && fn.supports == supports && fn.flag == flag;
	}


	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public Node getNode() {
		return node;
	}
	public Set<Support> getSupports() {
		return supports;
	}	
}
