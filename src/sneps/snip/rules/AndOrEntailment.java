package sneps.snip.rules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
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

public class AndOrEntailment extends RuleNode {
	boolean sign = false;
	private int min, max, args;
	private int received=0;
	private static int pos=0;
	private static int neg=0;

	public int getAndOrMin() {
		return min;
	}

	public int getAndOrMax() {
		return max;
	}

	public int getAndOrArgs() {
		return args;
	}

	public void setAndOrMin(int min) {
		this.min = min;
	}

	public void setAndOrMax(int max) {
		this.max = max;
	}

	public void setAndOrArgs(int args) {
		this.args = args;
	}

	/**
	 * Constructor for the AndOr Entailment
	 * @param syn
	 */
	
	public AndOrEntailment(Molecular syn) {
		super(syn);
	}
	
	/**
	 * When a report is received, it checks whether it is true or false
	 * Then the positive or negative will be updated accordingly
	 * When there is enough args received to create a RUI, for the rule to check,
	 * A RUI will be created and apply the rule on this RUI
	 */
	public void applyRuleHandler(Report report, Node signature) {
		String contextID = report.getContextName();
		RuleUseInfo rui;
		if (report.isPositive()) {
			pos++;
		} else {
			neg++;
		}
		
		int rem = args-(pos+neg);
		if(rem<min && (min-pos)>rem) {
			Set<Support> propSet = report.getSupports();
			FlagNodeSet fns = new FlagNodeSet();
			fns.insert(new FlagNode(signature, propSet, 1));
			rui = new RuleUseInfo(report.getSubstitutions(),
					pos, neg, fns);
			applyRuleOnRui(rui, contextID);
		}
		
		
		
		if(pos+neg==args) {
		Set<Support> propSet = report.getSupports();
		FlagNodeSet fns = new FlagNodeSet();
		fns.insert(new FlagNode(signature, propSet, 1));
		rui = new RuleUseInfo(report.getSubstitutions(),
				pos, neg, fns);
		applyRuleOnRui(rui, contextID);
		}
	}
	
	
	/**
	 * Checks the condition for firing the rule.
	 * If the conditions are true, the sign is set to true
	 * Then a new report is created with the sign that was set.
	 * The report is broadcasted to the ants.
	 */
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		
		if(tRui.getPosCount()>=min&&tRui.getPosCount()<=max) {
			sign=true;
		}else if(tRui.getPosCount()>max||tRui.getPosCount()<min) {
			sign=false;
		}
		
		int rem = args-(tRui.getPosCount()+tRui.getNegCount());
		if(rem<min && (min-tRui.getPosCount())>rem) {
			sign=false;
		}
		
		
		Set<Integer> nodesSentReports = new HashSet<Integer>();
		for (FlagNode fn : tRui.getFlagNodeSet()) {
			nodesSentReports.add(fn.getNode().getId());
		}
		


		
		
		Substitutions sub = tRui.getSubstitutions();
		FlagNodeSet justification = new FlagNodeSet();
		justification.addAll(tRui.getFlagNodeSet());
		//PropositionSet supports = new PropositionSet();

		/*for(FlagNode fn : justification){
			try {
				supports = supports.union(fn.getSupports());
			} catch (NotAPropositionNodeException
					| NodeNotFoundInNetworkException e) {}
		}*/
		
		Iterator<FlagNode> fnIter = justification.iterator();
		Set<Support> supports = fnIter.next().getSupports();
		while (fnIter.hasNext()) {
			Set<Support> toBeCombined = fnIter.next().getSupports();
			supports = Support.combine(supports, toBeCombined);
		}

		/*try {
			supports = supports.union(tRui.getSupports());
		} catch (NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {}*/
		
		Set<Support> tRuiSupport = tRui.getCombinedSupports();
		supports = Support.combine(supports, tRuiSupport);

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
	

	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Xant");
	}

	/**
	 * Create the SIndex within the context
	 * @param ContextName
	 */
	protected RuisHandler createRuisHandler(String contextName) {
		SIndex index = new SIndex(contextName, getSharedVarsNodes(antNodesWithVars), (byte) 0);
		return this.addContextRUIS(contextName, index);
	}

	/**
	 * Getters of positive, negative, sign
	 * used in testing
	 */
	public static int getPos() {
		return pos;
	}

	public static int getNeg() {
		return neg;
	}
	
	public boolean isSign() {
		return sign;
	}
	
	/**
	 * Clear all the values
	 * Used in testing
	 */
	public void clrAll() {
		min=0;
		max=0;
		pos=0;
		neg=0;
	}
	
}
