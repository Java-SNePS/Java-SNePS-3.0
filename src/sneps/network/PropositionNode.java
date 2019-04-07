package sneps.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.term.Term;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Pair;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.MatchChannel;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Match;
import sneps.snip.matching.Matcher;
import sneps.snip.matching.Substitutions;

public class PropositionNode extends Node implements Serializable {
	private Support basicSupport;
	protected ChannelSet outgoingChannels;
	protected ChannelSet incomingChannels;
	protected ReportSet knownInstances;
	protected ReportSet newInstances;

	public PropositionNode() {
		outgoingChannels = new ChannelSet();
		incomingChannels = new ChannelSet();
		knownInstances = new ReportSet();
	}

	public PropositionNode(Term trm) {
		super(Semantic.proposition, trm);
		outgoingChannels = new ChannelSet();
		incomingChannels = new ChannelSet();
		knownInstances = new ReportSet();
		setTerm(trm);
	}

	/***
	 * Adding a report to all outgoing channels
	 * 
	 * @param report
	 */
	public void broadcastReport(Report report) {
		for (Channel outChannel : outgoingChannels) {
			if (outChannel.addReport(report)) {
				// System.out.println("SENDING REPORT " + this);
			}
		}
	}

	public boolean sendReport(Report report, Channel channel) {
		if (channel.addReport(report)) {
			// System.out.println("SENDING REPORT " + this);
			return true;
		}
		return false;
	}

	/***
	 * Method handling all types of Channels establishment according to different
	 * channel types passed through the matching.
	 * 
	 * @param type           type of channel being addressed
	 * @param currentElement source Node/Match element being addressed
	 * @param switchSubs     mapped substitutions from origin node
	 * @param filterSubs     constraints substitutions for a specific request
	 * @param contextId      context name used
	 * @return the established type based channel
	 */
	private Channel establishChannel(ChannelTypes type, Object currentElement, Substitutions switchSubs,
			Substitutions filterSubs, String contextId) {
		boolean matchTypeEstablishing = currentElement instanceof Match;
		Node evaluatedReporter = matchTypeEstablishing ? ((Match) currentElement).getNode() : (Node) currentElement;
		Substitutions switchLinearSubs = switchSubs == null ? new LinearSubstitutions() : switchSubs;
		Channel newChannel;
		switch (type) {
		case MATCHED:
			newChannel = new MatchChannel(switchLinearSubs, filterSubs, contextId, this, evaluatedReporter, true);
			break;
		case RuleAnt:
			newChannel = new AntecedentToRuleChannel(switchLinearSubs, filterSubs, contextId, this, evaluatedReporter,
					true);
		default:
			newChannel = new RuleToConsequentChannel(switchLinearSubs, filterSubs, contextId, this, evaluatedReporter,
					true);
		}
		return newChannel;

	}

	public void sendRequests(List<Match> list, String contextId, ChannelTypes channelType) {
		for (Match currentMatch : list) {
			Substitutions switchSubs = currentMatch.getSwitchSubs();
			Substitutions filterSubs = currentMatch.getFilterSubs();
			Channel newChannel = establishChannel(channelType, currentMatch, switchSubs, filterSubs, contextId);
			incomingChannels.addChannel(newChannel);
			currentMatch.getNode().receiveRequest(newChannel);
		}
	}

	public void sendRequests(NodeSet ns, Substitutions filterSubs, String contextID, ChannelTypes channelType) {
		for (Node sentTo : ns) {
			Channel newChannel = establishChannel(channelType, sentTo, null, filterSubs, contextID);
			incomingChannels.addChannel(newChannel);
			sentTo.receiveRequest(newChannel);
		}
	}

	public void processSingleReportsChannel(Channel currentChannel) {
		ReportSet reports = currentChannel.getReportsBuffer();
		for (Report currentReport : reports) {
			Report alteredReport = new Report(currentReport.getSubstitutions(), currentReport.getSupports(),
					currentReport.getSign(), currentReport.getContextName());
			if (knownInstances.contains(alteredReport)) {
				continue;
			}
			for (Channel outChannel : outgoingChannels)
				outChannel.addReport(alteredReport);
			currentChannel.clearReportsBuffer();
		}
		currentChannel.clearReportsBuffer();
	}

	public void processReports() {
		for (Channel inChannel : incomingChannels)
			processSingleReportsChannel(inChannel);
	}

