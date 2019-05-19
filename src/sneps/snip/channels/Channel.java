package sneps.snip.channels;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Filter;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.Switch;
import sneps.snip.matching.Substitutions;

public abstract class Channel {
	static int count = 0;
	private int idCount;
	protected Filter filter;
	private Switch switcher;
	private String contextName;
	private Node requester;
	private Node reporter;
	private boolean valve;
	private ReportSet reportsBuffer;
	private boolean requestProcessed = false;
	private boolean reportProcessed = false;

	public Channel() {
		idCount = count++;
		filter = new Filter();
		switcher = new Switch();
		setReportsBuffer(new ReportSet());
	}

	public Channel(Substitutions switcherSubstitution, Substitutions filterSubstitutions, String contextID,
			Node requester, Node reporter, boolean v) {
		idCount = count++;
		this.filter = new Filter(filterSubstitutions);
		this.switcher = new Switch(switcherSubstitution);
		this.contextName = contextID;
		this.requester = requester;
		this.reporter = reporter;
		this.valve = v;
		this.reporter = reporter;
		setReportsBuffer(new ReportSet());
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public boolean testReportToSend(Report report) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		boolean passTest = filter.canPass(report); // te be reviewed
		Context channelContext = Controller.getContextByName(getContextName());
		if (passTest && report.anySupportAssertedInContext(channelContext)) {
			System.out.println("\nThe switcher data:\n" + switcher);
			switcher.switchReport(report);
			PropositionNode requesterNode = (PropositionNode) getRequester();
			requesterNode.receiveReport(this);
			getReportsBuffer().addReport(report);
			return true;
		}
		return false;
	}

	/* add helper to check report test */
	public String getContextName() {
		return contextName;
	}

	public boolean isValveOpen() {
		return valve;
	}

	public Filter getFilter() {
		return filter;
	}

	public Switch getSwitch() {
		return switcher;
	}

	public Node getRequester() {
		return requester;
	}

	public Node getReporter() {
		return reporter;
	}

	public ReportSet getReportsBuffer() {
		return reportsBuffer;
	}

	public boolean isRequestProcessed() {
		return requestProcessed;
	}

	public void setRequestProcessed(boolean requestProcessed) {
		this.requestProcessed = requestProcessed;
	}

	public boolean isReportProcessed() {
		return reportProcessed;
	}

	public void setReportProcessed(boolean reportProcessed) {
		this.reportProcessed = reportProcessed;
	}

	public void setValve(boolean valve) {
		this.valve = valve;
	}

	public void clearReportsBuffer() {
		getReportsBuffer().clear();
	}

	public boolean processedGeneralizedRequest(Substitutions currentChannelFilterSubs) {
		ChannelSet filteredChannelsSet = ((PropositionNode) requester).getIncomingChannels()
				.getFilteredRequestChannels(true);
		for (Channel incomingChannel : filteredChannelsSet) {
			if (incomingChannel != this) {
				Substitutions processedChannelFilterSubs = incomingChannel.getFilter().getSubstitutions();
				if (processedChannelFilterSubs.isSubSet(currentChannelFilterSubs)) {
					return true;
				}
			}
		}
		return false;

	}

	public void setReportsBuffer(ReportSet reportsBuffer) {
		this.reportsBuffer = reportsBuffer;
	}

	public int getId() {
		return idCount;
	}

	public void setId(int idCount) {
		this.idCount = idCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Channel) {
			Channel channel = (Channel) obj;
			boolean filterCheck = filter.getSubstitutions().equals(channel.getFilter().getSubstitutions());
			boolean contextCheck = getContextName().equals(channel.getContextName());
			boolean requesterCheck = getRequester().getId() == channel.getRequester().getId();
			boolean reporterCheck = getReporter().getId() == channel.getReporter().getId();
			return filterCheck && contextCheck && requesterCheck && reporterCheck;
		}
		return super.equals(obj);
	}

}
