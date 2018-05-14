package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.PropositionNode;
import sneps.network.classes.term.Term;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

public class OrNode extends RuleNode {

	private int ant,cq;
	

	/**
	 *Constructor for the Or Entailment
	 * @param syn
	 */
	public OrNode(Term syn) {
		super(syn);
		ant = getDownNodeSet("ant").size();
		cq = getDownNodeSet("cq").size();
	}

	
	/**
	 * Constructor for the Or Entailment
	 * @param sym
	 * @param syn
	 */
	public OrNode(Semantic sym, Term syn) {
		super(sym, syn);
		ant = getDownNodeSet("ant").size();
		cq = getDownNodeSet("cq").size();
	}
	
	/**
	 * Checks if the report received is true
	 * If yes, the report is sent to the ants with the true report
	 */
	public void applyRuleHandler(Report report, Node node) {
		
		if(report.isPositive()) {
			
			FlagNodeSet justification = contextRuisSet.getByContext(report.getContextName()).getPositiveNodes();
			NodeSet temp = new NodeSet();
			temp.addNode(this);
			FlagNode fn = new FlagNode(this, temp, 1);
			justification.insert(fn);

			Report reply = new Report(report.getSubstitutions(), justification, true, report.getContextName());
			
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(reply);
			
		}
		
}
	


	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Vant");
	}

	@Override
	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
		
	}

	@Override
	protected RuisHandler createRuisHandler(String contextName) {
		// TODO Auto-generated method stub
		return null;
	}

}
