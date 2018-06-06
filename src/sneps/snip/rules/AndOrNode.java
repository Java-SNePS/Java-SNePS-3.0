package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;

public class AndOrNode extends RuleNode {

	boolean sign = false;
	
	private int min, max, args;
	private int received=0;
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
	
	public AndOrNode(Term syn) {
		super(syn);
	}

	/**
	 * Constructor for the AndOr Entailment
	 * @param sym
	 * @param syn
	 */
	
	public AndOrNode(Semantic sym, Term syn) {
		super(sym, syn);
	}
	
	public void applyRuleHandler(Report report, Node signature) {
		super.applyRuleHandler(report, signature);
		received++;
	}
	
	
	/**
	 * Checks the condition for firing the rule.
	 * If the conditions are true, the sign is set to true
	 * Then a new report is created with the sign that was set.
	 * The report is broadcasted to the ants.
	 */
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		
		if (tRui.getNegCount() == args - min)
			sign = true;
		else if (tRui.getPosCount() != max)
			return;
		
		
		int rem = args-(tRui.getPosCount()+tRui.getNegCount());
		if(rem<min && (min-tRui.getPosCount())>rem) {
			sign=false;
		}
		
		
		Set<Integer> nodesSentReports = new HashSet<Integer>();
		for (FlagNode fn : tRui.getFlagNodeSet()) {
			nodesSentReports.add(fn.getNode().getId());
		}
		
		FlagNodeSet justification = contextRuisSet.getByContext(contextID).getPositiveNodes();
		NodeSet temp = new NodeSet();
		temp.addNode(this);
		FlagNode fn = new FlagNode(this, temp, 1);
		justification.insert(fn);

		Report forwardReport = new Report(tRui.getSub(), justification, true, contextID);
		
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
		Context contxt = (Context) Controller.getContextByName(contextName);
		SIndex index = new SIndex(contextName, getSharedVarsNodes(antNodesWithVars), (byte) 0);
		return this.addContextRUIS(contxt, index);
	}
	
}
