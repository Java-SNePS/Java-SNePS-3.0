package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snip.classes.RuleUseInfo;

public class DoIfNode extends RuleNode {

	public DoIfNode() {}
	
	public DoIfNode(Term syn) {
		super(syn);
	}

	public DoIfNode(Semantic sem, Term syn) {
		super(sem, syn);
	}


	@Override
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

}
