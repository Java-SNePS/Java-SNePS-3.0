package sneps.snip.rules;

import java.util.Collection;

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
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;
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

	public NumericalEntailment(Molecular syn) {
		super(syn);
		// Initializing i
		NodeSet max = getDownNodeSet("i");
		i = Integer.parseInt(max.getNode(0).getIdentifier());
		
		// Initializing the antecedents
		antecedents = getDownAntNodeSet();
		processNodes(antecedents);
	}

	/**
	 * Creates the first RuleUseInfo from a given Report and stores it (if positive)
	 * Also checks if current number of positive Reports satisfy rule
	 * @param report
	 * @param signature
	 */
	@Override
	public void applyRuleHandler(Report report, Node signature) {
		if (report.isPositive()) {
			Collection<PropositionSet> propSet = report.getSupports();
			FlagNodeSet fns = new FlagNodeSet();
			fns.insert(new FlagNode(signature, propSet, 1));
			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns);
			
			// Inserting the RuleUseInfo into the RuleNode's RuisHandler:
			// SIndex in case there shared variables between the antecedents, or 
			// RUISet in case there are no shared variables
			if(ruisHandler == null){
				ruisHandler = addRuiHandler();
			}
			
			ruisHandler.insertRUI(rui);
			if(!ruisHandler.getPositiveNodes().contains(signature))
				ruisHandler.getPositiveNodes().addNode(signature);
		}
		
		int curPos = contextRuisSet.getByContext(contxt).getPositiveNodes().size();
		int n = antNodesWithoutVars.size()+antNodesWithVars.size();
		//The second cond will be removed
		if ((curPos >= i) && ((curPos < n-i+1) || (curPos < n-1) ) )
			sendSavedRUIs(report.getContextName());
	}

	/**
	 * Creates a Report from a given RuleUseInfo to be broadcasted to outgoing channels
	 * @param rui
	 * @param contextID 
	 */
	@Override
	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
		if (Rui.getPosCount() >= i){
			Substitutions sub = Rui.getSubstitutions();
			FlagNodeSet justification = new FlagNodeSet();
			justification.addAll(Rui.getFlagNodeSet());
			PropositionSet supports = new PropositionSet();

			for(FlagNode fn : justification){
				try {
					supports = supports.union(fn.getSupports());
				} catch (NotAPropositionNodeException
						| NodeNotFoundInNetworkException e) {}
			}

			try {
				supports = supports.union(Rui.getSupports());
			} catch (NotAPropositionNodeException
					| NodeNotFoundInNetworkException e) {}

			if(this.getTerm() instanceof Open){
				//knownInstances check this.free vars - > bound
				VarNodeSet freeVars = ((Open)this.getTerm()).getFreeVariables();
				Substitutions ruiSub = Rui.getSubstitutions();
				boolean allBound = true;

				for(Report report : knownInstances){
					//Bound to same thing(if bound)
					for(VariableNode var : freeVars){
						if(!report.getSubstitutions().isBound(var)){
							allBound = false;
							break;
						}
					}
					if(allBound){//if yes
						Substitutions instanceSub = report.getSubstitutions();

						for(int i = 0; i < ruiSub.cardinality(); i++){
							Binding ruiBind = ruiSub.getBinding(i);//if rui also bound
							Binding instanceBind = instanceSub.
									getBindingByVariable(ruiBind.getVariable());
							if( !((instanceBind != null) &&
									(instanceBind.isEqual(ruiBind))) ){
								allBound = false;
								break;
							}
						}
						if(allBound){
							//combine known with rui
							Substitutions newSub = new LinearSubstitutions();
							newSub.insert(instanceSub);
							newSub.insert(ruiSub);

							//add to new support and send
							Report reply = new Report(newSub, supports, true, contextID);
							sendReportToConsequents(reply);
							return;
						}
					}
				}
			}

			Report reply = new Report(sub, supports, true, contextID);
			sendReportToConsequents(reply);
		}
	}

	/**
	 * Prepares the appropriate SIndex and all its root RuleUseInfo for broadcasting  
	 * @param contextID
	 */
	private void sendSavedRUIs(String contextID) {
		RuleUseInfo addedConstant = getConstantRUI(contextID);
		if (addedConstant == null && antNodesWithoutVars.size() != 0)
			return;

		if (antNodesWithoutVars.size() != addedConstant.getPosCount())
			return;

		//Should be a RuisHandler in general, not a PTree
		RuleUseInfoSet ruis = ((PTree)contextRuisSet.getByContext(contextID)).getAllRootRuis();
		if (ruis == null) {
			applyRuleOnRui(addedConstant, contextID);
			return;
		}

		RuleUseInfo combined;
		for (RuleUseInfo info : ruis) {
			combined = info.combine(addedConstant);
			if (combined != null)
				applyRuleOnRui(combined, contextID);
		}
	}

	/**
	 * Naming convention used to retrieve Nodes in Down Antecedent position is "iant";
	 * "i" for NumericalEntailment, "ant" for Antecedent
	 * @return
	 */
	@Override
	public NodeSet getDownAntNodeSet(){
		return this.getDownNodeSet("&ant");
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
	public void setI(int newI){
		i = newI;
	}

}
