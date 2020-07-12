package sneps.snip.rules;

import java.util.ArrayList;
import java.util.Set;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.Runner;
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
	protected ArrayList<RuleResponse> applyRuleOnRui(RuleUseInfo rui) {
		boolean reportSign = false;
		if (rui.getNegCount() == getAntSize() - min)
			reportSign = true;
		else if (rui.getPosCount() != max)
			return null;
		
		consequents = antecedents.difference(rui.getFlagNodeSet().getAllNodes());
		ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
		RuleResponse response = new RuleResponse();
		Report reply;
		
		PropositionSet replySupport = new PropositionSet();
		PropositionSet ruleSupport = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				replySupport = replySupport.union(fn.getSupport());
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
			}
		
		if(this.getTerm() instanceof Closed) {
			try {
				ruleSupport = ruleSupport.add(this.getId());
				replySupport = replySupport.union(ruleSupport);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | 
					DuplicatePropositionException e) {
			}
			
			if(Runner.isNodeAssertedThroughForwardInf(this))
				reply = new Report(rui.getSubstitutions(), replySupport, reportSign, 
						InferenceTypes.FORWARD);
			else
				reply = new Report(rui.getSubstitutions(), replySupport, reportSign, 
						rui.getType());
			
			reportsToBeSent.add(reply);
			response.setReport(reply);
			Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
			response.addAllChannels(forwardChannels);
			responseList.add(response);
		}
		else if(this.getTerm() instanceof Open) {
			// knownInstances contain instances found for the rule node itself
			if(knownInstances.isEmpty())
				return null;
			VarNodeSet freeVars = ((Open) this.getTerm()).getFreeVariables();
			boolean allBound;
			Report ruiReport = new Report(rui.getSubstitutions(), replySupport, 
					reportSign, rui.getType());
			for(Report r : knownInstances) {
				// Only positive reports should be considered
				if(r.getSign() == false)
					continue;
				
				allBound = true;
				// Check that each free variable in this rule node is bound in the 
				// current report
				for(VariableNode var : freeVars) {
					if(!r.getSubstitutions().isBound(var)) {
						allBound = false;
						break;
					}
				}
				
				// All free variables of this rule node are bound in the current report
				if(allBound) {
					reply = ruiReport.combine(r);
					if(reply != null) {
						response.clear();
						response.setReport(reply);
						Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
						response.addAllChannels(forwardChannels);
						responseList.add(response);
					}
				}
			}
		}
		
		if(responseList.isEmpty())
			return null;
		
		return responseList;
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