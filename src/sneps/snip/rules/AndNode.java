package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snip.classes.RuleUseInfo;

public class AndNode extends RuleNode {
	private NodeSet consequents;

	public AndNode() {
		setConsequents(new NodeSet());
	}	
	public AndNode(Term syn) {
		super(syn);
		setConsequents(new NodeSet());
	}
	public AndNode(Semantic sym, Term syn) {
		super(sym, syn);
		setConsequents(new NodeSet());
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
	
	
	public NodeSet getConsequents() {
		return consequents;
	}
	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}

}
