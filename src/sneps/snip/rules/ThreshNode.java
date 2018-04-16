package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.PropositionNode;
import sneps.network.classes.term.Term;
import sneps.setClasses.NodeSet;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.FlagNode;

public class ThreshNode extends RuleNode {

	boolean sign = false;
	
	private int min, max, args;
	int positiveCount = 0;
	
	public int getThreshMin() {
		return min;
	}

	public int getThreshMax() {
		return max;
	}

	public int getThreshArgs() {
		return args;
	}

	public ThreshNode() {
		// TODO Auto-generated constructor stub
	}

	public ThreshNode(Term syn) {
		super(syn);
		// TODO Auto-generated constructor stub
	}

	public ThreshNode(Semantic sym, Term syn) {
		super(sym, syn);
	}

	public void applyRuleHandler(Report request, Node node) {
		
		if(request.isPositive()==true)
			positiveCount++;
		
		for (Channel outChannel : outgoingChannels)
			outChannel.addReport(request);
		
	}
	
	
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		
		if(min!=max) {
			if(positiveCount<min || positiveCount>max)
				sign=true;
			else
				sign=false;
		} else {
			if(positiveCount>0 && positiveCount!=min)
				sign=true;
			else
				sign=false;
		}
		
		
		
		Set<Integer> consequents = new HashSet<Integer>();
		for (FlagNode fn : tRui.getFlagNodeSet()) {
			consequents.add(fn.getNode().getId());
		}
		
		Support originSupports = ((PropositionNode) this).getSemantic().getBasicSupport();
		Report forwardReport = new Report(tRui.getSub(), tRui.getSupport(originSupports), sign,contextID);
		
		for (Channel outChannel : outgoingChannels) {
			if(!consequents.contains(outChannel.getRequester().getId()))
			outChannel.addReport(forwardReport);
		}
		
	}
	
	/*
	public void applyRuleHandler(Report request, Node node) {
		
		if(request.isPositive()==true)
			positiveCount++;
		
	}
	
	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		if(min!=max) {
			if(positiveCount<=min || positiveCount>=max)
				sign=true;
			else
				sign=false;
		} else {
			if(positiveCount>0)
				sign=true;
			else
				sign=false;
		}
		
		
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
	*/


	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

}
