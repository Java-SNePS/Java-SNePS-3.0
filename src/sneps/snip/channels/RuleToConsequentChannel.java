package sneps.snip.channels;

import sneps.network.Node;
import sneps.network.classes.setClasses.ReportSet;
import sneps.snip.InferenceTypes;
import sneps.snip.matching.Substitutions;

public class RuleToConsequentChannel extends Channel {

	public RuleToConsequentChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions,
			String contextID, Node requester, Node reporter, boolean v) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v);
	}

	public RuleToConsequentChannel clone() {
		RuleToConsequentChannel channel = new RuleToConsequentChannel(getSwitch().getSubstitutions(),
				getFilter().getSubstitutions(), getContextName(), getRequester(), getReporter(), isValveOpen());
		channel.setReportsBuffer(new ReportSet());
		return channel;
	}

}
