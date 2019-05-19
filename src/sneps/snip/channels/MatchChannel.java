package sneps.snip.channels;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.classes.setClasses.ReportSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Filter;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.Switch;
import sneps.snip.matching.Substitutions;

public class MatchChannel extends Channel {
	private int matchType;

	public MatchChannel() {
		super();
	}

	public MatchChannel(Substitutions switchSubstitution, Substitutions filterSubstitutions, String contextID,
			Node requester, Node reporter, boolean v, int matchType) {
		super(switchSubstitution, filterSubstitutions, contextID, requester, reporter, v);
		this.setMatchType(matchType);
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	public MatchChannel clone() {
		MatchChannel channel = new MatchChannel(getSwitch().getSubstitutions(), getFilter().getSubstitutions(),
				getContextName(), getRequester(), getReporter(), isValveOpen(), getMatchType());
		channel.setReportsBuffer(new ReportSet());
		return channel;
	}

	public boolean testReportToSend(Report report) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		int channelMatchType = getMatchType();
		boolean toBeSentFlag = (channelMatchType == 0) || (channelMatchType == 1 && report.isPositive())
				|| (channelMatchType == 2 && report.isNegative());
		return toBeSentFlag && super.testReportToSend(report);
	}
}
