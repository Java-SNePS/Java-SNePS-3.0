package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snip.classes.RuleUseInfo;

public class WhenDoNode extends RuleNode {
	private static final long serialVersionUID = 2515697705889848498L;

	public WhenDoNode(Term syn) {
		super(syn);
	}


//	@Override
//	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
//		// TODO Auto-generated method stub
//		
//	}


	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	protected RuisHandler createRuisHandler(String contextName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub
		
	}

}
