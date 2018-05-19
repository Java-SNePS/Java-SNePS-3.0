package sneps.snip.rules;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.setClasses.VarNodeSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;
/**
 * @author Amgad Ashraf
 */
public class AndEntailment extends RuleNode {
	private static final long serialVersionUID = -8545987005610860977L;

	/**
	 * Constructor for the AndEntailment rule node
	 * @param syn
	 */
	public AndEntailment(Term syn) {
		super(syn);
	}
	/**
	 * Constructor for the AndEntailment rule node
	 * @param sym
	 * @param syn
	 */
	public AndEntailment(Semantic sym, Term syn) {
		super(sym, syn);
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
	 * @param Rui
	 * @param contextID 
	 */
	@Override
	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
		if (Rui.getPosCount() >= getAntSize()){
			Substitutions sub = Rui.getSub();

			FlagNodeSet justification = new FlagNodeSet();
			justification.addAll(Rui.getFlagNodeSet());

			PropositionSet supports = new PropositionSet();
			for(FlagNode fn : justification){
				try {
					supports = supports.union(fn.getSupports());
				} catch (NotAPropositionNodeException
						| NodeNotFoundInNetworkException e) {
				}
			}

			if(this.getTerm() instanceof Closed){
				//add this and send
				try {
					supports = supports.union(Rui.getSupports());
				} catch (NotAPropositionNodeException
						| NodeNotFoundInNetworkException e) {
				}

				Report reply = new Report(sub, supports, true, contextID);
				sendReportToConsequents(reply);
			}

			if(this.getTerm() instanceof Open){
				//knownInstances check this.free vars - > bound
				VarNodeSet freeVars = ((Open)this.getTerm()).getFreeVariables();
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
						Substitutions ruiSub = Rui.getSubstitutions();

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
						}
					}
				}	

			}
		}
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

		if (antNodesWithoutVars.size() != addedConstant.getPosCount())
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
	 * Creates an appropriate PTree as a RuisHandler, builds it and inserts it into ContextRuisSet by Context
	 * @param context
	 * @return
	 */
	@Override
	public RuisHandler createRuisHandler(String context) {
		Context contxt = (Context) Controller.getContextByName(context);
		PTree tree = new PTree(context);
		NodeSet ants = antNodesWithoutVars;
		ants.addAll(antNodesWithVars);
		tree.buildTree(ants);
		return this.addContextRUIS(contxt, tree);
	}
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("&ant");//ants for & name convention
	}

}
