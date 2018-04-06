package sneps.snip.classes;

import java.util.Set;

import sneps.network.Node;
import sneps.snebr.Support;

public class FlagNode {
	private Node node;
	private Set<Support> supports;
	private int flag;

	public FlagNode(Node signature, Set<Support> supports, int i) {
		// TODO Auto-generated constructor stub
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
