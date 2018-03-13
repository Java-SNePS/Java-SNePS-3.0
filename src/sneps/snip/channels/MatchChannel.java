package sneps.snip.channels;

import sneps.network.Node;
import sneps.snip.matching.Substitutions;

public class MatchChannel extends Channel {
	
	public MatchChannel() {
		super();
	}
	public MatchChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions, int contextID, Node requester, Node reporter, boolean v) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v);
	}

}
