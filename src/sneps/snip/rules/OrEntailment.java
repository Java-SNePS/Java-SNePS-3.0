package sneps.snip.rules;

import java.util.ArrayList;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;

public class OrEntailment extends RuleNode {
	private static final long serialVersionUID = 1L;
	
	// Used for testing
	private ArrayList<Report> reportsToBeSent;

	public OrEntailment(Molecular syn) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		super(syn);
		
		// Initializing the antecedents
		antecedents = getDownAntNodeSet();
		processNodes(antecedents);
		
		// Initializing the consequents
		consequents = getDownNodeSet("Vconsq");
		
		reportsToBeSent = new ArrayList<Report>();
	}
	
	public void applyRuleHandler(Report report, Node node) {
		if(report.isPositive()) {
			Support replySupport = new Support();
			/*PropositionSet propSet = new PropositionSet();
			try {
				propSet.add(node.getId());
				propSet.add(this.getId());
			} catch (DuplicatePropositionException | NotAPropositionNodeException 
					| NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}
			try {
				replySupport.addJustificationBasedSupport(propSet);
			} catch (NodeNotFoundInPropSetException | NotAPropositionNodeException 
					| NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}*/
			
			Report reply = new Report(report.getSubstitutions(), replySupport, true);
			reportsToBeSent.add(reply);
		}
	}
	
	@Override
	protected RuleResponse applyRuleOnRui(RuleUseInfo tRui) {
		return null;
	}
	
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Vant");
	}

	@Override
	protected RuisHandler createRuisHandler() {
		return null;
	}

	@Override
	protected byte getSIndexType() {
		return 0;
	}
	
	public ArrayList<Report> getSentReports() {
		return reportsToBeSent;
	}

}
