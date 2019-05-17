package sneps.snip.rules;

import java.util.Set;

import sneps.network.RuleNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Molecular;
import sneps.snip.Report;
import sneps.snip.channels.Channel;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;

public class DoIfNode extends RuleNode {
	private static final long serialVersionUID = -262476672166406490L;

	public DoIfNode(Molecular syn) {
		super(syn);
	}

	@Override
	public NodeSet getDownAntNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte getSIndexType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected RuleResponse applyRuleOnRui(RuleUseInfo tRui) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet getDownConsqNodeSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RuisHandler createRuisHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Set<Channel> getOutgoingChannelsForReport(Report r) {
		// TODO Auto-generated method stub
		return null;
	}

}
