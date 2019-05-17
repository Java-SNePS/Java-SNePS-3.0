package sneps.snip.rules;

import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.RuleNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.term.Molecular;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;

public class ThreshEntailment extends RuleNode {
	private static final long serialVersionUID = 1L;
	
	private int min;
	private int max;
	
	public ThreshEntailment(Molecular syn) {
		super(syn);
		NodeSet minNode = getDownNodeSet("min");
		min = Integer.parseInt(minNode.getNode(0).getIdentifier());
		NodeSet maxNode = getDownNodeSet("max");
		max = Integer.parseInt(maxNode.getNode(0).getIdentifier());
		antecedents = getDownAntNodeSet();
		processNodes(antecedents);
	}

	@Override
	protected RuleResponse applyRuleOnRui(RuleUseInfo rui) {
		boolean reportSign = false;
		if (rui.getPosCount() == min
				&& rui.getNegCount() == getAntSize() - max - 1)
			reportSign = true;
		else if (rui.getPosCount() != min - 1
				|| rui.getNegCount() != getAntSize() - max)
			return null;
		
		PropositionSet replySupport = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				replySupport.union(fn.getSupport());
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}

		// TODO
		// Add rule node to replySupport
		
		consequents = antecedents.difference(rui.getFlagNodeSet().getAllNodes());
		
		Report reply = new Report(rui.getSubstitutions(), replySupport, reportSign, 
				rui.getType());
		reportsToBeSent.add(reply);
		
		RuleResponse r = new RuleResponse();
		r.setReport(reply);
		Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
		r.addAllChannels(forwardChannels);
		
		return r;
	}

	@Override
	public NodeSet getDownAntNodeSet() {
		return getDownNodeSet("arg");
	}

	@Override
	public NodeSet getDownConsqNodeSet() {
		return null;
	}

	@Override
	protected RuisHandler createRuisHandler() {
		return new RuleUseInfoSet(false);
	}

	@Override
	protected byte getSIndexType() {
		return SIndex.RUIS;
	}
	
	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

}
