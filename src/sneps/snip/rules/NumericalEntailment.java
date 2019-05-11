package sneps.snip.rules;

import java.util.ArrayList;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.term.Molecular;
import sneps.snebr.Support;
import sneps.snip.Report;
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
 * Generally, when a rule node has enough reports, it creates a reply report and broadcasts it to all requesting consequent nodes.
 * In the case of the Numerical-Entailment rule, the reply report is created and sent when a minimum of i antecedent nodes sent their respective positive reports.
 * 
 * @author Amgad Ashraf
 * @version 3.00 31/5/2018
 */
public class NumericalEntailment extends RuleNode {
	private static final long serialVersionUID = 3546852401118194013L;
	
	private int i;
	
	// Used for testing
	private ArrayList<Report> reportsToBeSent;

	public NumericalEntailment(Molecular syn) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		super(syn);
		// Initializing i
		NodeSet max = getDownNodeSet("i");
		if(max != null)
			i = Integer.parseInt(max.getNode(0).getIdentifier());
		
		// Initializing the antecedents
		//antecedents = getDownAntNodeSet();
		//processNodes(antecedents);
		
		// Initializing the consequents
		consequents = getDownNodeSet("iconsq");
		
		reportsToBeSent = new ArrayList<Report>();
	}

	/**
	 * Creates the first RuleUseInfo from a given Report and stores it (if positive)
	 * Also checks if current number of positive Reports satisfies the rule
	 * @param report
	 * @param signature
	 */
	@Override
	public void applyRuleHandler(Report report, Node signature) {
		processNodes(antecedents);
		System.out.println("---------------");
		if (report.isPositive()) {
			//System.out.println("CREATING POSITIVE RUI");
			Support propSet = report.getSupport();
			FlagNodeSet fns = new FlagNodeSet();
			fns.insert(new FlagNode(signature, propSet, 1));
			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns);
			//System.out.println(rui);
			
			// Inserting the RuleUseInfo into the RuleNode's RuisHandler:
			// SIndex in case there are shared variables between the antecedents, or 
			// RUISet in case there are no shared variables
			if(ruisHandler == null)
				ruisHandler = addRuiHandler();
			
			RuleResponse response = new RuleResponse();
			response.setConsequents(consequents);
			if(antNodesWithoutVars.contains(signature)) {
				addConstantRui(rui);
				//System.out.println(constantRUI);
				if (ruisHandler.isEmpty())
					applyRuleOnRui(constantRUI);
				
				// What if ruisHandler is not empty? I need to combine constantRUI with
				// all the RUIs stored or what?
			}
			else {
				// The RUI created for the given report is inserted to the RuisHandler
				RuleUseInfoSet res = ruisHandler.insertRUI(rui);
				//System.out.println(res);
				
				if(constantRUI != null) {
					RuleUseInfo combined;
					for (RuleUseInfo tRui : res) {
						combined = tRui.combine(constantRUI);
						if(combined != null) {
							applyRuleOnRui(combined);
						}
					}
				}
				else {
					for (RuleUseInfo tRui : res) {
						applyRuleOnRui(tRui);
					}
				}
			}
		}
		
	}
	
	protected RuleResponse applyRuleOnRui(RuleUseInfo rui) {
		if(rui.getPosCount() < i)
			return null;
		
		Support replySupport = new Support();
		/*PropositionSet propSet = new PropositionSet();
		for(FlagNode fn : rui.getFlagNodeSet())
			try {
				propSet.add(fn.getNode().getId());
			} catch (DuplicatePropositionException | NotAPropositionNodeException 
					| NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			}
		
		try {
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
		
		Report reply = new Report(rui.getSubstitutions(), replySupport, true);
		reportsToBeSent.add(reply);
		
		return new RuleResponse();
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
	
	public ArrayList<Report> getReplies() {
		return reportsToBeSent;
	}

}
