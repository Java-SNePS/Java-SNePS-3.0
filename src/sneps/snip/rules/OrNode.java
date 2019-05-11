package sneps.snip.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.PropositionNode;
import sneps.network.classes.term.Term;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuleUseInfo;

public class OrNode extends RuleNode {

	private int ant, cq;

	public OrNode(Term syn) {
		super(syn);
		ant = getDownNodeSet("ant").size();
		cq = getDownNodeSet("cq").size();
	}

	public void applyRuleHandler(Report report, Node node) throws DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		if (report.isPositive()) {
			Support originSupports = this.getBasicSupport();
			Collection<PropositionSet> originSupportsSet = originSupports.getAssumptionBasedSupport().values();
			PropositionSet sup = new PropositionSet();
			sup.add(getId());
			Report reply = new Report(report.getSubstitutions(), sup, true, null);
			for (Channel outChannel : outgoingChannels)
				try {
					outChannel.testReportToSend(reply);
				} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("Vant");
	}

//	@Override
//	protected void applyRuleOnRui(RuleUseInfo tRui, String contextID) {
//		
//	}
//
//	@Override
//	protected RuisHandler createRuisHandler(String contextName) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	protected void sendRui(RuleUseInfo tRui, String contextID) {
		// TODO Auto-generated method stub

	}

}
