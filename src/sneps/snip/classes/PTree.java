package sneps.snip.classes;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.classes.term.Variable;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.setClasses.VariableSet;

//@SuppressWarnings("unused")//TODO Not final
public class PTree extends RuisHandler {
	private Hashtable<Integer, Set<Variable>> patternVariables;//PatId, Variables
	private Hashtable<Integer, Set<Integer>> variablePatterns;//VarId, Patterns
	private VariableSet vars;
	private NodeSet nodes;
	private Set<Integer> notProccessed;
	private HashSet<PSubTree> subTrees;
	private Hashtable<Integer, PSubTree> subTreesMap;

	public PTree() {
		patternVariables = new Hashtable<Integer, Set<Variable>>();
		variablePatterns = new Hashtable<Integer, Set<Integer>>();
		vars = new VariableSet();
		notProccessed = new HashSet<Integer>();
		subTrees = new HashSet<PSubTree>();
	}

	public void buildTree(NodeSet ants){
		//TODO Clear afterwards?
		nodes = ants;
		fillPVandVP(ants);

		LinkedHashSet<Integer> patternSequence = getPatternSequence();
		Queue<PTreeNode> treeNodes = new LinkedList<PTreeNode>();
		for (int pat : patternSequence) {
			Set<Integer> pats = new HashSet<Integer>();
			pats.add(pat);
			
			Set<Variable> varSet = patternVariables.get(pat);
			Set<Integer> proccessed = new HashSet<Integer>();
			for(Variable var : varSet)
				proccessed.add(var.getId());
			
			treeNodes.add(new PTreeNode(pats, proccessed));
		}

		constructBottomUp(treeNodes);
	}

	private void fillPVandVP(NodeSet ants) {
		Set<Variable> tempVars = null;
		for(int i=0; i < ants.size(); i++){
			Node pattern = ants.getNode(i);
			int id = pattern.getId();

			VariableNode patVarNode = null;
			if(pattern instanceof VariableNode)
				patVarNode = (VariableNode) pattern;

			if(patVarNode != null){
				VariableSet patVars = patVarNode.getFreeVariables();
				tempVars = patternVariables.get(id);
				
				if(tempVars == null){
					tempVars = new HashSet<Variable>();
					
					for(Variable currentVar : patVars){
						int varId = currentVar.getId();
						
						vars.addVariable(currentVar);
						tempVars.add(currentVar);
						notProccessed.add(varId);
						
						Set<Integer> pats = variablePatterns.get(varId);
						if (pats == null)
							pats = new HashSet<Integer>();
						pats.add(pattern.getId());
						
						variablePatterns.put(varId, pats);
					}//VP filled
					patternVariables.put(id, tempVars);
				}//PV filled
			}
		}

	}
	private LinkedHashSet<Integer> getPatternSequence() {
		//TODO Really, That's it?
		LinkedHashSet<Integer> res = new LinkedHashSet<Integer>();
		//Set<String> proccessed = new HashSet<String>();
		
		for(int currentVarId : notProccessed){
			Set<Integer> vPatternsIds = variablePatterns.get(currentVarId);

			for(int currentPatId : vPatternsIds)
				if(!res.contains(currentVarId))
					res.add(currentPatId);
			
		}
		return res;
		/*while (!notProccessed.isEmpty()) {
			toBeProccessed.add(peek(notProccessed));
			while (!toBeProccessed.isEmpty()) {
				String varId = peek(toBeProccessed);
				Iterator<Integer> varPatsIter = variablePatterns.get(varId)
						.iterator();
				while (varPatsIter.hasNext()) {
					int pat = varPatsIter.next();
					if (res.contains(pat))
						continue;
					res.add(pat);
					Set<Variable> patVarSet = patternVariables.get(pat);
					toBeProccessed.addAll(patVarSet);
				}
				toBeProccessed.remove(varId);
				notProccessed.remove(varId);
			}
		}
		res.addAll(patterns);*/
	}
	private void constructBottomUp(Queue<PTreeNode> treeNodes) {
		// TODO constructBottomUp
		PTreeNode head = treeNodes.poll();
		if (treeNodes.isEmpty()) {
			processSubTree(head);
			return;
		}
		Queue<PTreeNode> newTreeNodes = new LinkedList<PTreeNode>();
		boolean sharing = false;
		while (!treeNodes.isEmpty()) {
			PTreeNode second = treeNodes.poll();
			if (sharingVars(head, second)) {
				sharing = true;
				Set<Integer> pats = union(head.getPats(), second.getPats());
				Set<Integer> vars = union(head.getVars(), second.getVars());
				PTreeNode treeNode = new PTreeNode(pats, vars);
				Set<Integer> intersection = getSharedVars(head, second);
				treeNode.insertLeftAndRight(head, second, intersection);
				newTreeNodes.add(treeNode);
				head = treeNodes.poll();
			} else {
				newTreeNodes.add(head);
				head = second;
			}
		}
		if (head != null)
			newTreeNodes.add(head);
		if (sharing)
			constructBottomUp(newTreeNodes);
		else{
			for(PTreeNode subHead : newTreeNodes)
				processSubTree(subHead);
		}

	}


