package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.PropositionNode;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;

public class OrNode extends RuleNode {

	private int ant,cq;
	

	public OrNode(Term syn) {
		super(syn);
		ant = getDownNodeSet("ant").size();
		cq = getDownNodeSet("cq").size();
	}

	public OrNode(Semantic sym, Term syn) {
		super(sym, syn);
		ant = getDownNodeSet("ant").size();
		cq = getDownNodeSet("cq").size();
	}
	
	public void applyRuleHandler(Report report, Node node) {
		
		if(report.isPositive()) {
			
			Support originSupports = this.getBasicSupport();
			HashSet<Support> sup = new HashSet<Support>();
			sup.add(originSupports);
			Report reply = new Report(report.getSubstitutions(), sup, true, report.getContextName());
			
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(reply);
			
		}
		
}
	


	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Vant");
	}

	@Override
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		
	}

}
