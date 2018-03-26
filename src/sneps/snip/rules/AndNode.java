package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snip.classes.RuleUseInfo;

public class AndNode extends RuleNode {

	public AndNode() {}
	
	public AndNode(Term syn) {
		super(syn);
	}

	public AndNode(Term syn, Semantic sym) {
		super(syn, sym);
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
