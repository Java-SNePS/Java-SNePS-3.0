package sneps.snip.rules;

import java.util.ArrayList;
import java.util.Set;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;

public class OrEntailment extends RuleNode {
	private static final long serialVersionUID = 1L;
	
	public OrEntailment(Molecular syn) {
		super(syn);
		antecedents = getDownAntNodeSet();
		consequents = getDownConsqNodeSet();
	}
	
	public ArrayList<RuleResponse> applyRuleHandler(Report report, Node signature) {
		if (report.isNegative())
			return null;
		
		ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
		RuleResponse response = new RuleResponse();
		Report reply;
		
		PropositionSet replySupport = report.getSupport();
		PropositionSet ruleSupport = new PropositionSet();
		
		if(this.getTerm() instanceof Closed) {
			try {
				ruleSupport = ruleSupport.add(this.getId());
				replySupport = replySupport.union(ruleSupport);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | 
					DuplicatePropositionException e) {
			}
			
			if(Runner.isNodeAssertedThroughForwardInf(this))
				reply = new Report(report.getSubstitutions(), replySupport, 
						true, InferenceTypes.FORWARD);
			else
				reply = new Report(report.getSubstitutions(), replySupport, 
						true, report.getInferenceType());
			
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
			for(Report r : knownInstances) {
				// Only positive reports for this rule node should be considered
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
					reply = report.combine(r);
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
	protected ArrayList<RuleResponse> applyRuleOnRui(RuleUseInfo tRui) {
		return null;
	}
	
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Vant");
	}
	
	@Override
	public NodeSet getDownConsqNodeSet() {
		return this.getDownNodeSet("Vconsq");
	}

	@Override
	protected RuisHandler createRuisHandler() {
		return null;
	}

	@Override
	protected byte getSIndexType() {
		return 0;
	}

}
