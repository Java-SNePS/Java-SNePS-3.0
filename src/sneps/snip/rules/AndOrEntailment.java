package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
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

	@Override
	protected Report applyRuleOnRui(RuleUseInfo tRui) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeSet getDownAntNodeSet() {
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
	protected byte getSIndexType() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}