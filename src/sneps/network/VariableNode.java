package sneps.network;

import sneps.network.classes.Semantic;
import sneps.network.classes.term.Variable;

public class VariableNode extends Node {

	public VariableNode(Variable trm) {
		super(trm);
	}

	public VariableNode(Semantic semantic, Variable v) {
		super(semantic, v);
	}

	public boolean hasSameFreeVariableAs(VariableNode node) {
		if(!((Variable)this.getTerm())
				.equals(((Variable)node.getTerm())))
				return false;
		
		return true;
	}

}
