package sneps.snip.channels;

import sneps.network.Node;
import sneps.snip.InferenceTypes;
import sneps.snip.matching.Substitutions;

public class RuleToConsequentChannel extends Channel {

	public RuleToConsequentChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions,
			String contextID, Node requester, Node reporter, boolean v, InferenceTypes inferenceType) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v, inferenceType);
	}

}
