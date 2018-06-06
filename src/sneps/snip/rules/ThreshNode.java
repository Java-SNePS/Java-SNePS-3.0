package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
import sneps.network.PropositionNode;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.setClasses.VarNodeSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Substitutions;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;

public class ThreshNode extends RuleNode {

	boolean sign = false;
	
	private int min, max, args;
	
	public int getThreshMin() {
		return min;
	}

	public int getThreshMax() {
		return max;
	}

	public int getThreshArgs() {
		return args;
	}
	
	public void setThreshMin(int min) {
		this.min = min;
	}

	public void setThreshMax(int max) {
		this.max = max;
	}

	public void setThreshArgs(int args) {
		this.args = args;
	}


	/**
	 * Constructor for the Thresh Entailment
	 * @param syn
	 */
	public ThreshNode(Term syn) {
		super(syn);
	}

	/**
	 * Constructor for the Thresh Entailment
	 * @param sym
	 * @param syn
	 */
	public ThreshNode(Semantic sym, Term syn) {
		super(sym, syn);
	}
	
	public void applyRuleHandler(Report report, Node signature) {
		super.applyRuleHandler(report, signature);
	}
	
	
	/**
	 * Checks the condition for firing the rule.
	 * If the conditions are true, the sign is set to true
	 * Then a new report is created with the sign that was set.
	 * The report is broadcasted to the ants.
	 */
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		
		if (tRui.getPosCount() == min
				&& tRui.getNegCount() == args - max - 1)
			sign = true;
		else if (tRui.getPosCount() != min - 1 || tRui.getNegCount() != args - max)
			sign = false;
		
		int rem = args-(tRui.getPosCount()+tRui.getNegCount());
		if(tRui.getPosCount()>min && tRui.getPosCount()<max && max-tRui.getPosCount()>rem) {
			sign=false;
		}
		
		Set<Integer> nodesSentReports = new HashSet<Integer>();
		for (FlagNode fn : tRui.getFlagNodeSet()) {
			nodesSentReports.add(fn.getNode().getId());
		}

		


		
		
		
		
		
		
		
		Substitutions sub = tRui.getSubstitutions();
		FlagNodeSet justification = new FlagNodeSet();
		justification.addAll(tRui.getFlagNodeSet());
		PropositionSet supports = new PropositionSet();

		for(FlagNode fn : justification){
			try {
				supports = supports.union(fn.getSupports());
			} catch (NotAPropositionNodeException
					| NodeNotFoundInNetworkException e) {}
		}

		try {
			supports = supports.union(tRui.getSupports());
		} catch (NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {}

		if(this.getTerm() instanceof Open){
			//knownInstances check this.free vars - > bound
			VarNodeSet freeVars = ((Open)this.getTerm()).getFreeVariables();
			Substitutions ruiSub = tRui.getSubstitutions();
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

					}
				}
			}
		}
		
		
		Report forwardReport = new Report(sub, supports, sign, contextID);
		
		for (Channel outChannel : outgoingChannels) {
			if(!nodesSentReports.contains(outChannel.getRequester().getId()))
			outChannel.addReport(forwardReport);
		}
		
	}
	
	/**
	 * Create the SIndex within the context
	 * @param ContextName
	 */
	protected RuisHandler createRuisHandler(String contextName) {
		Context contxt = (Context) Controller.getContextByName(contextName);
		SIndex index = new SIndex(contextName, getSharedVarsNodes(antNodesWithVars), (byte) 0);
		return this.addContextRUIS(contxt, index);
	}

	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Tant");
	}

}
