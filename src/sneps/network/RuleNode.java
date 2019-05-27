package sneps.network;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.ContextRuisSet;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Filter;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.KnownInstances;
import sneps.snip.Runner;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.MatchChannel;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.classes.VariableNodeStats;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.matching.Match;
import sneps.snip.matching.Matcher;
import sneps.snip.matching.Substitutions;
import sneps.snip.rules.AndOrNode;
import sneps.snip.rules.ThreshNode;

public abstract class RuleNode extends PropositionNode implements Serializable {

	/**
	 * a NodeSet containing all the pattern antecedents attached to this Node
	 */
	protected NodeSet antNodesWithVars;

	/**
	 * a NodeSet containing all the non pattern antecedents attached to this Node
	 */
	protected NodeSet antNodesWithoutVars;

	/**
	 * an integer set containing all the ids of the pattern antecedents attached to
	 * this Node
	 */
	protected Set<Integer> antNodesWithVarsIDs;

	/**
	 * an integer set containing all the ids of the non pattern antecedents attached
	 * to this Node
	 */
	protected Set<Integer> antNodesWithoutVarsIDs;

	/**
	 * set to true if all the antecedents with Variables share the same variables,
	 * false otherwise.
	 */
	protected boolean shareVars;

	/**
	 * Set of ids of the variables shared by all patterns
	 */
	protected Set<Integer> sharedVars;

	protected ContextRuisSet contextRuisSet;

	private Hashtable<Integer, RuleUseInfo> contextConstantRUI;

	public RuleNode() {
	}

	public RuleNode(Term syn) {
		super(syn);
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		contextRuisSet = new ContextRuisSet();
		contextConstantRUI = new Hashtable<Integer, RuleUseInfo>();
	}

	protected void processNodes(NodeSet antNodes) {
		this.splitToNodesWithVarsAndWithout(antNodes, antNodesWithVars, antNodesWithoutVars);
		for (Node n : antNodesWithVars) {
			antNodesWithVarsIDs.add(n.getId());
		}
		for (Node n : antNodesWithoutVars) {
			antNodesWithoutVarsIDs.add(n.getId());
		}
		// this.antNodesWithoutVars.size();
		// this.antNodesWithVars.size();
		this.shareVars = this.allShareVars(antNodesWithVars);
		sharedVars = getSharedVarsInts(antNodesWithVars);
	}

