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
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Open;
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
import sneps.snip.KnownInstances;
import sneps.snip.Runner;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.MatchChannel;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.classes.VariableNodeStats;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Match;
import sneps.snip.matching.Matcher;
import sneps.snip.matching.Substitutions;

public class PropositionNode extends Node implements Serializable {
	private Support basicSupport;
	protected ChannelSet outgoingChannels;
	protected ChannelSet incomingChannels;
	protected KnownInstances knownInstances;
	protected ReportSet newInstances;

	public PropositionNode() {
		outgoingChannels = new ChannelSet();
		incomingChannels = new ChannelSet();
		knownInstances = new KnownInstances();
	}

	public PropositionNode(Term trm) {
		super(Semantic.proposition, trm);
		outgoingChannels = new ChannelSet();
		incomingChannels = new ChannelSet();
		knownInstances = new KnownInstances();
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
		/* BEGIN - Helpful Prints */
		String requesterIdent = getIdentifier();
		String reporterIdent = evaluatedReporter.getIdentifier();
		System.out.println("Trying to establish a channel from " + requesterIdent + " to " + reporterIdent);
		/* END - Helpful Prints */
		Substitutions switchLinearSubs = switchSubs == null ? new LinearSubstitutions() : switchSubs;
		Substitutions filterLinearSubs = filterSubs == null ? new LinearSubstitutions() : filterSubs;
		Channel newChannel;
		switch (type) {
		case MATCHED:
			newChannel = new MatchChannel(switchLinearSubs, filterLinearSubs, contextName, this, evaluatedReporter,
					true, matchType);
			break;
		case RuleAnt:
			newChannel = new AntecedentToRuleChannel(switchLinearSubs, filterLinearSubs, contextName, this,
					evaluatedReporter, true);
		default:
			newChannel = new RuleToConsequentChannel(switchLinearSubs, filterLinearSubs, contextName, this,
					evaluatedReporter, true);
		}
		ChannelSet incomingChannels = getIncomingChannels();
		Channel extractedChannel = incomingChannels.getChannel(newChannel);
		if (extractedChannel == null) {
			/* BEGIN - Helpful Prints */
			System.out.println("Channel of type " + newChannel.getClass()
					+ " is successfully created and used for further operations");
			/* END - Helpful Prints */
			((PropositionNode) evaluatedReporter).addToOutgoingChannels(newChannel);
			addToIncomingChannels(newChannel);
			return newChannel;
		}
		/* BEGIN - Helpful Prints */
		System.out.println("Channel is already established and retrieved for further operations");
		/* END - Helpful Prints */
		return extractedChannel;

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
				System.out.println("\nReport instance:" + "\n~~~~\n" + report + "\n~~~~\n"
						+ "was successfuly sent from " + channel.getReporter().getIdentifier() + " to "
						+ channel.getRequester().getIdentifier() + "\n");
			}
		} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
			System.out.println("\nReport instance:" + "\n~~~~\n" + report + "\n~~~~\n" + "could not be sent from "
					+ channel.getReporter().getIdentifier() + " to " + channel.getRequester().getIdentifier() + "\n");
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
	 * Trying to send reports to all outgoing channels
	 * 
	 * @param report
	 */
	public void broadcastReports(ReportSet reports) {
		for (Report report : reports)
			broadcastReport(report);
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
	protected void sendReportToNodeSet(NodeSet ns, Report toBeSent, String contextName, ChannelTypes channelType) {
		for (Node sentTo : ns) {
			Substitutions reportSubs = toBeSent.getSubstitutions();
			Channel newChannel = establishChannel(channelType, sentTo, null, reportSubs, contextName, -1);
			sendReport(toBeSent, newChannel);
		}
		System.out.println("Sent report to " + ns.size() + " nodes");
	}

	protected void sendReportsToNodeSet(NodeSet ns, ReportSet toBeSent, String contextName, ChannelTypes channelType) {
		for (Report report : toBeSent)
			sendReportToNodeSet(ns, report, contextName, channelType);
		System.out.println("Sent reports to " + ns.size() + " nodes");
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
			sendReport(toBeSent, newChannel);
		}
		System.out.println("Sent report to " + list.size() + " matched nodes");
	}

	protected void sendReportsToMatches(List<Match> list, ReportSet reports, String contextId) {
		for (Report report : reports) {
			sendReportToMatches(list, report, contextId);
		}
		System.out.println("Sent reports to " + list.size() + " matched nodes");
	}

	protected void sendReportToChannelSet(ChannelSet filteredNodeSet, Report toBeSent) {
		for (Channel channel : filteredNodeSet) {
			sendReport(toBeSent, channel);
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
			PropositionNode matchedNode = (PropositionNode) currentMatch.getNode();
			Channel newChannel = establishChannel(ChannelTypes.MATCHED, currentMatch, switchSubs, filterSubs, contextId,
					matchType);
			matchedNode.receiveRequest(newChannel);
		}
		System.out.println("Sent requests to " + list.size() + " matched nodes");
	}

	/***
	 * Helper method responsible for establishing channels between this current node
	 * and each of the Matches to further request instances with the given inputs
	 * 
	 * @param list      List<Match> to be sent to
	 * @param contextID latest channel context
	 */
	protected void sendRequestsToNodeSet(NodeSet ns, Substitutions filterSubs, String contextID,
			ChannelTypes channelType) {
		for (Node sentTo : ns) {
			Channel newChannel = establishChannel(channelType, sentTo, null, filterSubs, contextID, -1);
			sentTo.receiveRequest(newChannel);
		}
		System.out.println("Sent requests to " + ns.size() + " dominating rule nodes");
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
		/* BEGIN - Helpful Prints */
		String nodeIdent = getIdentifier();
		System.out.println("PropositionNode " + nodeIdent + " just received a request.");
		/* END - Helpful Prints */
		Runner.addToLowQueue(this);
		channel.setRequestProcessed(true);
	}

	/***
	 * Reports received added to the high priority queue to be served accordingly
	 * through the runner.
	 */
	public void receiveReport(Channel channel) {
		/* BEGIN - Helpful Prints */
		String nodeIdent = getIdentifier();
		System.out.println("PropositionNode " + nodeIdent + " just received a report.");
		/* END - Helpful Prints */
		Runner.addToHighQueue(this);
		channel.setReportProcessed(true);
	}

	protected void getNodesToSendReport(ChannelTypes channelType, String currentContextName,
			Substitutions substitutions, boolean reportSign, InferenceTypes inferenceType) {
		try {
			PropositionSet supportPropSet = new PropositionSet();
			supportPropSet.add(getId());
			Substitutions substitutionsLinear = substitutions == null ? new LinearSubstitutions() : substitutions;
			Report toBeSent = new Report(substitutionsLinear, supportPropSet, reportSign, inferenceType);
			switch (channelType) {
			case MATCHED:
				List<Match> matchesReturned = Matcher.match(this, substitutions);
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
			Substitutions substitutions) {
		try {
			switch (channelType) {
			case MATCHED:
				List<Match> matchesReturned = Matcher.match(this, substitutions);
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
	 * Method comparing opened outgoing channels over each match's node of the
	 * matches whether a more generic request of the specified channel was
	 * previously sent in order not to re-send redundant requests -- ruleType gets
	 * applied on Andor or Thresh part.
	 * 
	 * @param matchingNodes
	 * @param currentChannel
	 * @param ruleType
	 * @return
	 */
	protected List<Match> removeAlreadyWorkingOn(List<Match> matchingNodes, Channel currentChannel) {
		List<Match> nodesToConsider = new ArrayList<Match>();
		for (Match sourceMatch : matchingNodes) {
			Node sourceNode = sourceMatch.getNode();
			if (sourceNode instanceof PropositionNode) {
				boolean conditionMet = true;
				Substitutions currentChannelFilterSubs = currentChannel.getFilter().getSubstitutions();
				ChannelSet outgoingChannels = ((PropositionNode) sourceNode).getOutgoingChannels();
				// ChannelSet filteredChannelsSet =
				// outgoingChannels.getFilteredRequestChannels(true);
				for (Channel outgoingChannel : outgoingChannels) {
					Substitutions processedChannelFilterSubs = outgoingChannel.getFilter().getSubstitutions();
					conditionMet &= !processedChannelFilterSubs.isSubSet(currentChannelFilterSubs)
							&& outgoingChannel.getRequester().getId() == currentChannel.getReporter().getId();
				}
				if (conditionMet)
					nodesToConsider.add(sourceMatch);
			}
		}
		return nodesToConsider;
	}

	/***
	 * Method comparing opened outgoing channels over each node of the nodes whether
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
		/* BEGIN - Helpful Prints */
		String nodesToBeFiltered = "[ ";
		String nodesToBeKept = "[ ";
		/* END - Helpful Prints */
		NodeSet nodesToConsider = new NodeSet();
		for (Node sourceNode : nodes)
			if (sourceNode instanceof PropositionNode) {
				/* BEGIN - Helpful Prints */
				nodesToBeFiltered += sourceNode.getIdentifier() + ", ";
				/* END - Helpful Prints */
				boolean conditionMet = !ruleType || sourceNode.getId() != channel.getRequester().getId();
				if (conditionMet) {
					ChannelSet outgoingChannels = ((PropositionNode) sourceNode).getOutgoingChannels();
//					ChannelSet filteredChannelsSet = outgoingChannels.getFilteredRequestChannels(true);
					for (Channel outgoingChannel : outgoingChannels) {
						Substitutions processedChannelFilterSubs = outgoingChannel.getFilter().getSubstitutions();
						conditionMet &= !processedChannelFilterSubs.isSubSet(toBeCompared)
								&& outgoingChannel.getRequester().getId() == channel.getReporter().getId();
					}
					if (conditionMet) {
						nodesToConsider.addNode(sourceNode);
						/* BEGIN - Helpful Prints */
						nodesToBeKept += sourceNode.getIdentifier() + ", ";
						/* END - Helpful Prints */
					}
				}
			}
		if (nodesToBeKept.length() > 2)
			nodesToBeKept = nodesToBeKept.substring(0, nodesToBeKept.length() - 2) + " ]";
		else
			nodesToBeKept += "]";
		if (nodesToBeFiltered.length() > 2)
			nodesToBeFiltered = nodesToBeFiltered.substring(0, nodesToBeFiltered.length() - 2) + " ]";
		else
			nodesToBeFiltered += "]";

		System.out.println("\n\u2022 Removing nodes with a request that is subset of " + toBeCompared.toString() + ":");
		System.out.println("Result: " + nodesToBeKept + " from " + nodesToBeFiltered + ".");
		/* END - Helpful Prints */
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

	public KnownInstances getKnownInstances() {
		return knownInstances;
	}

	public void setKnownInstances(KnownInstances knownInstances) {
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
		UpCable propCable = this.getUpCableSet().getUpCable("prop");
		if (argsCable != null) {
			ret.addAll(argsCable.getNodeSet());
		}
		if (consequentCable != null) {
			ret.addAll(consequentCable.getNodeSet());
		}
		if (propCable != null) {
			ret.addAll(propCable.getNodeSet());
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

	protected Collection<Channel> getOutgoingAntecedentRuleChannels() {
		return outgoingChannels.getAntRuleChannels();
	}

	protected Collection<Channel> getOutgoingRuleConsequentChannels() {
		return outgoingChannels.getRuleConsChannels();
	}

	protected Collection<Channel> getOutgoingMatchChannels() {
		return outgoingChannels.getMatchChannels();
	}

	public void addToOutgoingChannels(Channel channel) {
		outgoingChannels.addChannel(channel);
	}

	public void addToIncomingChannels(Channel channel) {
		incomingChannels.addChannel(channel);
	}

	protected Collection<Channel> getIncomingAntecedentRuleChannels() {
		return incomingChannels.getAntRuleChannels();
	}

	protected Collection<Channel> getIncomingRuleConsequentChannels() {
		return incomingChannels.getRuleConsChannels();
	}

	protected Collection<Channel> getIncomingMatchChannels() {
		return incomingChannels.getMatchChannels();
	}

	/***
	 * Checking if this node instance contains not yet bound free variables
	 * 
	 * @param filterSubs reference substitutions
	 * @return boolean computed from VariableNodeStats.areAllVariablesBound()
	 */
	public boolean isWhQuestion(Substitutions filterSubs) {
		VariableNodeStats currentNodeStats = computeNodeStats(filterSubs);
		/* BEGIN - Helpful Prints */
		System.out.println("\n\u2022 Testing if " + getIdentifier() + " is a Wh-Question:");
		System.out.println(currentNodeStats.toString());
		System.out.println("> Result: "
				+ (currentNodeStats.getNodeFreeVariables().size() > 0 && !currentNodeStats.areAllVariablesBound()));
		/* END - Helpful Prints */
		return currentNodeStats.getNodeFreeVariables().size() > 0 && !currentNodeStats.areAllVariablesBound();
	}

	/***
	 * Method computing an output of VariableNodeStats containing info about a
	 * certain node with variables by checking the input Substitutions and comparing
	 * them with the instance freeVariables, stating whether over a given
	 * substitutions the node will have all its freeVariables bound and also
	 * filtering the input substitutions to match the free variables (not including
	 * extra irrelevant filters)
	 * 
	 * @param filterSubs Substitutions the given substitutions on which bindings
	 *                   check will occur
	 * @return VariableNodeStats
	 */
	public VariableNodeStats computeNodeStats(Substitutions filterSubs) {
		VariableSet freeVariables = new VariableSet();
		if (term instanceof Open)
			freeVariables = ((Open) term).getFreeVariables();
		VariableNodeStats toBeReturned = filterSubs.extractBoundStatus(freeVariables);
		toBeReturned.setNodeId(getId());
		return toBeReturned;

	}

	private Report attemptAddingReportToKnownInstances(Channel channel, Report report) {
		Substitutions reportSubs = report.getSubstitutions();
		Set<Report> compatibleReports = knownInstances.getReportBySubstitutions(reportSubs);
		boolean channelCheck = channel instanceof MatchChannel || channel instanceof RuleToConsequentChannel;
		Report evaluatedReport;
		if (compatibleReports == null) {
			if (channelCheck)
				knownInstances.addReport(report);
			return report;
		}
		for (Report singleReport : compatibleReports) {
			evaluatedReport = singleReport.computeReportFromDifferencesToSend(report);
			if (evaluatedReport != null) {
				if (channelCheck)
					knownInstances.addReport(report);
				return evaluatedReport;
			}
		}
		return null;
	}

	protected PropositionNode buildNodeSubstitutions(Substitutions subs) {
		return null;
		// TODO nawar
	}

	public void deduce() {
		/* BEGIN - Helpful Prints */
		System.out.println("deduce() method initated.");
		System.out.println("-------------------------\n");
		/* END - Helpful Prints */
		Runner.initiate();
		String currentContextName = Controller.getCurrentContextName();
		/* BEGIN - Helpful Prints */
		System.out.println("\nSending to rule nodes during deduce()\n");
		/* END - Helpful Prints */
		getNodesToSendRequest(ChannelTypes.RuleCons, currentContextName, null);
		/* BEGIN - Helpful Prints */
		System.out.println("\nSending to matching nodes during deduce()\n");
		/* BEGIN - Helpful Prints */
		getNodesToSendRequest(ChannelTypes.MATCHED, currentContextName, null);
		// what to return here ?
		Runner.run();
		System.out.println(knownInstances.toString());
	}

	public void add() {
		/* BEGIN - Helpful Prints */
		System.out.println("add() method initated.\n");
		System.out.println("-------------------------");
		/* END - Helpful Prints */
		Runner.initiate();
		String currentContextName = Controller.getCurrentContextName();
		boolean reportSign = Controller.isNegated(this);
		/* BEGIN - Helpful Prints */
		System.out.println("\nSending to rule nodes during add()\n");
		/* END - Helpful Prints */
		getNodesToSendReport(ChannelTypes.RuleAnt, currentContextName, null, reportSign, InferenceTypes.FORWARD);
		/* BEGIN - Helpful Prints */
		System.out.println("\nSending to matching nodes during add()\n");
		/* END - Helpful Prints */
		getNodesToSendReport(ChannelTypes.MATCHED, currentContextName, null, reportSign, InferenceTypes.FORWARD);
		System.out.println(Runner.run());
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
		String currentContextName = currentChannel.getContextName();
		Context desiredContext = Controller.getContextByName(currentContextName);
		if (assertedInContext(desiredContext)) {
			// TODO change the subs to hashsubs
			int propNodeId = getId();
			PropositionSet supportPropSet = new PropositionSet();
			supportPropSet = supportPropSet.add(propNodeId);
			boolean reportSign = Controller.isNegated(this);
			Report reply = new Report(new LinearSubstitutions(), supportPropSet, reportSign, InferenceTypes.BACKWARD);
			sendReport(reply, currentChannel);
		} else {
			boolean sentAtLeastOne = false;
			for (Report currentReport : knownInstances)
				sentAtLeastOne |= sendReport(currentReport, currentChannel);
			Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
			boolean isWhQuestion = isWhQuestion(filterSubs);
			if (!sentAtLeastOne || isWhQuestion) {
				NodeSet dominatingRules = getUpConsNodeSet();
				NodeSet toBeSentToDom = removeAlreadyWorkingOn(dominatingRules, currentChannel, filterSubs, false);
				sendRequestsToNodeSet(toBeSentToDom, filterSubs, currentContextName, ChannelTypes.RuleAnt);
				if (!(currentChannel instanceof MatchChannel)) {
					List<Match> matchingNodes = Matcher.match(this, filterSubs);
					List<Match> toBeSentToMatch = removeAlreadyWorkingOn(matchingNodes, currentChannel);
					sendRequestsToMatches(toBeSentToMatch, currentContextName);
				}
			}
		}
	}

	public void processReports() {
		for (Channel inChannel : incomingChannels)
			try {
				processSingleReportsChannel(inChannel);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | DuplicatePropositionException
					| NodeNotFoundInPropSetException | CannotInsertJustificationSupportException e) {
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
	 * @throws CannotInsertJustificationSupportException
	 * @throws NodeNotFoundInPropSetException
	 */
	protected void processSingleReportsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException,
			NodeNotFoundInPropSetException, CannotInsertJustificationSupportException {
		ReportSet reports = currentChannel.getReportsBuffer();
		String currentChannelContextName = currentChannel.getContextName();
		for (Report currentReport : reports) {
			Report reportToBeBroadcasted = attemptAddingReportToKnownInstances(currentChannel, currentReport);
			if (reportToBeBroadcasted != null) {
				boolean forwardReportType = reportToBeBroadcasted.getInferenceType() == InferenceTypes.FORWARD;
				if (currentChannel instanceof RuleToConsequentChannel) {
					PropositionNode supportNode = buildNodeSubstitutions(currentReport.getSubstitutions());
					if (supportNode != null) {
						supportNode.addJustificationBasedSupport(reportToBeBroadcasted.getSupport());
						PropositionSet reportSupportPropSet = new PropositionSet();
						reportSupportPropSet.add(supportNode.getId());
						reportToBeBroadcasted.setSupport(reportSupportPropSet);
					}
				}
				// TODO: GRADED PROPOSITIONS HANDLING REPORTS
				if (forwardReportType) {
					if (currentChannel instanceof MatchChannel) {
						List<Match> matchesReturned = Matcher.match(this);
						sendReportToMatches(matchesReturned, currentReport, currentChannelContextName);
					}
					NodeSet dominatingRules = getUpAntNodeSet();
					sendReportToNodeSet(dominatingRules, currentReport, currentChannelContextName,
							ChannelTypes.RuleAnt);
				} else
					broadcastReport(reportToBeBroadcasted);

			}
			if (!(this instanceof RuleNode))
				currentChannel.getReportsBuffer().removeReport(currentReport);
		}
	}

}