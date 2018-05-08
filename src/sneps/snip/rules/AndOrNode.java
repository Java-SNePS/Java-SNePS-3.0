package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Term;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.classes.FlagNode;

public class AndOrNode extends RuleNode {

	boolean sign = false;
	
	private int min, max, args;
	
	public int getAndOrMin() {
		return min;
	}

	public int getAndOrMax() {
		return max;
	}

	public int getAndOrArgs() {
		return args;
	}

	public AndOrNode(Term syn) {
		super(syn);
		NodeSet minNode = this.getDownNodeSet("min");
		min = Integer.parseInt(minNode.getNode(0).getIdentifier());
		NodeSet maxNode = this.getDownNodeSet("max");
		max = Integer.parseInt(maxNode.getNode(0).getIdentifier());
		NodeSet antNodes = this.getDownNodeSet("arg");
		args = antNodes.size();

		this.processNodes(antNodes);
	}
	
//	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
//		
//		if (tRui.getNegCount() == args - min)
//			sign = true;
//		else if (tRui.getPosCount() != max)
//			return;
//		
//		Set<Integer> nodesSentReports = new HashSet<Integer>();
//		for (FlagNode fn : tRui.getFlagNodeSet()) {
//			nodesSentReports.add(fn.getNode().getId());
//		}
//		
//		Support originSupports = this.getBasicSupport();
//		HashSet<Support> sup = new HashSet<Support>();
//		sup.add(originSupports);
//		Report forwardReport = new Report(tRui.getSub(), tRui.getSupport(sup), sign,contextID);
//		
//		for (Channel outChannel : outgoingChannels) {
//			if(!nodesSentReports.contains(outChannel.getRequester().getId()))
//			outChannel.addReport(forwardReport);
//		}
//		
//	}
	
	
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Xant");
	}

	
//	protected RuisHandler createRuisHandler(String contextName) {
//		Context contxt = (Context) Controller.getContextByName(contextName);
//		SIndex index = new SIndex(contextName, getSharedVarsNodes(antNodesWithVars), (byte) 0, getDominatingRules());
//		return this.addContextRUIS(contxt, index);
//	}

	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub
		
	}
	
}
