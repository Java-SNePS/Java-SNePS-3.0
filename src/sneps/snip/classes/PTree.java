package sneps.snip.classes;

import java.util.HashSet;

public class PTree extends RuisHandler {

	public PTree() {
		// TODO Auto-generated constructor stub
	}

}

class PSubTree {
	private PTreeNode root;
	
	public PSubTree(){
		root = null;
	}
	public PSubTree(PTreeNode rot){
		root = rot;
	}
	
}

class PTreeNode {
	private PTreeNode parent;
	private PTreeNode sibling;
	private PTreeNode leftChild;
	private PTreeNode rightChild;
	private HashSet<RuleUseInfo> ruis;
	
	public PTreeNode(){
		parent = null;			sibling = null;
		leftChild = null;		rightChild = null;
	}
	

}