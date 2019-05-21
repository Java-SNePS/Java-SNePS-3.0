package sneps.snip.classes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;

/**
 * @className PTree.java
 * 
 * @ClassDescription A PTree is a binary tree structure. Every AndEntailment rule node stores combinations of antecedent RUIs inside a PTree because of its optimized structure.
 * A PTree class follows an algorithm for construction and insertion, having leaf nodes representing RUIs of rule antecedents and root nodes representing combined RUIs of the rule itself.
 */
public class PTree extends RuisHandler {
	private Hashtable<Integer, VarNodeSet> patternVariables;//PatternId, VariableNodes
	private Hashtable<VariableNode, Set<Integer>> variablePatterns;//VariableNode, PatternIds
	private VarNodeSet notProccessed;
	private HashSet<PSubTree> subTrees;
	/**
	 * Maps every pattern to a subTree.
	 */
	private Hashtable<Integer, PSubTree> subTreesMap;

	/**
	 * Constructor for the PTree
	 */
	public PTree() {
		super();
		patternVariables = new Hashtable<Integer, VarNodeSet>();
		variablePatterns = new Hashtable<VariableNode, Set<Integer>>();
		notProccessed = new VarNodeSet();
		subTrees = new HashSet<PSubTree>();
		subTreesMap = new Hashtable<Integer, PSubTree>();
	}

	/**
	 * Builds the PTree by preparing pattern variables set, variable patterns set, pattern sequence and finally constructing the tree bottom up 
	 * @param ants
	 */
	public void buildTree(NodeSet ants) {
		fillPVandVP(ants);

		Queue<PTreeNode> patternSequence = getPatternSequence();
		
		constructBottomUp(patternSequence);

		patternVariables = new Hashtable<Integer, VarNodeSet>();
		variablePatterns = new Hashtable<VariableNode, Set<Integer>>();
		notProccessed = new VarNodeSet();
	}

	/**
	 * Prepares both pattern variables set and variable patterns set
	 * @param ants
	 */
	public void fillPVandVP(NodeSet ants) {
		for(Node pattern : ants) {
			int patId = pattern.getId();
			Term term = pattern.getTerm();
			VarNodeSet vars = patternVariables.get(patId);
			if(vars == null || vars.isEmpty())
				vars = new VarNodeSet();

			if(term instanceof Variable) {
				vars.addVarNode((VariableNode) pattern);
				notProccessed.addVarNode((VariableNode) pattern);
			}

			if(term instanceof Open) {
				VarNodeSet freeVars = ((Open) term).getFreeVariables();
				vars.addAll(freeVars);
				notProccessed.addAll(freeVars);
			}
			
			patternVariables.put(patId, vars);

			for(VariableNode curVar : vars) {
				Set<Integer> pats = variablePatterns.get(curVar);
				if(pats == null) {
					pats = new HashSet<Integer>();
					pats.add(patId);
					variablePatterns.put(curVar, pats);
					continue;
				}
				
				if(!pats.contains(patId)) {
					pats.add(patId);
					variablePatterns.put(curVar, pats);
				}
			}
		}
	}
	
	/**
	 * Prepares pattern sequence into a sequence of PTreeNodes ready for construction
	 * @return
	 */
	public Queue<PTreeNode> getPatternSequence() {
		LinkedHashSet<Integer> res = new LinkedHashSet<Integer>();
		Queue<PTreeNode> treeNodes = new LinkedList<PTreeNode>();
		VarNodeSet toBeProcessed = new VarNodeSet();
		
		while(!notProccessed.isEmpty()) {
			toBeProcessed.addVarNode(notProccessed.iterator().next());
			while (!toBeProcessed.isEmpty()) {
				VariableNode var = toBeProcessed.iterator().next();
				Set<Integer> vPatternsIds = variablePatterns.get(var);
				for(int currentPatId : vPatternsIds) {
					if(!res.contains(currentPatId)) {
						res.add(currentPatId);
						VarNodeSet patVarSet = patternVariables.get(currentPatId);
						toBeProcessed.addAll(patVarSet);
					}
				}
				
				toBeProcessed.remove(var);
				notProccessed.remove(var);
			}
		}
		
		for (int pat : res) {
			Set<Integer> pats = new HashSet<Integer>();
			pats.add(pat);
			treeNodes.add(new PTreeNode(pats, patternVariables.get(pat)));
		}
		
		return treeNodes;
	}
	
