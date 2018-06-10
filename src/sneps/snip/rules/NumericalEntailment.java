package sneps.snip.rules;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
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
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;
/**
 * @author Amgad Ashraf
 */
public class NumericalEntailment extends RuleNode {
	private static final long serialVersionUID = 3546852401118194013L;
	private int i;

	public NumericalEntailment(Term syn) {
		super(syn);
	}
	public NumericalEntailment(Semantic sym, Term syn) {
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
		int curPos = contextRuisSet.getByContext(contxt).getPositiveNodes().size();
		int n = antNodesWithoutVars.size()+antNodesWithVars.size();
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
	 * Inserts given RuleUseInfo into the appropriate SIndex and updates the corresponding SIndex
	 * @param rui
	 * @param contxt
	 * @param signature
	 */
	public void addNotSentRui(RuleUseInfo rui, String contxt, Node signature){
		SIndex set = (SIndex) contextRuisSet.getByContext(contxt);
		if (set == null) 
			set = new SIndex(contxt, getSharedVarsNodes(antNodesWithVars), (byte) 0);
		set.insertRUI(rui);
		if(!set.getPositiveNodes().contains(signature))
			set.getPositiveNodes().addNode(signature);
		contextRuisSet.addHandlerSet(contxt, set);
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
	 * Creates an appropriate SIndex as a RuisHandler and inserts it into ContextRuisSet by Context
	 * @param contextName
	 * @return
	 */
	@Override
	public RuisHandler createRuisHandler(String contextName) {
		SIndex index = new SIndex(contextName, getSharedVarsNodes(antNodesWithVars), (byte) 0);
		return this.addContextRUIS(contextName, index);
	}
	@Override
	public NodeSet getDownAntNodeSet(){
		return this.getDownNodeSet("iant");
	}

	public int getI() {
		return i;
	}
	public void setI(int newI){
		i = newI;
	}
}
