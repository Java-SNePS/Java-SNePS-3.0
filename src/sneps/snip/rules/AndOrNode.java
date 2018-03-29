package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snip.classes.RuleUseInfo;

public class AndOrNode extends RuleNode {

	public AndOrNode() {
		// TODO Auto-generated constructor stub
	}

	public AndOrNode(Term syn) {
		super(syn);
		// TODO Auto-generated constructor stub
	}

	public AndOrNode(Semantic sym, Term syn) {
		super(sym, syn);
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