	public Collection<RuleResponse> applyRuleHandler(Report report, Channel currentChannel) {
		Node currentChannelReporter = currentChannel.getReporter();
		String contextID = currentChannel.getContextName();
		// Context context = SNeBR.getContextByID(contextID);
		RuleUseInfo rui;
		if (report.isPositive()) {
			FlagNode fn = new FlagNode(currentChannelReporter, report.getSupport(), 1);
			FlagNodeSet fns = new FlagNodeSet();
			fns.putIn(fn);
			rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns);
		} else {
			FlagNode fn = new FlagNode(currentChannelReporter, report.getSupport(), 2);
			FlagNodeSet fns = new FlagNodeSet();
			fns.putIn(fn);
			rui = new RuleUseInfo(report.getSubstitutions(), 0, 1, fns);
		}
		RuleUseInfoSet crtemp = null;
		if (this.getContextRUISSet().hasContext(contextID)) {
			crtemp = this.getContextRUISSet().getContextRUIS(contextID);
		} else {
			crtemp = addContextRUIS(contextID);
		}
		RuleUseInfoSet res = crtemp.add(rui);
		if (res == null)
			res = new RuleUseInfoSet();
		for (RuleUseInfo tRui : res) {
			sendRui(tRui, contextID);
		}
		return null;
	}

	abstract protected void sendRui(RuleUseInfo tRui, String contextID);

	public void clear() {
		contextRuisSet.clear();
		contextConstantRUI.clear();
	}

	public boolean allShareVars(NodeSet nodes) {
		if (nodes.isEmpty())
			return false;

		VariableNode n = (VariableNode) nodes.getNode(0);
		boolean res = true;
		for (int i = 1; i < nodes.size(); i++) {
			if (!n.hasSameFreeVariablesAs((VariableNode) nodes.getNode(i))) {
				res = false;
				break;
			}
		}
		return res;
	}

	public Set<VariableNode> getSharedVarsNodes(NodeSet nodes) {
		/*
		 * if (nodes.isEmpty()) return new HashSet<VariableNode>(); VariableNode n =
		 * (VariableNode) nodes.getNode(0); Set<VariableNode> res =
		 * ImmutableSet.copyOf(n.getFreeVariables()); for (int i = 1; i < nodes.size();
		 * i++) { n = (VariableNode) nodes.getNode(i); Set<VariableNode> temp =
		 * ImmutableSet.copyOf(n.getFreeVariables()); res = Sets.intersection(res,
		 * temp); } return res;
		 */
		return null;
	}

	public Set<Integer> getSharedVarsInts(NodeSet nodes) {
		Set<VariableNode> vars = getSharedVarsNodes(nodes);
		Set<Integer> res = new HashSet<Integer>();
		for (VariableNode var : vars)
			res.add(var.getId());
		return res;
	}

	public NodeSet getDownNodeSet(String name) {
		return ((Molecular) term).getDownCableSet().getDownCable(name).getNodeSet();
	}

	public abstract NodeSet getDownAntNodeSet();

	public NodeSet getUpNodeSet(String name) {
		return this.getUpCableSet().getUpCable(name).getNodeSet();
	}

	public ContextRuisSet getContextRUISSet() {
		return contextRuisSet;
	}

	public RuleUseInfoSet addContextRUIS(String contextName) {
		if (sharedVars.size() != 0) {
			SIndex si = null;
			if (shareVars)
				si = new SIndex(contextName, sharedVars, SIndex.SINGLETONRUIS, getPatternNodes());
			else
				si = new SIndex(contextName, sharedVars, getSIndexContextType(), getParentNodes());
			return this.addContextRUIS(si);
		} else {
			return this.addContextRUIS(createContextRUISNonShared(contextName));
		}
	}

	private RuleUseInfoSet addContextRUIS(SIndex si) {
		// TODO Auto-generated method stub
		return null;
	}

	public RuleUseInfoSet addContextRUIS(RuleUseInfoSet cRuis) {
		contextRuisSet.putIn(cRuis);
		return cRuis;
	}

	protected RuleUseInfoSet createContextRUISNonShared(String contextName) {
		return new RuleUseInfoSet(contextName, false);
	}

	protected byte getSIndexContextType() {
		return SIndex.RUIS;
	}

	protected NodeSet getPatternNodes() {
		return antNodesWithVars;
	}

	public void splitToNodesWithVarsAndWithout(NodeSet allNodes, NodeSet withVars, NodeSet WithoutVars) {
		for (int i = 0; i < allNodes.size(); i++) {
			Node n = allNodes.getNode(i);
			if (isConstantNode(n))
				WithoutVars.addNode(n);
			else
				withVars.addNode(n);
		}
	}

	public RuleUseInfo addConstantRuiToContext(int context, RuleUseInfo rui) {
		RuleUseInfo tRui = contextConstantRUI.get(context);
		if (tRui != null)
			tRui = rui.combine(tRui);
		else
			tRui = rui;
		if (tRui == null)
			throw new NullPointerException(
					"The existed RUI could not be merged " + "with the given rui so check your code again");
		contextConstantRUI.put(context, tRui);
		return tRui;
	}

	public RuleUseInfo getConstantRui(Context con) {
		RuleUseInfo tRui = contextConstantRUI.get(con.getName());
		return tRui;
	}

	public RuleUseInfo getConstantRUI(int context) {
		return contextConstantRUI.get(context);
	}

	public static boolean isConstantNode(Node n) {
		return !(n instanceof VariableNode) || n instanceof RuleNode || ((VariableNode) n).getFreeVariables().isEmpty();
	}

	protected void requestAntecedentsNotAlreadyWorkingOn(Channel currentChannel, boolean removeSender) {
		NodeSet antecedentsNodeSet = getDownAntNodeSet();
		if (removeSender)
			antecedentsNodeSet.removeNode(currentChannel.getRequester());
		boolean ruleType = this instanceof ThreshNode || this instanceof AndOrNode;
		String currentContextName = currentChannel.getContextName();
		Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
		NodeSet toBeSentTo = removeAlreadyWorkingOn(antecedentsNodeSet, currentChannel, filterSubs, ruleType);
		sendRequestsToNodeSet(toBeSentTo, filterSubs, currentContextName, ChannelTypes.RuleAnt);
	}

	/***
	 * Sending requests with a union substitutions between the original request and
	 * the report to all Antecedents not already working on that type of request.
	 * 
	 * @param currentChannel
	 * @param report
	 */
	protected void requestAntecedentsNotAlreadyWorkingOn(Channel currentChannel, Report report) {
		NodeSet antecedentNodeSet = getDownAntNodeSet();
		boolean ruleType = this instanceof ThreshNode || this instanceof AndOrNode;
		String currentContextName = currentChannel.getContextName();
		Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
		Substitutions reportSubs = report.getSubstitutions();
		Substitutions unionSubs = filterSubs.union(reportSubs);
		NodeSet toBeSentTo = removeAlreadyWorkingOn(antecedentNodeSet, currentChannel, unionSubs, ruleType);
		sendRequestsToNodeSet(toBeSentTo, unionSubs, currentContextName, ChannelTypes.RuleAnt);
	}

	protected void requestAntecedentsNotAlreadyWorkingOn(Channel currentChannel, Report report, boolean removeSender) {
		NodeSet antecedentNodeSet = getDownAntNodeSet();
		boolean ruleType = this instanceof ThreshNode || this instanceof AndOrNode;
		String currentContextName = currentChannel.getContextName();
		Substitutions reportSubs = report.getSubstitutions();
		NodeSet toBeSentTo = removeAlreadyWorkingOn(antecedentNodeSet, currentChannel, reportSubs, ruleType);
		sendRequestsToNodeSet(toBeSentTo, reportSubs, currentContextName, ChannelTypes.RuleAnt);
	}

	/*
	 * Check error in Context public boolean anySupportAssertedInContext(Report
	 * report) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
	 * String reportContextName = report.getContextName(); Set<Support>
	 * reportSupports = report.getSupports(); for (Support support : reportSupports)
	 * { int supportId = support.getId(); PropositionNode supportNode =
	 * (PropositionNode) Network.getNodeById(supportId); if
	 * (supportNode.assertedInContext(reportContextName)) return true; } return
	 * false; }
	 */

	public void handleResponseOfApplyRuleHandler(Collection<RuleResponse> ruleResponses, Report currentReport,
			Channel currentChannel) {
		for (RuleResponse ruleResponse : ruleResponses) {
			if (ruleResponse != null) {
				Report reportToBeSent = ruleResponse.getReport();
//				if (reportToBeSent.getInferenceType() == InferenceTypes.FORWARD) {
				Collection<Channel> consequentsChannels = ruleResponse.getConsequentChannels();
				for (Channel consequentChannel : consequentsChannels) {
					sendReport(reportToBeSent, consequentChannel);
				}
//				}
				/*
				 * OLD
				 * 
				 * broadcastReport(reportToBeSent); if (reportToBeSent.getInferenceType() ==
				 * InferenceTypes.FORWARD) { ChannelSet consequents =
				 * ruleResponse.getConsequentChannels(); ChannelSet filteredNodeSet =
				 * removeExistingOutgoingChannelsFromSet(consequents);
				 * sendReportToChannelSet(filteredNodeSet, reportToBeSent); }
				 */
			} else if (currentReport.getInferenceType() == InferenceTypes.FORWARD)
				requestAntecedentsNotAlreadyWorkingOn(currentChannel, true);
		}

	}

	private NodeSet removeExistingNodesOutgoingChannels(NodeSet nodeSet) {
		ChannelSet outgoingChannels = getOutgoingChannels();
		Collection<Channel> channels = outgoingChannels.getChannels();
		for (Node node : nodeSet)
			for (Channel channel : channels) {
				Node channelRequester = channel.getRequester();
				if (node.equals(channelRequester))
					nodeSet.removeNode(node);
			}
		return nodeSet;
	}

	/***
	 * Method filtering given ChannelSet to remove previously existing channels to
	 * avoid redundancy
	 * 
	 * @param channelSet
	 * @return
	 */
	private Collection<Channel> removeExistingOutgoingChannelsFromSet(Collection<Channel> channelSet) {
		ChannelSet outgoingChannels = getOutgoingChannels();
		for (Channel inputChannel : channelSet)
			for (Channel channel : outgoingChannels) {
				if (inputChannel.equals(channel))
					channelSet.remove(inputChannel);
			}
		return channelSet;
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
	 * Request handling in Rule proposition nodes.
	 * 
	 * @param currentChannel
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws DuplicatePropositionException
	 */
	protected void processSingleRequestsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		if (currentChannel instanceof RuleToConsequentChannel) {
			boolean closedTypeTerm = term instanceof Closed;
			String currentContextName = currentChannel.getContextName();
			Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
			if (closedTypeTerm) {
				/* Case 1 */
				if (assertedInContext(currentContextName)) {
					requestAntecedentsNotAlreadyWorkingOn(currentChannel, false);
				} else
					super.processSingleRequestsChannel(currentChannel);
			} else {
				VariableNodeStats ruleNodeStats = computeNodeStats(filterSubs);
				boolean ruleNodeAllVariablesBound = ruleNodeStats.areAllVariablesBound();
				Substitutions ruleNodeExtractedSubs = ruleNodeStats.getVariableNodeSubs();
				/* Case 2 & 3 */
				KnownInstances knownReportSet = knownInstances;
				for (Report report : knownReportSet) {
					Substitutions reportSubstitutions = report.getSubstitutions();
					boolean subSetCheck = ruleNodeExtractedSubs.isSubSet(reportSubstitutions);
					boolean supportCheck = report.anySupportAssertedInContext(currentContextName);
					if (subSetCheck && supportCheck) {
						if (ruleNodeAllVariablesBound) {
							requestAntecedentsNotAlreadyWorkingOn(currentChannel, false);
							return;
						} else
							requestAntecedentsNotAlreadyWorkingOn(currentChannel, report);
					}
				}
				/* TODO instead of calling super we know it's the case of isWhQuestion */
				super.processSingleRequestsChannel(currentChannel);
			}
		} else
			super.processSingleRequestsChannel(currentChannel);
	}

	// PROCESS REPORT : 3adi -> outgoing channels node we ab3at accordingly, forard
	// -> outgoing channels and the rest of the consequents kolohom we ab3at 3adi

	/***
	 * Report handling in Rule proposition nodes.
	 */
	public void processReports() {
		for (Channel currentChannel : incomingChannels)
			try {
				processSingleReportsChannel(currentChannel);
			} catch (NotAPropositionNodeException | NodeNotFoundInNetworkException | DuplicatePropositionException
					| NodeNotFoundInPropSetException | CannotInsertJustificationSupportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	protected void processSingleReportsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException,
			NodeNotFoundInPropSetException, CannotInsertJustificationSupportException {
		ReportSet channelReports = currentChannel.getReportsBuffer();
		String currentChannelContextName = currentChannel.getContextName();
		boolean assertedInContext = assertedInContext(currentChannelContextName);
		boolean closedTypeTerm = term instanceof Closed;
		ReportSet currentChannelReportBuffer = currentChannel.getReportsBuffer();
		for (Report currentReport : channelReports) {
			boolean forwardReportType = currentReport.getInferenceType() == InferenceTypes.FORWARD;
			Substitutions currentReportSubs = currentReport.getSubstitutions();
			if (currentChannel instanceof AntecedentToRuleChannel) {
				/** AntecedentToRule Channel */
				if (forwardReportType) {
					/** Forward Inference */
					if (closedTypeTerm) {
						/** Close Type Implementation */
						if (assertedInContext) {
							requestAntecedentsNotAlreadyWorkingOn(currentChannel, false);
							Collection<RuleResponse> ruleResponse = applyRuleHandler(currentReport, currentChannel);
							handleResponseOfApplyRuleHandler(ruleResponse, currentReport, currentChannel);
							currentChannelReportBuffer.removeReport(currentReport);
						} else {
							NodeSet dominatingRules = getUpConsNodeSet();
							NodeSet toBeSentToDom = removeAlreadyWorkingOn(dominatingRules, currentChannel,
									currentReportSubs, false);
							sendRequestsToNodeSet(toBeSentToDom, currentReportSubs, currentChannelContextName,
									ChannelTypes.RuleAnt);
							List<Match> matchingNodes = Matcher.match(this, currentReportSubs);
							List<Match> toBeSentToMatch = removeAlreadyWorkingOn(matchingNodes, currentChannel);
							sendRequestsToMatches(toBeSentToMatch, currentChannelContextName);
						}
					} else {
						/** Open Type Implementation */
						/*
						 * for every known instance compatible (el free variables el fel rule hntala3 el
						 * bindings beta3thom fel report we necheck law dah subset men had men el known
						 * instance ; use variablenodestats) with the report, if(zay belzabt el check
						 * beta3 el subset wel support el fe requests)send requests to the rest of the
						 * antecedents gheir el ba3at el report and apply rule handler and not remove
						 * the report from the reports buffer, try to assert
						 */
						/*
						 * le kol known instance hanla2ih hnt3amel ma3 el node ka2enaha asserted we
						 * closed
						 */

						/* always sue the extracted report subs in the requests */
						VariableNodeStats ruleNodeStats = computeNodeStats(currentReportSubs);
						Substitutions ruleNodeExtractedSubs = ruleNodeStats.getVariableNodeSubs();
						for (Report knownInstance : knownInstances) {
							Substitutions knownInstanceSubstitutions = knownInstance.getSubstitutions();
							boolean subSetCheck = ruleNodeExtractedSubs.isSubSet(knownInstanceSubstitutions);
							boolean supportCheck = knownInstance.anySupportAssertedInContext(currentChannelContextName);
							if (subSetCheck && supportCheck) {
								requestAntecedentsNotAlreadyWorkingOn(currentChannel, knownInstance, true);
								Collection<RuleResponse> ruleResponse = applyRuleHandler(knownInstance, currentChannel);
								handleResponseOfApplyRuleHandler(ruleResponse, knownInstance, currentChannel);
							}
						}
						Collection<RuleResponse> ruleResponse = applyRuleHandler(currentReport, currentChannel);
						handleResponseOfApplyRuleHandler(ruleResponse, currentReport, currentChannel);
						NodeSet dominatingRules = getUpConsNodeSet();
						NodeSet toBeSentToDom = removeAlreadyWorkingOn(dominatingRules, currentChannel,
								currentReportSubs, false);
						sendRequestsToNodeSet(toBeSentToDom, ruleNodeExtractedSubs, currentChannelContextName,
								ChannelTypes.RuleAnt);
						List<Match> matchingNodes = Matcher.match(this, ruleNodeExtractedSubs);
						List<Match> toBeSentToMatch = removeAlreadyWorkingOn(matchingNodes, currentChannel);
						sendRequestsToMatches(toBeSentToMatch, currentChannelContextName);
						/*
						 * zeyada 3aleiha hnkamel akenaha mesh asserted el heya open
						 */
					}
				} else {
					/** Backward Inference */
					Collection<RuleResponse> ruleResponse = applyRuleHandler(currentReport, currentChannel);
					handleResponseOfApplyRuleHandler(ruleResponse, currentReport, currentChannel);
					currentChannelReportBuffer.removeReport(currentReport);
				}
			} else {
				/** Not AntecedentToRule Channel */
				super.processSingleReportsChannel(currentChannel);
				if (forwardReportType) {
					if (closedTypeTerm)
						Runner.addNodeAssertionThroughFReport(currentReport, this);
					getNodesToSendRequest(ChannelTypes.RuleAnt, currentChannelContextName, currentReportSubs);
				} else {
					Collection<Channel> outgoingChannels = getOutgoingChannels().getChannels();
					Collection<Channel> incomingChannels = getIncomingChannels().getAntRuleChannels();
					boolean existsForwardReportBuffers = false;
					for (Channel incomingChannel : incomingChannels) {
						existsForwardReportBuffers |= !incomingChannel.getReportsBuffer().hasForwardReports();
					}
					if (!outgoingChannels.isEmpty())
						receiveRequest(currentChannel);
					if (!incomingChannels.isEmpty() && existsForwardReportBuffers)
						receiveReport(currentChannel);
					currentChannelReportBuffer.removeReport(currentReport);
				}
			}
		}
	}

}
