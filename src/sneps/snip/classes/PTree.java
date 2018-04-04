package sneps.snip.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.setClasses.NodeSet;

@SuppressWarnings("unused")//TODO Not final
public class PTree extends RuisHandler {
	private Hashtable<Integer, Set<Node>> patternVariables,variablesPattern;
	private HashSet<Node> patterns,notProccessed;
	private HashSet<PSubTree> subTrees;

	public PTree() {
		patternVariables = new Hashtable<Integer, Set<Node>>();
		variablesPattern = new Hashtable<Integer, Set<Node>>();
		patterns = new HashSet<Node>();
		notProccessed = new HashSet<Node>();
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
			patterns.add(pattern);
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
				notProccessed.add(var);
				Set<Node> pats = variablesPattern.get(varId);
				if (pats == null) {
					pats = new HashSet<Node>();
					variablesPattern.put(varId, pats);
				}
				pats.add(pattern);
			}
		}
		
	}
	private void getPatternSequence() {
		// TODO getPatternSequence()
		
	}
	private void constructBottomUp() {
		// TODO Auto-generated method stub
		
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
}