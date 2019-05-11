package sneps.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.cables.UpCable;
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
	 * Method handling all types of Channels establishment according to different
	 * channel types passed through the matching.
	 * 
	 * @param type           type of channel being addressed
	 * @param currentElement source Node/Match element being addressed
	 * @param switchSubs     mapped substitutions from origin node
	 * @param filterSubs     constraints substitutions for a specific request
	 * @param contextName    context name used
	 * @param inferenceType  inference type used for this process
	 * @param matchType      int representing the match Type. -1 if not a matching
	 *                       node scenario
	 * @return the established type based channel
	 */
	protected Channel establishChannel(ChannelTypes type, Object currentElement, Substitutions switchSubs,
			Substitutions filterSubs, String contextName, int matchType) {
		boolean matchTypeEstablishing = currentElement instanceof Match;
		Node evaluatedReporter = matchTypeEstablishing ? ((Match) currentElement).getNode() : (Node) currentElement;
		Substitutions switchLinearSubs = switchSubs == null ? new LinearSubstitutions() : switchSubs;
		Channel newChannel;
		switch (type) {
		case MATCHED:
			newChannel = new MatchChannel(switchLinearSubs, filterSubs, contextName, this, evaluatedReporter, true,
					matchType);
			break;
		case RuleAnt:
			newChannel = new AntecedentToRuleChannel(switchLinearSubs, filterSubs, contextName, this, evaluatedReporter,
					true);
		default:
			newChannel = new RuleToConsequentChannel(switchLinearSubs, filterSubs, contextName, this, evaluatedReporter,
					true);
		}
		return newChannel;

	}

	/***
	 * Used to send a report over a channel through calling Channel.testReportToSend
	 * 
	 * @param report
	 * @param channel
	 * @return
	 */
	public boolean sendReport(Report report, Channel channel) {
		try {
			if (channel.testReportToSend(report)) {
				System.out.println("Report instance (" + report + ") was successfuly sent over (" + channel + ")");
			}
		} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
			System.out.println("Report instance (" + report + ") could not be sent.");
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * Trying to send a report to all outgoing channels
	 * 
	 * @param report
	 */
	public void broadcastReport(Report report) {
		for (Channel outChannel : outgoingChannels)
			sendReport(report, outChannel);
	}

	/***
	 * Helper method responsible for establishing channels between this current node
	 * and each of the NodeSet to further request instances with the given inputs
	 * 
	 * @param ns            NodeSet to be sent to
	 * @param toBeSent      Substitutions to be passed
	 * @param contextID     latest channel context
	 * @param channelType
	 * @param inferenceType
	 */
	protected void sendReportToNodeSet(NodeSet ns, Report toBeSent, String contextID, ChannelTypes channelType) {
		for (Node sentTo : ns) {
			Substitutions reportSubs = toBeSent.getSubstitutions();
			Channel newChannel = establishChannel(channelType, sentTo, null, reportSubs, contextID, -1);
			ReportSet channelReportBuffer = newChannel.getReportsBuffer();
			channelReportBuffer.addReport(toBeSent);
			outgoingChannels.addChannel(newChannel);
			sentTo.receiveReport(newChannel);
		}
	}

	protected void sendReportsToNodeSet(NodeSet ns, ReportSet reports, String contextID, ChannelTypes channelType) {
		for (Report report : reports) {
			sendReportToNodeSet(ns, report, contextID, channelType);
		}

	}

	/***
	 * Helper method responsible for establishing channels between this current node
	 * and each of the List<Match> to further request instances with the given
	 * inputs
	 * 
	 * @param list
	 * @param toBeSent
	 * @param contextId
	 * @param inferenceType
	 */
	protected void sendReportToMatches(List<Match> list, Report toBeSent, String contextId) {
		for (Match currentMatch : list) {
			Substitutions reportSubs = toBeSent.getSubstitutions();
			int matchType = currentMatch.getMatchType();
			Channel newChannel = establishChannel(ChannelTypes.MATCHED, currentMatch, null, reportSubs, contextId,
					matchType);
			ReportSet channelReportBuffer = newChannel.getReportsBuffer();
			channelReportBuffer.addReport(toBeSent);
			outgoingChannels.addChannel(newChannel);
			currentMatch.getNode().receiveReport(newChannel);
		}
	}

	protected void sendReportsToMatches(List<Match> list, ReportSet reports, String contextId) {
		for (Report report : reports) {
			sendReportToMatches(list, report, contextId);
		}
	}

	/***
	 * Helper method responsible for establishing channels between this current node
	 * and each of the List<Match> to further request instances with the given
	 * inputs
	 * 
	 * @param list
	 * @param contextId
	 * @param inferenceType
	 */
	protected void sendRequestsToMatches(List<Match> list, String contextId) {
		for (Match currentMatch : list) {
			Substitutions switchSubs = currentMatch.getSwitchSubs();
			Substitutions filterSubs = currentMatch.getFilterSubs();
			int matchType = currentMatch.getMatchType();
			Channel newChannel = establishChannel(ChannelTypes.MATCHED, currentMatch, switchSubs, filterSubs, contextId,
					matchType);
			incomingChannels.addChannel(newChannel);
			currentMatch.getNode().receiveRequest(newChannel);
		}
	}

	/***
	 * Helper method responsible for establishing channels between this current node
	 * and each of the NodeSet to further request instances with the given inputs
	 * 
	 * @param ns            NodeSet to be sent to
	 * @param filterSubs    Substitutions to be passed
	 * @param contextID     latest channel context
	 * @param channelType
	 * @param inferenceType
	 */
	protected void sendRequestsToNodeSet(NodeSet ns, Substitutions filterSubs, String contextID,
			ChannelTypes channelType) {
		for (Node sentTo : ns) {
			Channel newChannel = establishChannel(channelType, sentTo, null, filterSubs, contextID, -1);
			incomingChannels.addChannel(newChannel);
			sentTo.receiveRequest(newChannel);
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

	public boolean assertedInContext(String desiredContextName)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return Controller.getContextByName(desiredContextName).isAsserted(this);
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
		channel.setReportProcessed(true);
	}

	public void deduce() {
		Runner.initiate();
		String currentContextName = Controller.getCurrentContextName();
		getNodesToSendRequest(ChannelTypes.RuleCons, currentContextName, null, InferenceTypes.BACKWARD);
		getNodesToSendRequest(ChannelTypes.MATCHED, currentContextName, null, InferenceTypes.BACKWARD);
		// what to return here ?
		System.out.println(Runner.run());
	}

	public void add() {
		Runner.initiate();
		String currentContextName = Controller.getCurrentContextName();
		boolean reportSign = Controller.isNegated(this);
		getNodesToSendReport(ChannelTypes.RuleAnt, currentContextName, null, reportSign, InferenceTypes.FORWARD);
		getNodesToSendReport(ChannelTypes.MATCHED, currentContextName, null, reportSign, InferenceTypes.FORWARD);
		System.out.println(Runner.run());
	}

	protected void getNodesToSendReport(ChannelTypes channelType, String currentContextName,
			Substitutions substitutions, boolean reportSign, InferenceTypes inferenceType) {
		try {
			PropositionSet supportPropSet = new PropositionSet();
			supportPropSet.add(getId());
			Report toBeSent = new Report(substitutions, supportPropSet, reportSign, inferenceType);
			switch (channelType) {
			case MATCHED:
				List<Match> matchesReturned = Matcher.match(this);
				if (matchesReturned != null)
					sendReportToMatches(matchesReturned, toBeSent, currentContextName);
				break;
			case RuleAnt:
				if (this instanceof RuleNode) {
					NodeSet antecedentsNodes = ((RuleNode) this).getDownAntNodeSet();
					sendReportToNodeSet(antecedentsNodes, toBeSent, currentContextName, channelType);
					break;
				}

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	protected void getNodesToSendRequest(ChannelTypes channelType, String currentContextName,
			Substitutions substitutions, InferenceTypes inferenceType) {
		try {
			switch (channelType) {
			case MATCHED:
				List<Match> matchesReturned = Matcher.match(this);
				if (matchesReturned != null)
					sendRequestsToMatches(matchesReturned, currentContextName);
				break;
			case RuleCons:
				NodeSet dominatingRules = getUpConsNodeSet();
				// TODO Youssef: check if passing a new LinearSubstitutions is correct
				Substitutions linearSubs = substitutions == null ? new LinearSubstitutions() : substitutions;
				sendRequestsToNodeSet(dominatingRules, linearSubs, currentContextName, channelType);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * Method comparing opened incoming channels over each match's node of the
	 * matches whether a more generic request of the specified channel was
	 * previously sent in order not to re-send redundant requests -- ruleType gets
	 * applied on Andor or Thresh part.
	 * 
	 * @param matchingNodes
	 * @param currentChannel
	 * @param ruleType
	 * @return
	 */
	protected List<Match> removeAlreadyWorkingOn(List<Match> matchingNodes, Channel currentChannel, boolean ruleType) {
		List<Match> nodesToConsider = new ArrayList<Match>();
		for (Match sourceMatch : matchingNodes) {
			Node sourceNode = sourceMatch.getNode();
			if (sourceNode instanceof PropositionNode) {
				boolean conditionMet = ruleType && sourceNode == currentChannel.getRequester();
				if (!conditionMet) {
					conditionMet = true;
					Substitutions currentChannelFilterSubs = currentChannel.getFilter().getSubstitutions();
					ChannelSet outgoingChannels = ((PropositionNode) sourceNode).getOutgoingChannels();
					ChannelSet filteredChannelsSet = outgoingChannels.getFilteredRequestChannels(true);
					for (Channel outgoingChannel : filteredChannelsSet) {
						Substitutions processedChannelFilterSubs = outgoingChannel.getFilter().getSubstitutions();
						conditionMet &= !processedChannelFilterSubs.isSubSet(currentChannelFilterSubs)
								&& outgoingChannel.getRequester() == currentChannel.getReporter();
					}
					if (conditionMet)
						nodesToConsider.add(sourceMatch);
				}
			}
		}
		return nodesToConsider;
	}

	/***
	 * Method comparing opened incoming channels over each node of the nodes whether
	 * a more generic request of the specified channel was previously sent in order
	 * not to re-send redundant requests -- ruleType gets applied on Andor or Thresh
	 * part.
	 * 
	 * @param node         set on which we will check existing request
	 * @param channel      current channel handling the current request
	 * @param toBeCompared subs to be compared over each node
	 * @return NodeSet containing all nodes that has not previously requested the
	 *         subset of the specified channel request
	 */
	protected static NodeSet removeAlreadyWorkingOn(NodeSet nodes, Channel channel, Substitutions toBeCompared,
			boolean ruleType) {
		NodeSet nodesToConsider = new NodeSet();
		for (Node sourceNode : nodes)
			if (sourceNode instanceof PropositionNode) {
				boolean conditionMet = ruleType && sourceNode == channel.getRequester();
				if (!conditionMet) {
					conditionMet = true;
					ChannelSet outgoingChannels = ((PropositionNode) sourceNode).getOutgoingChannels();
					ChannelSet filteredChannelsSet = outgoingChannels.getFilteredRequestChannels(true);
					for (Channel outgoingChannel : filteredChannelsSet) {
						Substitutions processedChannelFilterSubs = outgoingChannel.getFilter().getSubstitutions();
						conditionMet &= !processedChannelFilterSubs.isSubSet(toBeCompared)
								&& outgoingChannel.getRequester() == channel.getReporter();
					}
					if (conditionMet)
						nodesToConsider.addNode(sourceNode);
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

	/***
	 * Method getting the NodeSet that this current node is considered a consequent
	 * to
	 * 
	 * @return
	 */
	public NodeSet getUpConsNodeSet() {
		NodeSet ret = new NodeSet();
		UpCable consequentCable = this.getUpCableSet().getUpCable("cq");
		UpCable argsCable = this.getUpCableSet().getUpCable("arg");
		if (argsCable != null) {
			ret.addAll(argsCable.getNodeSet());
		}
		if (consequentCable != null) {
			ret.addAll(consequentCable.getNodeSet());
		}

		return ret;
	}

	/***
	 * Method getting the NodeSet for nodes that are consequents to this node
	 * 
	 * @return
	 */
	/*
	 * public NodeSet getDownConsNodeSet() { NodeSet ret = new NodeSet(); UpCable
	 * consequentCable = this.getDownCableSet().getUpCable("cq"); UpCable argsCable
	 * = this.getDownCableSet().getUpCable("arg"); if (argsCable != null) {
	 * ret.addAll(argsCable.getNodeSet()); } if (consequentCable != null) {
	 * ret.addAll(consequentCable.getNodeSet()); }
	 * 
	 * return ret; }
	 */

	/***
	 * Method getting the NodeSet that this current node is considered an antecedent
	 * to
	 * 
	 * @return
	 */
	public NodeSet getUpAntNodeSet() {
		NodeSet ret = new NodeSet();
		UpCable argsCable = this.getUpCableSet().getUpCable("arg");
		UpCable andAntCable = this.getUpCableSet().getUpCable("&ant");
		UpCable antCable = this.getUpCableSet().getUpCable("ant");
		if (argsCable != null) {
			ret.addAll(argsCable.getNodeSet());
		}
		if (antCable != null) {
			ret.addAll(antCable.getNodeSet());
		}
		if (andAntCable != null) {
			ret.addAll(andAntCable.getNodeSet());
		}

		return ret;
	}

	/***
	 * Method getting the NodeSet of the antecedents for this current node
	 * 
	 * @return
	 */
	/*
	 * public NodeSet getDownAntNodeSet() { NodeSet ret = new NodeSet(); UpCable
	 * argsCable = this.getDownCableSet().getUpCable("arg"); UpCable andAntCable =
	 * this.getDownCableSet().getUpCable("&ant"); UpCable antCable =
	 * this.getDownCableSet().getUpCable("ant"); if (argsCable != null) {
	 * ret.addAll(argsCable.getNodeSet()); } if (antCable != null) {
	 * ret.addAll(antCable.getNodeSet()); } if (andAntCable != null) {
	 * ret.addAll(andAntCable.getNodeSet()); }
	 * 
	 * return ret; }
	 */

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

	protected Set<Channel> getOutgoingAntecedentRuleChannels() {
		return outgoingChannels.getAntRuleChannels();
	}

	protected Set<Channel> getOutgoingRuleConsequentChannels() {
		return outgoingChannels.getRuleConsChannels();
	}

	protected Set<Channel> getOutgoingMatchChannels() {
		return outgoingChannels.getMatchChannels();
	}

	protected Set<Channel> getIncomingAntecedentRuleChannels() {
		return incomingChannels.getAntRuleChannels();
	}

	protected Set<Channel> getIncomingRuleConsequentChannels() {
		return incomingChannels.getRuleConsChannels();
	}

	protected Set<Channel> getIncomingMatchChannels() {
		return incomingChannels.getMatchChannels();
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
	 * Request handling in Non-Rule proposition nodes.
	 * 
	 * @param currentChannel
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws DuplicatePropositionException
	 */
	protected void processSingleRequestsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		Support nodeSupport = getBasicSupport();

		String currentContextName = currentChannel.getContextName();
		Context desiredContext = Controller.getContextByName(currentContextName);
		if (assertedInContext(desiredContext)) {
			// TODO change the subs to hashsubs
			int propNodeId = getId();
			PropositionSet supportPropSet = new PropositionSet();
			supportPropSet.add(propNodeId);
			Report reply = new Report(new LinearSubstitutions(), supportPropSet, true, InferenceTypes.BACKWARD);
			sendReport(reply, currentChannel);
		} else {
			boolean sentAtLeastOne = false;
			for (Report currentReport : knownInstances)
				sentAtLeastOne |= sendReport(currentReport, currentChannel);
			Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
			if (!sentAtLeastOne || isWhQuestion(filterSubs)) {
				NodeSet dominatingRules = getDominatingRules();
				NodeSet toBeSentToDom = removeAlreadyWorkingOn(dominatingRules, currentChannel, filterSubs, false);
				sendRequestsToNodeSet(toBeSentToDom, new LinearSubstitutions(), currentContextName,
						ChannelTypes.RuleAnt);
				if (!(currentChannel instanceof MatchChannel)) {
					List<Match> matchingNodes = Matcher.match(this);
					List<Match> toBeSentToMatch = removeAlreadyWorkingOn(matchingNodes, currentChannel, false);
					sendRequestsToMatches(toBeSentToMatch, currentContextName);
				}
			}
		}
	}

	public void processReports() {
		for (Channel inChannel : incomingChannels)
			try {
				processSingleReportsChannel(inChannel);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | DuplicatePropositionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/***
	 * Report handling in Non-Rule proposition nodes.
	 * 
	 * @param currentChannel
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws DuplicatePropositionException
	 */
	protected void processSingleReportsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		ReportSet reports = currentChannel.getReportsBuffer();
		String currentChannelContextName = currentChannel.getContextName();
		for (Report currentReport : reports) {
			Substitutions reportSubs = currentReport.getSubstitutions();
			PropositionSet reportSupport = currentReport.getSupport();
			boolean reportSign = currentReport.isPositive();
			boolean toBeSentFlag = true;
			/* to be removed and handled in testReportToSend */
			Report alteredBReport = new Report(reportSubs, reportSupport, reportSign, InferenceTypes.BACKWARD);
			Report alteredFReport = new Report(reportSubs, reportSupport, reportSign, InferenceTypes.FORWARD);
			boolean backwardReportFound = knownInstances.contains(alteredBReport);
			boolean forwardReportFound = knownInstances.contains(alteredFReport);
			if ((!backwardReportFound || !forwardReportFound) && toBeSentFlag) {
				broadcastReport(!forwardReportFound ? alteredFReport : alteredBReport);
			}
			/* Handling forward inference broadcasting */
			if (currentReport.getInferenceType() == InferenceTypes.FORWARD) {
				List<Match> matchesReturned = Matcher.match(this);
				sendReportToMatches(matchesReturned, currentReport, currentChannelContextName);
				NodeSet isAntecedentTo = getUpAntNodeSet();
				sendReportToNodeSet(isAntecedentTo, currentReport, currentChannelContextName, ChannelTypes.RuleAnt);
			}
		}
		currentChannel.clearReportsBuffer();
	}

	// PROCESS REPORT : 3adi -> , forward
	// -> same as 3adi , plus matching to send and get the nodes el howa lihom
	// antecedents we send reports

}