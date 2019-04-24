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
import sneps.snip.InferenceTypes;
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
			if (outChannel.testReportToSend(report)) {
				// System.out.println("SENDING REPORT " + this);
			}
		}
	}

	public boolean sendReport(Report report, Channel channel) {
		if (channel.testReportToSend(report)) {
			// System.out.println("SENDING REPORT " + this);
			return true;
		}
		return false;
	}

	protected void sendReports(NodeSet isAntecedentTo, ReportSet reports, ChannelTypes channelType) {
		for (Node node : isAntecedentTo) {
			for (Report report : reports) {
				Substitutions reportSubs = report.getSubstitutions();
				Set<Support> reportSuppSet = report.getSupports();
				boolean reportSign = report.getSign();
				String reportContextName = report.getContextName();
				Channel newChannel = establishChannel(channelType, node, null, reportSubs, reportContextName,
						InferenceTypes.FORWARD, -1);
				outgoingChannels.addChannel(newChannel);
				node.receiveReport(newChannel);
			}
		}

	}

	protected void sendReports(List<Match> list, ReportSet reports) {
		for (Match match : list) {
			for (Report report : reports) {
				Substitutions reportSubs = report.getSubstitutions();
				Set<Support> reportSuppSet = report.getSupports();
				Node sentTo = match.getNode();
				boolean reportSign = report.getSign();
				String reportContextName = report.getContextName();
				int matchType = match.getMatchType();
				Channel newChannel = establishChannel(ChannelTypes.MATCHED, sentTo, null, reportSubs, reportContextName,
						InferenceTypes.FORWARD, matchType);
				outgoingChannels.addChannel(newChannel);
				sentTo.receiveReport(newChannel);
			}
		}
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
	 * @param inferenceType  inference type used for this process
	 * @param matchType      int representing the match Type. -1 if not a matching
	 *                       node scenario
	 * @return the established type based channel
	 */
	protected Channel establishChannel(ChannelTypes type, Object currentElement, Substitutions switchSubs,
			Substitutions filterSubs, String contextId, InferenceTypes inferenceType, int matchType) {
		boolean matchTypeEstablishing = currentElement instanceof Match;
		Node evaluatedReporter = matchTypeEstablishing ? ((Match) currentElement).getNode() : (Node) currentElement;
		Substitutions switchLinearSubs = switchSubs == null ? new LinearSubstitutions() : switchSubs;
		Channel newChannel;
		switch (type) {
		case MATCHED:
			newChannel = new MatchChannel(switchLinearSubs, filterSubs, contextId, this, evaluatedReporter, true,
					inferenceType, matchType);
			break;
		case RuleAnt:
			newChannel = new AntecedentToRuleChannel(switchLinearSubs, filterSubs, contextId, this, evaluatedReporter,
					true, inferenceType);
		default:
			newChannel = new RuleToConsequentChannel(switchLinearSubs, filterSubs, contextId, this, evaluatedReporter,
					true, inferenceType);
		}
		return newChannel;

	}

	public void sendRequests(List<Match> list, String contextId, ChannelTypes channelType,
			InferenceTypes inferenceType) {
		for (Match currentMatch : list) {
			Substitutions switchSubs = currentMatch.getSwitchSubs();
			Substitutions filterSubs = currentMatch.getFilterSubs();
			int matchType = currentMatch.getMatchType();
			Channel newChannel = establishChannel(channelType, currentMatch, switchSubs, filterSubs, contextId,
					inferenceType, matchType);
			incomingChannels.addChannel(newChannel);
			currentMatch.getNode().receiveRequest(newChannel);
		}
	}

	public void sendRequests(NodeSet ns, Substitutions filterSubs, String contextID, ChannelTypes channelType,
			InferenceTypes inferenceType) {
		for (Node sentTo : ns) {
			Channel newChannel = establishChannel(channelType, sentTo, null, filterSubs, contextID, inferenceType, -1);
			incomingChannels.addChannel(newChannel);
			sentTo.receiveRequest(newChannel);
		}
	}

	/***
	 * Report handling in Non-Rule proposition nodes.
	 * 
	 * @param currentChannel
	 */
	public void processSingleReportsChannel(Channel currentChannel) {
		ReportSet reports = currentChannel.getReportsBuffer();
		for (Report currentReport : reports) {

			Substitutions reportSubs = currentReport.getSubstitutions();
			Set<Support> reportSupportSet = currentReport.getSupports();
			boolean reportSign = currentReport.isPositive();
			String reportContextName = currentReport.getContextName();
			boolean toBeSentFlag = true;
			if (currentChannel instanceof MatchChannel) {
				int channelMatchType = ((MatchChannel) currentChannel).getMatchType();
				toBeSentFlag = (channelMatchType == 0) || (channelMatchType == 1 && currentReport.isPositive())
						|| (channelMatchType == 2 && currentReport.isNegative());
			}
			Report alteredReport = new Report(reportSubs, reportSupportSet, reportSign, reportContextName);
			if (knownInstances.contains(alteredReport))
				continue;
			if (toBeSentFlag)
				broadcastReport(alteredReport);
			currentChannel.clearReportsBuffer();
		}
		/* Handling forward inference broadcasting */
		if (currentChannel.getInferenceType() == InferenceTypes.FORWARD) {
			List<Match> matchesReturned = Matcher.match(this);
			if (matchesReturned != null)
				sendReports(matchesReturned, reports);
			NodeSet isAntecedentTo = getDominatingRules();
			sendReports(isAntecedentTo, reports, ChannelTypes.RuleAnt);
		}
		currentChannel.clearReportsBuffer();
	}

	// PROCESS REPORT : 3adi -> , forward
	// -> same as 3adi , plus matching to send and get the nodes el howa lihom
	// antecedents we send reports

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
			Set<Support> support = new HashSet<Support>();
			support.add(new Support(instanceNodeId));
			Report reply = new Report(new LinearSubstitutions(), support, true, currentChannel.getContextName());
			knownInstances.addReport(reply);
			broadcastReport(reply);
		} else {
			boolean sentAtLeastOne = false;
			for (Report currentReport : knownInstances)
				sentAtLeastOne |= sendReport(currentReport, currentChannel);
			Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
			if (!sentAtLeastOne || isWhQuestion(filterSubs)) {
				NodeSet dominatingRules = getDominatingRules();
				NodeSet toBeSentTo = alreadyWorking(dominatingRules, currentChannel, false);
				sendRequests(toBeSentTo, new LinearSubstitutions(), currentContextName, ChannelTypes.RuleAnt,
						currentChannel.getInferenceType());
				if (!(currentChannel instanceof MatchChannel)) // was in !alreadyWorking if condition
					getNodesToSendRequests(ChannelTypes.MATCHED, currentChannel.getContextName(), null,
							currentChannel.getInferenceType());
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
		getNodesToSendRequests(ChannelTypes.RuleCons, currentContextName, null, InferenceTypes.BACKWARD);
		getNodesToSendRequests(ChannelTypes.MATCHED, currentContextName, null, InferenceTypes.BACKWARD);
		Runner.run(); // what to return here ?
	}

	public void add() {

	}

	/***
	 * Method handling all types of Nodes retrieval and sending different type-based
	 * requests to each Node Type
	 * 
	 * @param type               type of channel being addressed
	 * @param currentContextName context name used
	 * @param substitutions      channel substitutions applied over the channel
	 * @param inferenceType      inference type used for this process
	 */
	protected void getNodesToSendRequests(ChannelTypes channelType, String currentContextName,
			Substitutions substitutions, InferenceTypes inferenceType) {
		try {
			switch (channelType) {
			case MATCHED:
				List<Match> matchesReturned = Matcher.match(this);
				if (matchesReturned != null)
					sendRequests(matchesReturned, currentContextName, channelType, inferenceType);
				break;
			case RuleCons:
				NodeSet dominatingRules = getDominatingRules();
				// TODO Youssef: check if passing a new LinearSubstitutions is correct
				Substitutions linearSubs = substitutions == null ? new LinearSubstitutions() : substitutions;
				sendRequests(dominatingRules, linearSubs, currentContextName, channelType, inferenceType);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * Method comparing opened incoming channels over each node of the nodes whether
	 * a more generic request of the specified channel was previously sent in order
	 * not to re-send redundant requests -- ruleType gets applied on Andor or Thresh
	 * part.
	 * 
	 * @param node    set on which we will check existing request
	 * @param channel current channel handling the current request
	 * @return NodeSet containing all nodes that has not previously requested the
	 *         subset of the specified channel request
	 */
	public static NodeSet alreadyWorking(NodeSet nodes, Channel channel, boolean ruleType) {
		NodeSet nodesToConsider = new NodeSet();
		for (Node sourceNode : nodes)
			if (sourceNode instanceof PropositionNode) {
				boolean conditionMet = ruleType && sourceNode == channel.getRequester();
				if (!conditionMet) {
					conditionMet = true;
					Substitutions currentChannelFilterSubs = channel.getFilter().getSubstitutions();
					ChannelSet outgoingChannels = ((PropositionNode) sourceNode).getOutgoingChannels();
					ChannelSet filteredChannelsSet = outgoingChannels.getFilteredRequestChannels(true);
					for (Channel outgoingChannel : filteredChannelsSet) {
						Substitutions processedChannelFilterSubs = outgoingChannel.getFilter().getSubstitutions();
						conditionMet &= !processedChannelFilterSubs.isSubSet(currentChannelFilterSubs)
								&& outgoingChannel.getRequester() == channel.getReporter();
						if (conditionMet) {
							nodesToConsider.addNode(sourceNode);
							break;
						}
					}
				}
			}
		return nodesToConsider;
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