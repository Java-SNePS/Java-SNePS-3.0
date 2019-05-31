package sneps.snip.rules;

import java.util.ArrayList;

import java.util.Set;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.setClasses.FlagNodeSet;
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
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;

/**
 * @className NumericalEntailment.java
 * 
 * @ClassDescription The Numerical-Entailment is a rule node that asserts the conjunction of at least i nodes in its antecedent position to imply the conjunction of all the nodes in its consequent position.
 * When the rule node receives a request from a node in its consequent position, it sends requests to all its nodes in antecedent positions.
 * Generally, when a rule node has enough reports, it creates a reply report and broadcasts it to all consequent nodes.
 * In the case of the Numerical-Entailment rule, the reply report is created and sent when a minimum of i antecedent nodes send their respective positive reports.
 */
public class NumericalEntailment extends RuleNode {
	private static final long serialVersionUID = 3546852401118194013L;
	
	private int i;
	
	public NumericalEntailment(Molecular syn) {
		super(syn);
		// Initializing i
		NodeSet max = getDownNodeSet("i");
		if(max != null)
			i = Integer.parseInt(max.getNode(0).getIdentifier());
		
		// Initializing the antecedents
		antecedents = getDownAntNodeSet();
		processNodes(antecedents);
		
		// Initializing the consequents
		consequents = getDownConsqNodeSet();
	}

	/**
	 * Creates the first RuleUseInfo from a given Report and stores it (if positive), 
	 * also checks if current number of positive Reports satisfies the rule.
	 * @param report
	 * @param signature
	 */
	@Override
	public ArrayList<RuleResponse> applyRuleHandler(Report report, Node signature) {
		if(report.isNegative())
			return null;
		
		ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
		ArrayList<RuleResponse> response = new ArrayList<RuleResponse>();
		
		PropositionSet propSet = report.getSupport();
		FlagNodeSet fns = new FlagNodeSet();
		fns.insert(new FlagNode(signature, propSet, 1));
		RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns, 
				report.getInferenceType());
		
		if(antNodesWithoutVars.contains(signature)) {
			addConstantRui(rui);
			if (ruisHandler == null) {
				response = applyRuleOnRui(constantRUI);
				if(response != null)
					responseList.addAll(response);
			}
			else {
				RuleUseInfoSet combined  = ruisHandler.combineConstantRUI(constantRUI);
				for (RuleUseInfo tRui : combined) {
					response = applyRuleOnRui(tRui);
					if(response != null)
						responseList.addAll(response);
				}
			}
		}
		else {
			// Inserting the RuleUseInfo into the RuleNode's RuisHandler:
			// SIndex in case there are shared variables between all the antecedents, or 
			// RUISet in case there are no shared variables
			if(ruisHandler == null)
				ruisHandler = addRuiHandler();
			
			// The RUI created for the given report is inserted to the RuisHandler
			RuleUseInfoSet res = ruisHandler.insertRUI(rui);
			
			if(constantRUI != null) {
				RuleUseInfo combined;
				for (RuleUseInfo tRui : res) {
					combined = tRui.combine(constantRUI);
					if(combined != null) {
						response = applyRuleOnRui(combined);
						if(response != null)
							responseList.addAll(response);
					}
				}
			}
			else {
				for (RuleUseInfo tRui : res) {
					response = applyRuleOnRui(tRui);
					if(response != null)
						responseList.addAll(response);
				}
			}
		}
		
		if(responseList.isEmpty())
			return null;
	
		return responseList;
	}

	protected ArrayList<RuleResponse> applyRuleOnRui(RuleUseInfo rui) {
		if(rui.getPosCount() < i)
			return null;
		
		ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
		RuleResponse response = new RuleResponse();
		Report reply;
		
		PropositionSet replySupport = new PropositionSet();
		PropositionSet ruleSupport = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				replySupport = replySupport.union(fn.getSupport());
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}
		
		if(this.getTerm() instanceof Closed) {
			try {
				ruleSupport = ruleSupport.add(this.getId());
				replySupport = replySupport.union(ruleSupport);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | 
					DuplicatePropositionException e) {
			}
			
			if(Runner.isNodeAssertedThroughForwardInf(this))
				reply = new Report(rui.getSubstitutions(), replySupport, true, 
						InferenceTypes.FORWARD);
			else
				reply = new Report(rui.getSubstitutions(), replySupport, true, 
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
					true, rui.getType());
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
	
	/**
	 * Naming convention used to retrieve Nodes in Down Antecedent position is "iant"
	 * "i" for NumericalEntailment, "ant" for Antecedent
	 * @return
	 */
	@Override
	public NodeSet getDownAntNodeSet(){
		return this.getDownNodeSet("iant");
	}
	
	@Override
	public NodeSet getDownConsqNodeSet() {
		return this.getDownNodeSet("iconsq");
	}

	@Override
	protected RuisHandler createRuisHandler() {
		return new RuleUseInfoSet(false);
	}

	@Override
	protected byte getSIndexType() {
		return SIndex.RUIS;
	}
	
	/**
	 * Getter for i
	 * @return
	 */
	public int getI() {
		return i;
	}
	
	/**
	 * Setter for i
	 * @param newI
	 */
	public void setI(int newI) {
		i = newI;
	}

}
