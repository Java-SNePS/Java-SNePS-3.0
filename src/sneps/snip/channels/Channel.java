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

	private Filter filter;
	private Switch switcher;
	private String contextName;
	private Node requester;
	private Node reporter;
	private boolean valve;
	private ReportSet reportsBuffer;
	private boolean requestProcessed = false;
	private boolean reportProcessed = false;
	private InferenceTypes inferenceType;

	public Channel() {
		filter = new Filter();
		switcher = new Switch();
		reportsBuffer = new ReportSet();
	}

	public Channel(Substitutions switcherSubstitution, Substitutions filterSubstitutions, String contextID,
			Node requester, Node reporter, boolean v, InferenceTypes inferenceType) {
		this.filter = new Filter(filterSubstitutions);
		this.switcher = new Switch(switcherSubstitution);
		this.contextName = contextID;
		this.requester = requester;
		this.reporter = reporter;
		this.valve = v;
		this.reporter = reporter;
		reportsBuffer = new ReportSet();
		this.inferenceType = inferenceType;
	}

	public boolean testReportToSend(Report report) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		boolean passTest = filter.canPass(report);
		System.out.println("Can pass " + passTest);
		/* Test the context name if it will pass or not */
		Context channelContext = Controller.getContextByName(getContextName());
		if (passTest && report.anySupportAssertedInContext(channelContext)) {
			System.out.println("\nThe switcher data:\n" + switcher);
			switcher.switchReport(report);
			reportsBuffer.addReport(report);
			Runner.addToHighQueue(requester);
			return true;
		}
		return false;
	}

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

	public InferenceTypes getInferenceType() {
		return inferenceType;
	}

	public void setInferenceType(InferenceTypes inferenceType) {
		this.inferenceType = inferenceType;
	}

	public void setValve(boolean valve) {
		this.valve = valve;
	}

	public void clearReportsBuffer() {
		reportsBuffer.clear();
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
}
