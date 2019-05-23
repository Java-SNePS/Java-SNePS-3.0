package sneps.snip.matching;

import sneps.network.Node;
import sneps.network.classes.setClasses.PropositionSet;

public class Match {
	private Substitutions filterSubs; // whquestion atleastone free not bound
	// no filter on match channel
	// target (switch el heya variables to variables) <- source (filter el heya variables to constants)
	private Substitutions switchSubs;
	private Node targetnode;
	private int matchType;
	private PropositionSet justifications;

	public Match(Substitutions filterSubs, Substitutions switchSubs, Node targetnode, int type,PropositionSet justifications) {
		this.filterSubs = filterSubs;
		this.switchSubs = switchSubs;
		this.targetnode = targetnode;
		this.matchType = type;
		this.justifications=justifications;
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
		return targetnode;
	}

	public void setNode(Node node) {
		this.targetnode = node;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	public PropositionSet getJustifications() {
		return justifications;
	}

	public void setJustifications(PropositionSet justifications) {
		this.justifications = justifications;
	}
}