	private void processSubTree(PTreeNode subHead) {
		PSubTree subTree = new PSubTree(subHead);
		subTrees.add(subTree);
		for (int id : subHead.getPats()) {
			subTreesMap.put(id, subTree);
		}
	}

	@Override
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		int pattern = rui.getFlagNodeSet().iterator().next().getNode().getId();
		PSubTree subTree = subTreesMap.get(pattern);
		RuleUseInfoSet returned = subTree.insert(rui);
		Stack<RuleUseInfoSet> stack = new Stack<RuleUseInfoSet>();
		for (PSubTree sub : subTrees) {
			if (sub == subTree)
				continue;
			RuleUseInfoSet tSet = sub.getRootRUIS();
			if (tSet == null)
				continue;
			stack.push(tSet);
		}
		stack.push(returned);
		return multiply(stack);
	}
	private RuleUseInfoSet multiply(Stack<RuleUseInfoSet> infoSets) {
		RuleUseInfoSet first = infoSets.pop();
		while (!infoSets.isEmpty()) {
			RuleUseInfoSet second = infoSets.pop();
			first = first.combine(second);
		}
		return first;
	}

	private String peek(Set<String> set) {
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
	private Set<Integer> getSharedVars(PTreeNode first, PTreeNode second) {
		Set<Integer> smaller = null, bigger = null, intersection = new HashSet<Integer>();
		if (first.getVars().size() > second.getVars().size()) {
			smaller = second.getVars();
			bigger = first.getVars();
		} else {
			bigger = second.getVars();
			smaller = first.getVars();
		}
		for (int i : smaller)
			if (bigger.contains(i))
				intersection.add(i);
		return intersection;
	}
	private Set<Integer> union(Set<Integer> vars1, Set<Integer> vars2) {
		Set<Integer> union = new HashSet<Integer>();
		for(int strng : vars1){
			if(vars2.contains(strng))
				union.add(strng);
		}
		return union;
	}

	private class PSubTree {
		private PTreeNode root;

		public PSubTree(){
			root = null;
		}
		public PSubTree(PTreeNode rot){
			root = rot;
		}

		public RuleUseInfoSet insert(RuleUseInfo rui) {
			int pattern = rui.getFlagNodeSet()
					.iterator().next()
					.getNode().getId();
			PTreeNode leaf = getLeafPattern(pattern, root);
			RuleUseInfoSet res = new RuleUseInfoSet();
			leaf.insertIntoTree(rui, res);
			return res;
		}

		private PTreeNode getLeafPattern(int pattern, PTreeNode pNode) {
			if (pNode.getLeftChild() == null)
				return pNode;
			PTreeNode left = pNode.getLeftChild(), right = pNode.getRightChild();
			if (left.getPats().contains(pattern))
				return getLeafPattern(pattern, left);
			else
				return getLeafPattern(pattern, right);
		}

		public RuleUseInfoSet getRootRUIS() {
			return root.getRUIS(0);
		}

	}

	private class PTreeNode {
		private PTreeNode parent, sibling;
		private PTreeNode leftChild, rightChild;
		private HashSet<RuleUseInfo> ruis;
		private Hashtable<Integer, RuleUseInfoSet> ruisMap;
		private Set<Integer> pats;
		private Set<Integer> vars;
		private Set<Integer> siblingIntersection;

		public PTreeNode(){
			ruisMap = new Hashtable<Integer, RuleUseInfoSet>();
			parent = null;			sibling = null;
			leftChild = null;		rightChild = null;
			pats = null;			vars = null;
		}
		public PTreeNode(Set<Integer> p, Set<Integer> v){
			this();
			pats = p;				vars = v;
		}

		public void insertIntoTree(RuleUseInfo rui, RuleUseInfoSet ruiSet) {
			int key = insertRUI(rui);
			if (sibling == null) {
				ruiSet.add(rui);
				return;
			}
			RuleUseInfoSet siblingSet = sibling.getRUIS(key);
			if (siblingSet == null)
				return;
			for (RuleUseInfo tRui : siblingSet) {
				RuleUseInfo combinedRui = rui.combine(tRui);
				if (combinedRui == null)
					continue;
				parent.insertIntoTree(combinedRui, ruiSet);
			}
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
			if (sibling == null) {
				RuleUseInfoSet ruiSet = ruisMap.get(0);
				if (ruiSet == null) {
					ruiSet = new RuleUseInfoSet();
					ruisMap.put(0, ruiSet);
				}
				ruiSet.add(rui);
				return 0;
			}

			int[] ids = new int[siblingIntersection.size()];
			int index = 0;
			for (int id : siblingIntersection)
				ids[index++] = rui.getSubstitutions().termID(id);

			int key = getKey(ids);
			RuleUseInfoSet ruis = ruisMap.get(key);
			if (ruis == null) {
				ruis = new RuleUseInfoSet();
				ruisMap.put(key, ruis);
			}
			ruis.add(rui);
			return key;
		}

		public RuleUseInfoSet getRUIS(int index) {
			return ruisMap.get(index);
		}

		public PTreeNode getLeftChild() {
			return leftChild;
		}
		public PTreeNode getRightChild() {
			return rightChild;
		}
		public Set<Integer> getPats() {
			return pats;
		}
		public Set<Integer> getVars() {
			return vars;
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