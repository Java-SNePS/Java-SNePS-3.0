package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.classes.RuleUseInfo;

public class NumericalEntailment extends RuleNode {
	private NodeSet consequents;
	private int i;

	public NumericalEntailment(Term syn) {
		super(syn);
	}
	public NumericalEntailment(Semantic sym, Term syn) {
		super(sym, syn);
	}

	@Override
	protected void applyRuleOnRui(RuleUseInfo rui, String contextID) {
		if (rui.getPosCount() >= i){
			Set<Support> originSupports = new HashSet<Support>();
			originSupports.add(this.getBasicSupport());
			Report reply = new Report(rui.getSub(),rui.getSupport(originSupports), true, contextID);
			broadcastReport(reply);
		}
	}

	@Override
	public NodeSet getDownAntNodeSet(){
		return this.getDownNodeSet("iant");
	}

	public NodeSet getConsequents() {
		return consequents;
	}
	public int getI() {
		return i;
	}

}
