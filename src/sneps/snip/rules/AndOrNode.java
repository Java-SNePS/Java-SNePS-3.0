package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.PropositionNode;
import sneps.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.FlagNode;

public class AndOrNode extends RuleNode {

	boolean sign = false;
	
	private int min, max, args;
	
	int positiveCount = 0;
	public int getAndOrMin() {
		return min;
	}

	public int getAndOrMax() {
		return max;
	}

	public int getAndOrArgs() {
		return args;
	}

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
	public void applyRuleHandler(Report report, Node node) {
		super.applyRuleHandler(report, node);
		if(report.isPositive()==true)
			positiveCount++;
		
	
	}
	
	
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		
		if(positiveCount>=min && positiveCount<=max)
			sign=true;
		else
			sign=false;
		
		Set<Integer> consequents = new HashSet<Integer>();
		for (FlagNode fn : tRui.getFlagNodeSet()) {
			consequents.add(fn.getNode().getId());
		}
		
		Set<Support> originSupports = ((PropositionNode) this.getSemantic()).getOriginSupport();
		Report forwardReport = new Report(tRui.getSub(), tRui.getSupport(originSupports), sign,contextID);
		
		for (Channel outChannel : outgoingChannels) {
			if(!consequents.contains(outChannel.getRequester().getId()))
			outChannel.addReport(forwardReport);
		}
		
	}
	
	
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	public void applyRuleHandler(Report request, Node node) {
	
		
		if(request.isPositive()==true)
			positiveCount++;
		
	}
	
	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		
		if(positiveCount>=min && positiveCount<=max)
			sign=true;
		else
			sign=false;
		
		
		Set<Integer> consequents = new HashSet<Integer>();
		for (FlagNode fn : tRui.getFlagNodeSet()) {
			if (antNodesWithVarsIDs.contains(fn.getNode().getId()))
				continue;
			if (antNodesWithoutVarsIDs.contains(fn.getNode().getId()))
				continue;
			consequents.add(fn.getNode().getId());
		}
		Set<Support> originSupports = ((PropositionNode) this.getSemantic()).getOriginSupport();
		Report report = new Report(tRui.getSub(), tRui.getSupport(originSupports), sign,contextID);
		for (Channel outChannel : outgoingChannels) {
			if (!consequents.contains(outChannel.getRequester().getId()))
				continue;
			outChannel.addReport(report);

		}
	}

	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void sendReport(Report report) {
		
	}
	
	//Node node = fn.getNode().getDominatingRules().getNode(1);
	 
	//if(tRui.getFlagNodeSet().isMember())
*/
}
