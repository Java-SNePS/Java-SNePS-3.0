package sneps.snip.channels;

import sneps.network.Node;
import sneps.network.classes.setClasses.ReportSet;
import sneps.snip.InferenceTypes;
import sneps.snip.matching.Substitutions;

public class AntecedentToRuleChannel extends Channel {

	public AntecedentToRuleChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions,
			String contextID, Node requester, Node reporter, boolean v) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v);
	}

	public AntecedentToRuleChannel clone() {
		AntecedentToRuleChannel channel = new AntecedentToRuleChannel(getSwitch().getSubstitutions(),
				getFilter().getSubstitutions(), getContextName(), getRequester(), getReporter(), isValveOpen());
		channel.setReportsBuffer(new ReportSet());
		return channel;
	}
}
