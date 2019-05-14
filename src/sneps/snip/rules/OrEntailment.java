package sneps.snip.rules;

import java.util.ArrayList;


import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.snip.Report;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;

public class OrEntailment extends NumericalEntailment {
	private static final long serialVersionUID = 1L;
	
	public OrEntailment(Molecular syn) {
		super(syn);
		antecedents = getDownAntNodeSet();
		consequents = getDownConsqNodeSet();
		
		reportsToBeSent = new ArrayList<Report>();
	}
	
	public ArrayList<RuleResponse> applyRuleHandler(Report report, Node signature) {
		ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
		RuleResponse response = new RuleResponse();
		PropositionSet replySupport = new PropositionSet();
		try {
			replySupport.union(report.getSupport());
		} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
			e.printStackTrace();
		}
		// TODO
		// Add rule node to replySupport
		Report reply = new Report(report.getSubstitutions(), replySupport, 
				true, report.getInferenceType());
		reportsToBeSent.add(reply);
		response.setReport(reply);
		//Set<Channel> outgoingChannels = getOutgoingChannelsForReport(reply);
		//response.addAllChannels(outgoingChannels);
		responseList.add(response);
		return responseList;
	}

	@Override
	protected Report applyRuleOnRui(RuleUseInfo tRui) {
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
