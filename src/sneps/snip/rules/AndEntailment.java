package sneps.snip.rules;

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
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;

/**
 * @className AndEntailment.java
 * 
 * @ClassDescription The AndEntailment is an inference rule that asserts the conjunction of all the nodes in its antecedent position to imply the conjunction of all the nodes in its consequent position.
 * When the rule node receives a request from a node in its consequent position, it sends requests to all its nodes in its antecedent position.
 * Generally, when a rule node has enough reports, it creates a reply report and broadcasts it to all consequent nodes.
 * In the case of the AndEntailment rule, the reply report is created and sent when all antecedent nodes sent their respective reports.
 * 
 * @author Amgad Ashraf
 * @version 3.00 31/5/2018
 */
public class AndEntailment extends RuleNode {
	private static final long serialVersionUID = -8545987005610860977L;

	/**
	 * Constructor for the AndEntailment rule node
	 * @param syn
	 */
	public AndEntailment(Molecular syn) {
		super(syn);
	}

	/**
	 * Creates the first RuleUseInfo from a given Report and stores it(if positive)
	 * Also checks if current number of positive Reports satisfy rule
	 * @param report
	 * @param signature
	 */
	@Override
	public void applyRuleHandler(Report report, Node signature) {
		String contxt = report.getContextName();
		if (report.isPositive()) {
			PropositionSet propSet = report.getSupports();
			FlagNodeSet fns = new FlagNodeSet();
			fns.insert(new FlagNode(signature, propSet, 1));
			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(),
					1, 0, fns);
			addNotSentRui(rui, contxt, signature);
		}
		if (contextRuisSet.getByContext(contxt).getPositiveNodes().size() >= getAntSize())
			sendSavedRUIs(report.getContextName());
	}

	/**
	 * Creates a Report from a given RuleUseInfo to be broadcasted to outgoing channels
	 * Also checks report supports and modifies accordingly
	 * @param Rui
	 * @param contextID 
	 */
	@Override
	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
		if (Rui.getPosCount() >= getAntSize()){
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
	 * Creates an appropriate PTree as a RuisHandler, builds it and inserts it into ContextRuisSet by Context
	 * @param context
	 * @return
	 */
	@Override
	public RuisHandler createRuisHandler(String context) {
		PTree tree = new PTree();
		NodeSet ants = antNodesWithoutVars;
		ants.addAll(antNodesWithVars);
		tree.buildTree(ants);
		this.addContextRUIS(context, tree);
		return tree;
	}

	/**
	 * Inserts given RuleUseInfo into the appropriate PTree and updates the corresponding PTree
	 * @param rui
	 * @param contxt
	 * @param signature
	 */
	public void addNotSentRui(RuleUseInfo rui, String contxt, Node signature){
		PTree tree = (PTree) contextRuisSet.getByContext(contxt);
		if (tree == null)
			tree = (PTree) createRuisHandler(contxt);
		tree.insertRUI(rui);
		if(!tree.getPositiveNodes().contains(signature))
			tree.getPositiveNodes().addNode(signature);
		contextRuisSet.addHandlerSet(contxt, tree);
	}
	/**
	 * Prepares the appropriate PTree and all its root RuleUseInfo for broadcasting
	 * @param contextID
	 */
	private void sendSavedRUIs(String contextID) {
		RuleUseInfo addedConstant = getConstantRUI(contextID);
		if (addedConstant == null && antNodesWithoutVars.size() != 0)
			return;

		if( (addedConstant != null) &&(antNodesWithoutVars.size() != addedConstant.getPosCount()))
			return;

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
	 * Naming convention used to retrieve Nodes in Down Antecedent position is "&ant";
	 * "&" for AndEntailment, "ant" for Antecedent
	 * @return
	 */
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("&ant");//ants for & name convention
	}

}
