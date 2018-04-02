package sneps.snip.classes;

import java.util.HashSet;

public class PTree extends RuisHandler {

	public PTree() {
		// TODO Auto-generated constructor stub
	}

}

class PTreeNode {
	public PTreeNode parent;
	public PTreeNode sibling;
	public PTreeNode leftChild;
	public PTreeNode rightChild;
	private HashSet<RuleUseInfo> ruis;
	
	public PTreeNode(){
		parent = null;			sibling = null;
		leftChild = null;		rightChild = null;
	}
	

}