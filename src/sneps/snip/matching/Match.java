package sneps.snip.matching;

import java.util.LinkedList;

import sneps.network.Node;
import sneps.network.classes.setClasses.PropositionSet;

public class Match {
	/**
	 *This class initialize all the outputs of the match method in Matcher class
	 *target node -> is the node that match 
	 *filterSubs -> are the target substitutions list  
	 *switchSubs -> are the source substitutions list 
	 *justifications -> set of supports for path-based inference
	 *matchType -> is the type of match whether it is an exact match, reducible or expandable => 
	 *matchType =0 if the match is exact
	 *matchType =1 if reducible or negated reducible
	 *matchType =2 if expandable or negated expandable 
	 */
	public Substitutions switchSubs;
	public Substitutions filterSubs;
	public  Node targetnode;
	public int matchType;
	//public  PropositionSet justifications;

	public Match(Substitutions filterSubs, Substitutions switchSubs, Node targetnode, int matchType) {
		this.filterSubs = filterSubs;
		this.switchSubs = switchSubs;
		this.targetnode = targetnode;
		this.matchType = matchType;
		//this.justifications=justifications;
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

	/*public PropositionSet getJustifications() {
		return justifications;
	}

	public void setJustifications(PropositionSet justifications) {
		this.justifications = justifications;
	}*/
}
