package sneps.snip.matching;

import sneps.network.Node;
import sneps.network.classes.setClasses.PropositionSet;

public class Match {
	private Substitutions filterSubs; // whquestion atleastone free not bound
	// no filter on match channel
	// target (switch el heya variables to variables) <- source (filter el heya
	// variables to constants)
	private Substitutions switchSubs;
	private Node node;
	private int matchType;
	private PropositionSet support;

	public Match(Substitutions filterSubs, Substitutions switchSubs, Node n, int type) {
		this.filterSubs = filterSubs;
		this.switchSubs = switchSubs;
		this.node = n;
		this.matchType = type;
	}

	public Substitutions getFilterSubs() {
		return filterSubs;
	}

	public void setFilterSubs(Substitutions filterSub) {
		this.filterSubs = filterSub;
	}

	public Substitutions getSwitchSubs() {
		return switchSubs;
	}

	public void setSwitchSubs(Substitutions switchSubs) {
		this.switchSubs = switchSubs;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}
}
