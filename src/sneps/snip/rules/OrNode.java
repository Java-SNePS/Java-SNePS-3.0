package sneps.snip.rules;

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
	
	public OrNode() {}

	public OrNode(Term syn) {
		super(syn);
	}

	public OrNode(Semantic sym, Term syn) {
		super(sym, syn);
	}
	
	public void applyRuleHandler(Report report, Node node) {
		
		if(report.isPositive()) {
			
			Set<Support> originSupports = ((Proposition) this.getSemantic()).getOriginSupport();
			Report reply = new Report(report.getSubstitutions(), Support.combine(originSupports,report.getSupports()), true, report.getContextName());
			
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(reply);
			
		}
		
		if(report.isNegative()) {
			
			Set<Support> originSupports = ((Proposition) this.getSemantic()).getOriginSupport();
			Report forwardReport = new Report(report.getSubstitutions(), Support.combine(originSupports,report.getSupports()), false, report.getContextName());
			
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(forwardReport);
		}
		
}
	
	/*
	public void applyRuleHandler(Report request, Node node) {
		
		if(request.isPositive()) {
			
			Set<Support> originSupports = ((Proposition) this.getSemantic()).getOriginSupport();
			Report report = new Report(request.getSubstitutions(), Support.combine(originSupport,request.getSupports()), true, request.getContextName());
			
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(report);
			
		}
		
		if(request.isNegative()) {
			
			Set<Support> originSupports = ((Proposition) this.getSemantic()).getOriginSupport();
			Report report = new Report(request.getSubstitutions(), Support.combine(originSupport,request.getSupports()), false, request.getContextName());
			
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(report);
		}
	}

*/
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
