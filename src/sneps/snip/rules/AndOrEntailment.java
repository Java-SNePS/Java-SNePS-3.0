package sneps.snip.rules;

import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.RuleNode;
import sneps.network.cables.DownCableSet;
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

public class AndOrEntailment extends RuleNode {
	private static final long serialVersionUID = 1L;
	
	private int min;
	private int max;
	
	public AndOrEntailment(Molecular syn) {
		super(syn);
		/*NodeSet minNode = getDownNodeSet("min");
		min = Integer.parseInt(minNode.getNode(0).getIdentifier());
		NodeSet maxNode = getDownNodeSet("max");
		max = Integer.parseInt(maxNode.getNode(0).getIdentifier());*/
		antecedents = getDownAntNodeSet();
		processNodes(antecedents);
	}

	@Override
	protected RuleResponse applyRuleOnRui(RuleUseInfo rui) {
		boolean reportSign = false;
		if (rui.getNegCount() == getAntSize() - min)
			reportSign = true;
		else if (rui.getPosCount() != max)
			return null;
		
		/*consequents = antecedents.difference(rui.getFlagNodeSet().getAllNodes());
		ArrayList<RuleResponse> res = new ArrayList<RuleResponse>();
		RuleResponse response = new RuleResponse();
		Report reply;
		PropositionSet replySupport = new PropositionSet();
		PropositionSet ruleSupport = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				replySupport.union(fn.getSupport());
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}
		
		if(this.getTerm() instanceof Closed) {
			try {
				ruleSupport = ruleSupport.add(this.getId());
				replySupport.union(ruleSupport);
			} catch (DuplicatePropositionException | NotAPropositionNodeException | 
					NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}
			
			if(Runner.isNodeAssertedThroughForwardInf(this))
				reply = new Report(rui.getSubstitutions(), replySupport, reportSign, 
						InferenceTypes.FORWARD);
			else
				reply = new Report(rui.getSubstitutions(), replySupport, reportSign, 
						rui.getType());
			
			response.setReport(reply);
			Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
			response.addAllChannels(forwardChannels);
			res.add(response);
		}
		else if(this.getTerm() instanceof Open) {
			Report ruiReport = new Report(rui.getSubstitutions(), replySupport, 
					reportSign, rui.getType());
			for(Report r : knownInstances) {
				reply = ruiReport.combine(r);
				if(reply != null) {
					response.setReport(reply);
					Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
					response.addAllChannels(forwardChannels);
					res.add(response);
					response.clear();
				}
			}
		}
		
		return res;*/
		
		PropositionSet replySupport = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				replySupport.union(fn.getSupport());
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}

		// TODO
		// Add rule node to replySupport
		// If Closed, just return this
		// Else, from knownInstances which contains instances found for the rule node itself
		// Check for report type, get support to union with rui, and get subs to union with rui
		 
		// If node is Closed, need to check if it was asserted thru forward inference, 
		// by calling method isAssertedthruForwardInference(), to get type of replyReport
		consequents = antecedents.difference(rui.getFlagNodeSet().getAllNodes());
		
		//System.out.println(rui.getFlagNodeSet().getAllNodes());
		//System.out.println(consequents);
		Report reply = new Report(rui.getSubstitutions(), replySupport, reportSign, 
				rui.getType());
		//System.out.println(reply);
		reportsToBeSent.add(reply);
		
		RuleResponse r = new RuleResponse();
		r.setReport(reply);
		//Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
		//r.addAllChannels(forwardChannels);
		
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