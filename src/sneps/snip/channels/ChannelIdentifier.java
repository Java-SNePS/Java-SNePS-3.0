package sneps.snip.channels;

import sneps.snip.matching.Substitutions;

public class ChannelIdentifier {
	int requesterId;
	int reporterId;
	String contextName;
	Substitutions filterSubstitutions;

	public ChannelIdentifier(int rqId, int rpId, String cName, Substitutions fSubs) {
		requesterId = rqId;
		reporterId = rpId;
		contextName = cName;
		filterSubstitutions = fSubs;
	}

	@Override
	public boolean equals(Object obj) {
		ChannelIdentifier channelId;
		if (obj instanceof ChannelIdentifier) {
			channelId = (ChannelIdentifier) obj;
			boolean filterCheck = filterSubstitutions.equals(channelId.getFilterSubstitutions());
			boolean requesterCheck = getRequesterId() == channelId.getRequesterId();
			boolean reporterCheck = getReporterId() == channelId.getReporterId();
			boolean contextCheck = getContextName() == channelId.getContextName();
			return filterCheck && reporterCheck && requesterCheck && contextCheck;
		}
		return false;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public int getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(int requesterId) {
		this.requesterId = requesterId;
	}

	public int getReporterId() {
		return reporterId;
	}

	public void setReporterId(int reporterId) {
		this.reporterId = reporterId;
	}

	public Substitutions getFilterSubstitutions() {
		return filterSubstitutions;
	}

	public void setFilterSubstitutions(Substitutions filterSubstitutions) {
		this.filterSubstitutions = filterSubstitutions;
	}

}