	/**
	 * Uses given ordered PTreeNodes to construct the PTree
	 * If no two adjacent nodes in the sequence share variables, a PSubTree is handled 
	 * @param treeNodes
	 */
	public void constructBottomUp(Queue<PTreeNode> treeNodes) {
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
				VarNodeSet vars = VarNodeSet.union(head.getVars(), second.getVars());
				PTreeNode treeNode = new PTreeNode(pats, vars);
				VarNodeSet intersection = getSharedVars(head, second);
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
		else
			for(PTreeNode subHead : newTreeNodes)
				processSubTree(subHead);
	}
	
	/**
	 * Creates a PSubTree and inserts it into subTrees and subTreesMap
	 * @param subHead
	 */
	private void processSubTree(PTreeNode subHead) {
		if(subHead != null) {
			PSubTree subTree = new PSubTree(subHead);
			subTrees.add(subTree);
			for (int id : subHead.getPats()) {
				if(subTree != null)
					subTreesMap.put(id, subTree);
			}
		}
	}

	@Override
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		Stack<RuleUseInfoSet> stack = new Stack<RuleUseInfoSet>();
		FlagNodeSet fns = rui.getFlagNodeSet();
		for(FlagNode node : fns) {
			int pattern = node.getNode().getId();
			PSubTree subTree = subTreesMap.get(pattern);
			RuleUseInfoSet returned = new RuleUseInfoSet();
			if(subTree != null)
				returned = subTree.insert(rui);
			for (PSubTree sub : subTrees) {
				if (sub == subTree)
					continue;
				RuleUseInfoSet tSet = sub.getRootRUIS();
				if (tSet == null)
					tSet = new RuleUseInfoSet();
				stack.push(tSet);
			}
			
			stack.push(returned);
		}
		
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

	private boolean sharingVars(PTreeNode head, PTreeNode second) {
		VarNodeSet smaller = null, bigger = null;
		if (head.getVars().size() > second.getVars().size()) {
			smaller = second.getVars();
			bigger = head.getVars();
		} else {
			bigger = second.getVars();
			smaller = head.getVars();
		}
		for (VariableNode i : smaller)
			if (bigger.contains(i))
				return true;
		return false;
	}
	
	private VarNodeSet getSharedVars(PTreeNode first, PTreeNode second) {
		VarNodeSet smaller = null, bigger = null, intersection = new VarNodeSet();
		if (first.getVars().size() > second.getVars().size()) {
			smaller = second.getVars();
			bigger = first.getVars();
		} else {
			bigger = second.getVars();
			smaller = first.getVars();
		}
		for (VariableNode i : smaller)
			if (bigger.contains(i))
				intersection.addVarNode(i);

		return intersection;
	}
	
	private Set<Integer> union(Set<Integer> vars1, Set<Integer> vars2) {
		Set<Integer> union = new HashSet<Integer>();
		for(int strng : vars1)
			union.add(strng);
		for(int strng : vars2)
			union.add(strng);
		return union;
	}

	public VarNodeSet getNotProccessed() {
		return notProccessed;
	}

	public void setNotProccessed(VarNodeSet notProccessed) {
		this.notProccessed = notProccessed;
	}

	public Hashtable<Integer, VarNodeSet> getPatternVariables() {
		return patternVariables;
	}

	public Hashtable<VariableNode, Set<Integer>> getVariablePatterns() {
		return variablePatterns;
	}

	public HashSet<PSubTree> getSubTrees() {
		return subTrees;
	}
	
	public void setSubTrees(HashSet<PSubTree> subTrees) {
		this.subTrees = subTrees;
	}
	
	public Hashtable<Integer, PSubTree> getSubTreesMap() {
		return subTreesMap;
	}

	public void setSubTreesMap(Hashtable<Integer, PSubTree> subTreesMap) {
		this.subTreesMap = subTreesMap;
	}
	
	public RuleUseInfoSet getAllRootRuis() {
		RuleUseInfoSet res = new RuleUseInfoSet();
		for(PSubTree subtree : subTrees)
			res.addAll(subtree.getRootRUIS());
		return res;
	}
	
	@Override
	public RuleUseInfoSet combineConstantRUI(RuleUseInfo rui) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void clear() {
		
	}

