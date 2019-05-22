package sneps.snip.rules;

import java.util.ArrayList;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;

import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.term.Molecular;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;

/**
 * @className AndEntailment.java
 * 
 * @ClassDescription The AndEntailment is an inference rule that asserts the conjunction of all the nodes in its antecedent position to imply the conjunction of all the nodes in its consequent position.
 * When the rule node receives a request from a node in its consequent position, it sends requests to all its nodes in its antecedent position.
 * Generally, when a rule node has enough reports, it creates a reply report and broadcasts it to all consequent nodes.
 * In the case of the AndEntailment rule, the reply report is created and sent when all antecedent nodes send their respective reports.
 * 
 */
public class AndEntailment extends RuleNode {
	private static final long serialVersionUID = -8545987005610860977L;

	public AndEntailment(Molecular syn) {
		super(syn);
		antecedents = getDownAntNodeSet();
		processNodes(antecedents);
		consequents = getDownConsqNodeSet();
	}

	/**
	 * Creates the first RuleUseInfo from a given Report and stores it (if positive).
	 * Also checks if current number of positive Reports satisfies the rule.
	 * @param report
	 * @param signature
	 */
	@Override
	public ArrayList<RuleResponse> applyRuleHandler(Report report, Node signature) {
		if(report.isNegative())
			return null;
		
		//System.out.println("------------------------------");
		ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
		RuleResponse response = new RuleResponse();
		
		PropositionSet propSet = report.getSupport();
		FlagNodeSet fns = new FlagNodeSet();
		fns.insert(new FlagNode(signature, propSet, 1));
		RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns, 
				report.getInferenceType());
		
		if(antNodesWithoutVars.contains(signature)) {
			addConstantRui(rui);
			if (constantRUI.getPosCount() != antNodesWithoutVars.size())
				return null;
			
			if(ruisHandler == null) {
				response = applyRuleOnRui(constantRUI);
				if(response != null)
					responseList.add(response);
			}
			
			RuleUseInfoSet ruis = ((PTree) ruisHandler).getAllRootRuis();
			if (ruis != null) {
				RuleUseInfo combined;
				for (RuleUseInfo r : ruis) {
					combined = r.combine(constantRUI);
					if (combined != null) {
						response = applyRuleOnRui(combined);
						if(response != null)
							responseList.add(response);
					}
				}
			}
		}
		else {
			if (ruisHandler == null)
				ruisHandler = addRuiHandler();
			RuleUseInfoSet res = ruisHandler.insertRUI(rui);
			if (res == null)
				res = new RuleUseInfoSet();
			for (RuleUseInfo tRui : res) {
				if (tRui.getPosCount() != antNodesWithVars.size())
					return null;
				
				if(constantRUI == null) {
					response = applyRuleOnRui(tRui);
					if(response != null)
						responseList.add(response);
				}
				else {
					RuleUseInfo combined;
					combined = tRui.combine(constantRUI);
					if (combined != null) {
						response = applyRuleOnRui(combined);
						if(response != null)
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
	 * Creates a Report from a given RuleUseInfo to be broadcasted to outgoing channels
	 */
	@Override
	protected RuleResponse applyRuleOnRui(RuleUseInfo rui) {
		if (rui.getPosCount() < getAntSize())
			return null;
		
		PropositionSet replySupport = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				//System.out.println("HERE");
				//System.out.println(fn.getSupport());
				replySupport.union(fn.getSupport());
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}
		//System.out.println(replySupport);

		// TODO
		// Add rule node to replySupport
		
		Report reply = new Report(rui.getSubstitutions(), replySupport, true, 
				rui.getType());
		reportsToBeSent.add(reply);
		
		RuleResponse r = new RuleResponse();
		r.setReport(reply);
		//Set<Channel> forwardChannels = getOutgoingChannelsForReport(reply);
		//r.addAllChannels(forwardChannels);
		
		return r;
	}
	
	/**
	 * Naming convention used to retrieve Nodes in Down Antecedent position is "&ant";
	 * "&" for AndEntailment, "ant" for Antecedent
	 * @return NodeSet
	 */
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("&ant");
	}
	
	@Override
	public NodeSet getDownConsqNodeSet() {
		return this.getDownNodeSet("&consq");
	}

	@Override
	protected RuisHandler createRuisHandler() {
		PTree tree = new PTree();
		tree.buildTree(antNodesWithVars);
		return tree;
	}

	@Override
	protected byte getSIndexType() {
		return SIndex.PTREE;
	}

}
