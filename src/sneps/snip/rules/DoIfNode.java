package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Term;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

public class DoIfNode extends RuleNode {
	private static final long serialVersionUID = -262476672166406490L;

	public DoIfNode(Molecular syn) {
		super(syn);
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

	@Override
	protected RuisHandler createRuisHandler(String contextName) {
		// TODO Auto-generated method stub
		return null;
	}

}