	/**
	 * @className PSubTree
	 * 
	 * @ClassDescription The PSubTree is used to handle the case where antecedent 
	 * patterns have disjoint variable unions, by keeping track of multiple PSubTrees 
	 * for every disjoint variable union inside the same PTree.
	 * 
	 */
	public class PSubTree {
		private PTreeNode root;

		public PSubTree(PTreeNode root){
			this.root = root;
		}

		public RuleUseInfoSet insert(RuleUseInfo rui) {
			FlagNodeSet fns = rui.getFlagNodeSet();
			RuleUseInfoSet res = new RuleUseInfoSet();
			for(FlagNode node : fns) {
				int pattern = node.getNode().getId();
				PTreeNode leaf = getLeafPattern(pattern, root);
				res = leaf.insertIntoTree(rui, res);
			}
			
			return res;
		}

		public PTreeNode getLeafPattern(int pattern, PTreeNode pNode) {
			if (pNode.getLeftChild() == null)
				return pNode;
			PTreeNode left = pNode.getLeftChild(), right = pNode.getRightChild();
			if (left.getPats().contains(pattern))
				return getLeafPattern(pattern, left);
			else
				return getLeafPattern(pattern, right);
		}

		public RuleUseInfoSet getRootRUIS() {
			RuleUseInfoSet ruis = root.getRUIS();
			if(ruis == null)
				return new RuleUseInfoSet();
			return ruis;
		}

		public PTreeNode getRoot(){
			return root;
		}
	}

	/**
	 * @className PTreeNode
	 * 
	 * @ClassDescription The PTreeNode is the lowest level in the PTree, 
	 * where RUIs are stored and combined. 
	 */
	public class PTreeNode {
		private PTreeNode parent, sibling;
		private PTreeNode leftChild, rightChild;
		/**
		 * Maps every set of bindings to a RUISet.
		 */
		private Hashtable<Integer, RuleUseInfoSet> ruisMap;
		private Set<Integer> pats;
		private VarNodeSet vars;
		private VarNodeSet siblingIntersection;

		public PTreeNode() {
			ruisMap = new Hashtable<Integer, RuleUseInfoSet>();
		}
		
		public PTreeNode(Set<Integer> p, VarNodeSet v) {
			this();
			pats = p;
			vars = v;
		}

		public RuleUseInfoSet insertIntoTree(RuleUseInfo rui, RuleUseInfoSet ruiSet) {
			int key = insertRUI(rui);
			if (sibling == null) {
				ruiSet.add(rui);
				return ruiSet;
			}
			RuleUseInfoSet siblingSet = sibling.getRUIS(key);
			if (siblingSet == null)
				return ruiSet;
			for (RuleUseInfo tRui : siblingSet) {
				RuleUseInfo combinedRui = rui.combine(tRui);
				if (combinedRui == null)
					continue;
				parent.insertIntoTree(combinedRui, ruiSet);
			}
			
			return ruiSet;
		}

		public void insertLeftAndRight(PTreeNode leftNode, PTreeNode rightNode,
				VarNodeSet intersection) {
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
 
			// The case when the node is other than the root
			int[] vs = new int[siblingIntersection.size()];
			int index = 0;
			for (VariableNode var : siblingIntersection) {
				if(rui.getSubstitutions().getBindingByVariable(var) != null)
					vs[index++] = rui.getSubstitutions().getBindingByVariable(var).getNode().getId();
			}
			int key = getKey(vs);
			RuleUseInfoSet ruis = ruisMap.get(vs);
			if (ruis == null) {
				ruis = new RuleUseInfoSet();
				ruisMap.put(key, ruis);
			}
			ruis.add(rui);
			return key;
		}

		public RuleUseInfoSet getRUIS() {
			RuleUseInfoSet ruiSet = new RuleUseInfoSet();
			Collection<RuleUseInfoSet> mappedRuis = ruisMap.values();
			for(RuleUseInfoSet singleSet : mappedRuis)
				ruiSet.addAll(singleSet);
			return ruiSet;
		}
		
		/**
		 * Get the index of a RuleUseInfo in the table by the ids of its
		 * substitutions
		 * 
		 * @param x
		 *            int[]
		 * @return int
		 */
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
		
		public VarNodeSet getVars() {
			return vars;
		}
		
		@Override
		public String toString(){
			return pats.toString();
		}
	}
}