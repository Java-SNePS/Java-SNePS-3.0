package sneps.snip.rules;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snip.classes.RuleUseInfo;

public class NumericalNode extends RuleNode {
	private NodeSet consequents;
	private int i;

	public NumericalNode() {}
	public NumericalNode(Term syn) {
		super(syn);
	}
	public NumericalNode(Semantic sym, Term syn) {
		super(sym, syn);
	}

	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub

	}

	@Override
	public NodeSet getDownAntNodeSet(){
		//TODO this.getDownAntNodeSet();
		return null;
	}

	public NodeSet getConsequents() {
		return consequents;
	}
	public int getI() {
		return i;
	}

}
