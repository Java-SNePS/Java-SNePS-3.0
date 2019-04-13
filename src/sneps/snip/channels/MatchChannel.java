package sneps.snip.channels;

import sneps.network.Node;
import sneps.snip.InferenceTypes;
import sneps.snip.matching.Substitutions;

public class MatchChannel extends Channel {
	private int matchType;

	public MatchChannel() {
		super();
	}

	public MatchChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions, String contextID,
			Node requester, Node reporter, boolean v, InferenceTypes inferenceType, int matchType) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v, inferenceType);
		this.setMatchType(matchType);
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

}
