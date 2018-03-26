package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snip.classes.RuleUseInfo;

public class NumericalNode extends RuleNode {

	public NumericalNode() {
		// TODO Auto-generated constructor stub
	}

	public NumericalNode(Term syn) {
		super(syn);
		// TODO Auto-generated constructor stub
	}

	public NumericalNode(Term syn, Semantic sym) {
		super(syn, sym);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub

	}

	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

}