	/***
	 * Request handling in Non-Rule proposition nodes.
	 * 
	 * @param currentChannel
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws DuplicatePropositionException
	 */
	public void processSingleRequestsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		// TODO check correctness
		int instanceNodeId = getId();
		PropositionSet propSet = new PropositionSet();
		propSet.add(instanceNodeId);
		String currentContextName = currentChannel.getContextName();
		Context desiredContext = Controller.getContextByName(currentContextName);
		if (assertedInContext(desiredContext)) {
			// TODO change the subs to hashsubs
			/**
			 * Sends an instance of this report
			 */
			Set<Support> support = new HashSet<Support>();
			support.add(new Support(instanceNodeId));
			Report reply = new Report(new LinearSubstitutions(), support, true, currentChannel.getContextName());
			knownInstances.addReport(reply);
			broadcastReport(reply);
		} else {
			/**
			 * Sends any previously known instances
			 */
			boolean sentAtLeastOne = false;
			for (Report currentReport : knownInstances) {
				sentAtLeastOne |= sendReport(currentReport, currentChannel);
			}
			// TODO Akram: passed the filter subs to isWhQuest, is that correct?
			// TODO Youssef: passed the switch subs to isWhQuest, is that also correct?
			Substitutions switchSubs = currentChannel.getSwitch().getSubstitutions();
			Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
			if (!sentAtLeastOne || isWhQuestion(switchSubs, filterSubs))
				if (!alreadyWorking(currentChannel)) {
					getNodesToSendRequests(ChannelTypes.RuleCons, currentChannel.getContextName(),
							currentChannel.getFilter().getSubstitutions());
					if (!(currentChannel instanceof MatchChannel))
						getNodesToSendRequests(ChannelTypes.MATCHED, currentChannel.getContextName(), null);
				}
		}
	}

	/***
	 * 
	 * @param desiredContext
	 * @return whether the PropositionNode is asserted in a desiredContext or not
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	public boolean assertedInContext(Context desiredContext)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return desiredContext.isAsserted(this);
	}

	public void processRequests() {
		for (Channel outChannel : outgoingChannels)
			try {
				processSingleRequestsChannel(outChannel);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
				e.printStackTrace();
			} catch (DuplicatePropositionException e) {
				e.printStackTrace();
			}
	}

	/***
	 * Requests received added to the low priority queue to be served accordingly
	 * through the runner.
	 */
	public void receiveRequest(Channel channel) {
		outgoingChannels.addChannel(channel);
		Runner.addToLowQueue(this);
		channel.setRequestProcessed(true);
	}

	/***
	 * Reports received added to the high priority queue to be served accordingly
	 * through the runner.
	 */
	public void receiveReports(Channel channel) {
		outgoingChannels.addChannel(channel);
		Runner.addToHighQueue(this);
	}

	public void deduce() {
		Runner.initiate();
		String currentContextName = Controller.getCurrentContextName();
		getNodesToSendRequests(ChannelTypes.RuleCons, currentContextName, null);
		getNodesToSendRequests(ChannelTypes.MATCHED, currentContextName, null);
		Runner.run(); // what to return here ?
	}

	/***
	 * Method handling all types of Nodes retrieval and sending different type-based
	 * requests to each Node Type
	 * 
	 * @param type               type of channel being addressed
	 * @param currentContextName context name used
	 * @param substitutions      channel substitutions applied over the channel
	 */
	private void getNodesToSendRequests(ChannelTypes type, String currentContextName, Substitutions substitutions) {
		try {
			switch (type) {
			case MATCHED:
				List<Match> matchesReturned = Matcher.match(this);
				if (matchesReturned != null)
					sendRequests(matchesReturned, currentContextName, type);
				break;
			case RuleCons:
				NodeSet dominatingRules = getDominatingRules();
				// TODO Youssef: check if passing a new LinearSubstitutions is correct
				Substitutions linearSubs = substitutions == null ? new LinearSubstitutions() : substitutions;
				sendRequests(dominatingRules, linearSubs, currentContextName, type);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * Method checking if a similar more generic request is being processed and
	 * awaits a report in order not to re-send the same request
	 * 
	 * @param channel current channel handling the current request
	 * @return boolean represents whether a more generic request has been received
	 */
	public boolean alreadyWorking(Channel channel) {
		ChannelSet filteredChannelsSet = incomingChannels.getFilteredRequestChannels(true);
		Substitutions currentChannelFilterSubs = channel.getFilter().getSubstitutions();
		for (Channel incomingChannel : filteredChannelsSet) {
			if (incomingChannel != channel) {
				Substitutions processedChannelFilterSubs = incomingChannel.getFilter().getSubstitutions();
				if (processedChannelFilterSubs.isSubSet(currentChannelFilterSubs)) {
					return true;
				}
			}
		}
		return false;
	}

	public Support getBasicSupport() {
		return basicSupport;
	}

	public void setBasicSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		this.basicSupport = new Support(this.getId());
	}

	public ChannelSet getOutgoingChannels() {
		return outgoingChannels;
	}

	public void setOutgoingChannels(ChannelSet outgoingChannels) {
		this.outgoingChannels = outgoingChannels;
	}

	public ChannelSet getIncomingChannels() {
		return incomingChannels;
	}

	public void setIncomingChannels(ChannelSet incomingChannels) {
		this.incomingChannels = incomingChannels;
	}

	public ReportSet getKnownInstances() {
		return knownInstances;
	}

	public void setKnownInstances(ReportSet knownInstances) {
		this.knownInstances = knownInstances;
	}

	public Hashtable<String, PropositionSet> getAssumptionBasedSupport() {
		return basicSupport.getAssumptionBasedSupport();

	}

	public Hashtable<String, PropositionSet> getJustificationSupport()
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.getJustificationSupport();
	}

	public void addJustificationBasedSupport(PropositionSet propSet)
			throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException,
			DuplicatePropositionException, CannotInsertJustificationSupportException {
		basicSupport.addJustificationBasedSupport(propSet);
	}

	public void removeNodeFromSupports(PropositionNode propNode)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		basicSupport.removeNodeFromSupports(propNode);

	}

	public void addParentNode(int id)
			throws DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		basicSupport.addParentNode(id);

	}

	public ArrayList<Integer> getParentSupports() {
		return basicSupport.getParentSupports();
	}

	public boolean HasChildren() {
		return basicSupport.HasChildren();
	}

	public ArrayList<ArrayList<ArrayList<Integer>>> getMySupportsTree()
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.getMySupportsTree();
	}

	public boolean reStructureJustifications() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.reStructureJustifications();
	}

	public void setHyp(boolean isHyp) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		basicSupport.setHyp(isHyp);
	}
}