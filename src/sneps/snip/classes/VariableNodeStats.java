package sneps.snip.classes;

import java.util.Vector;

import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;

public class VariableNodeStats {
	private boolean allVariablesBound;
	private Substitutions referenceSubs;
	private int nodeId;
	private Substitutions variableNodeSubs;

	public VariableNodeStats(boolean variablesBound, Substitutions extractedFilterRelevantToVariables,
			Substitutions refSubs, int id) {
		allVariablesBound = variablesBound;
		variableNodeSubs = extractedFilterRelevantToVariables;
		referenceSubs = refSubs;
		nodeId = id;
	}

	public VariableNodeStats(boolean variablesBound, Substitutions extractedFilterRelevantToVariables,
			Substitutions refSubs) {
		allVariablesBound = variablesBound;
		variableNodeSubs = extractedFilterRelevantToVariables;
		referenceSubs = refSubs;
	}

	public Substitutions getReferenceSubs() {
		return referenceSubs;
	}

	public void setReferenceSubs(Substitutions referenceSubs) {
		this.referenceSubs = referenceSubs;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	/***
	 * Method returns whether all freeVariables concerning this node are bound
	 * through the origin substitutions or not
	 * 
	 * @return boolean
	 */
	public boolean areAllVariablesBound() {
		return allVariablesBound;
	}

	public void setAllVariablesBound(boolean allVariablesBound) {
		this.allVariablesBound = allVariablesBound;
	}

	public Substitutions getVariableNodeSubs() {
		return variableNodeSubs;
	}

	public void setVariableNodeSubs(Substitutions variableNodeSubs) {
		this.variableNodeSubs = variableNodeSubs;
	}

	public boolean isSubSet(Substitutions variableSubstitutions) {
		return variableNodeSubs.isSubSet(variableSubstitutions);
	}

}
