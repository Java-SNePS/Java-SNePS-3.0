package sneps.snip.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;

@SuppressWarnings("unused")//TODO Not final
public class PTree extends RuisHandler {
	private Hashtable<Integer, Set<Node>> patternVariables,variablePatterns;
	private Set<Integer> patterns,notProccessed;
	private HashSet<PSubTree> subTrees;

	public PTree() {
		patternVariables = new Hashtable<Integer, Set<Node>>();
		variablePatterns = new Hashtable<Integer, Set<Node>>();
		patterns = new HashSet<Integer>();
		notProccessed = new HashSet<Integer>();
		subTrees = new HashSet<PSubTree>();
	}


	public void buildTree(NodeSet ants){
		fillPVandVP(ants);
		
		getPatternSequence();
		
		constructBottomUp();
	}

	
	private void fillPVandVP(NodeSet ants) {
		// TODO fillVP(NodeSet ants)
		for(int i=0; i < ants.size(); i++){
			Node pattern = ants.getNode(i);
			int id = pattern.getId();
			patterns.add(id);
			Set<Node> vars = patternVariables.get(pattern.getId());
			if (vars == null) {
				vars = new HashSet<Node>();
				patternVariables.put(id, vars);
			}
			//Iterator<VariableNode> nodeIter = ((NodeWithVar) pat).getFreeVariables().iterator();
			VariableNode patVarNode = (VariableNode) pattern;;
			/*if(pattern instanceof VariableNode)
				patVarNode = (VariableNode) pattern;*/
			
			while (true/*nodeIter.hasNext()*/) {
				Node var = patVarNode; //= nodeIter.next(); patVarNode.getFreeVariables()
				int varId = var.getId();
				vars.add(var);
				notProccessed.add(varId);
				Set<Node> pats = variablePatterns.get(varId);
				if (pats == null) {
					pats = new HashSet<Node>();
					variablePatterns.put(varId, pats);
				}
				pats.add(pattern);
			}
		}
		
	}
	private void getPatternSequence() {
		// TODO getPatternSequence()
		/*LinkedHashSet<Integer> res = new LinkedHashSet<Integer>();
		Set<Node> toBeProccessed = new HashSet<Node>();
		while (!notProccessed.isEmpty()) {
			toBeProccessed.add(peek(notProccessed));
			while (!toBeProccessed.isEmpty()) {
				int var = peek(toBeProccessed);
				Iterator<Node> varPatsIter = variablePatterns.get(var).iterator();
				while (varPatsIter.hasNext()) {
					Node pat = varPatsIter.next();
					int patId = pat.getId();
					if (res.contains(pat))
						continue;
					res.add(patId);
					Set<Node> patVarSet = patternVariables.get(pat);
					toBeProccessed.add(patVarSet);
				}
				toBeProccessed.remove(var);
				notProccessed.remove(var);
			}
		}
		res.addAll(patterns);*/
	}
	private void constructBottomUp() {
		// TODO Auto-generated method stub
		
	}

	private int peek(Set<Integer> set) {
		return set.iterator().next();
	}
	private boolean sharingVars(PTreeNode head, PTreeNode second) {
		Set<Integer> smaller = null, bigger = null;
		if (head.getVars().size() > second.getVars().size()) {
			smaller = second.getVars();
			bigger = head.getVars();
		} else {
			bigger = second.getVars();
			smaller = head.getVars();
		}
		for (int i : smaller)
			if (bigger.contains(i))
				return true;
		return false;
	}

	private class PSubTree {
		private PTreeNode root;

		public PSubTree(){
			root = null;
		}
		public PSubTree(PTreeNode rot){
			root = rot;
		}

	}

	private class PTreeNode {
		private PTreeNode parent, sibling;
		private PTreeNode leftChild, rightChild;
		private HashSet<RuleUseInfo> ruis;
		private Set<Integer> pats, vars;
		private Set<Integer> siblingIntersection;

		public PTreeNode(){
			parent = null;			sibling = null;
			leftChild = null;		rightChild = null;
		}
		
		public void insertLeftAndRight(PTreeNode leftNode, PTreeNode rightNode,
				Set<Integer> intersection) {
			leftNode.parent = this;
			rightNode.parent = this;
			leftNode.sibling = rightNode;
			rightNode.sibling = leftNode;
			leftNode.siblingIntersection = intersection;
			rightNode.siblingIntersection = intersection;
			leftChild = leftNode;
			rightChild = rightNode;
		}
		
		public int insertRUI(RuleUseInfo rui) {
			/*TODO insertRUI
			// Since it has no sibling, it has no parent, and it is the root
			if (sibling == null) {
				RuleUseInfoSet ruis = ruisMap.get(0);
				if (ruis == null) {
					ruis = new RuleUseInfoSet();
					ruisMap.put(0, ruis);
				}
				ruis.add(rui);
				return 0;
			}
			// The upper part of this method is for incase this node is aroot
			// node
			// and the lower part for other nodes in the tree
			int[] ids = new int[siblingIntersection.size()];
			int index = 0;
			for (int id : siblingIntersection) {
				ids[index++] = rui.getSub().termID(id);
			}
			int key = getKey(ids);
			RuleUseInfoSet ruis = ruisMap.get(key);
			if (ruis == null) {
				ruis = new RuleUseInfoSet();
				ruisMap.put(key, ruis);
			}
			ruis.add(rui);
			return key;*/
			return 0;
		}

		public PTreeNode getParent() {
			return parent;
		}
		public PTreeNode getSibling() {
			return sibling;
		}
		public PTreeNode getLeftChild() {
			return leftChild;
		}
		public PTreeNode getRightChild() {
			return rightChild;
		}
		public HashSet<RuleUseInfo> getRuis() {
			return ruis;
		}
		public Set<Integer> getPats() {
			return pats;
		}
		public Set<Integer> getVars() {
			return vars;
		}
		public Set<Integer> getSiblingIntersection() {
			return siblingIntersection;
		}
		private int getKey(int[] x) {
			int p = 16777619;
			int hash = (int) 2166136261L;
			for (int i = 0; i < x.length; ++i) {
				hash += (hash ^ x[i]) * p;
			}
			hash += hash << 13;
			hash ^= hash >> 7;
			hash += hash << 3;
			hash ^= hash >> 17;
			hash += hash << 5;
			return hash;
		}
		@Override
		public String toString(){
			return pats.toString();
		}

	}
}