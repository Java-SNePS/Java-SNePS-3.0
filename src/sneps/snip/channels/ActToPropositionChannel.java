package sneps.snip.channels;

import sneps.network.Node;
import sneps.snip.matching.Substitutions;

public class ActToPropositionChannel extends Channel {
	public ActToPropositionChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions, String contextID, Node requester, Node reporter, boolean v) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v);
	}

}
