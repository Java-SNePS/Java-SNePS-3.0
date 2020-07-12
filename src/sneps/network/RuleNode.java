package sneps.network;

import java.io.Serializable;
import java.util.ArrayList;
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
	private static final long serialVersionUID = 3891988384679269734L;

		protected NodeSet consequents;
		protected NodeSet antecedents;

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
	 * Set to true if all the antecedents with variables share the same
	 * variables, false otherwise.
	 */
	protected boolean shareVars;

	/**
	 * A VarNodeSet of the common free VariableNodes shared between the antecedents.
	 */
	protected Set<VariableNode> sharedVars;

	/**
	 * A ContextRuisSet that is used to map each context to its appropriate
	 * RuiHandler for this RuleNode.
	 */
	//protected ContextRuisSet contextRuisSet;

	private Hashtable<Integer, RuleUseInfo> contextConstantRUI;

	/**
	 * A RuisHandler that is used to keep track of all the RUIs for this RuleNode.
	 */
	protected RuisHandler ruisHandler;

	/**
	 * A single RUI that contains all the constant instances found that do not
	 * dominate variables for this RuleNode.
	 */
	protected RuleUseInfo constantRUI;

	/**
	 * Used for testing.
	 */
	protected ArrayList<Report> reportsToBeSent;

	public RuleNode() {
		consequents = new NodeSet();
		antecedents = new NodeSet();
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		shareVars = false;
		sharedVars = new HashSet<VariableNode>();
		reportsToBeSent = new ArrayList<Report>();
	}

	public RuleNode(Molecular syn) {
		super(syn);
		consequents = new NodeSet();
		antecedents = new NodeSet();
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		shareVars = false;
		sharedVars = new HashSet<VariableNode>();
		reportsToBeSent = new ArrayList<Report>();
	}

	public RuisHandler getRuisHandler() {
		return ruisHandler;
	}

	public int getAntSize(){
		return antNodesWithoutVars.size() + antNodesWithVars.size();
	}

	public NodeSet getAntsWithoutVars() {
		return antNodesWithoutVars;
	}

	public NodeSet getAntsWithVars() {
		return antNodesWithVars;
	}

	public NodeSet getAntecedents() {
		return antecedents;
	}

	public void setAntecedents(NodeSet antecedents) {
		this.antecedents = antecedents;
	}

	public NodeSet getConsequents() {
		return consequents;
	}

	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}

	public ArrayList<Report> getReplies() {
		return reportsToBeSent;
	}

	/*protected void sendReportToConsequents(Report reply) {
		if(!knownInstances.contains(reply))
			newInstances.addReport(reply);
		for (Channel outChannel : outgoingChannels)
			if(outChannel instanceof RuleToConsequentChannel)
				outChannel.addReport(reply);
	}*/

	/**
		 * Process antecedent nodes, used for initialization.
		 *
		 * @param antNodes
		 */

		 public void processNodes(NodeSet antNodes) {
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
	 		sharedVars = getSharedVarsNodes(antNodesWithVars);
	 	}

/**
* The main method that does all the inference process in the RuleNode. Creates
* a RUI for the given report, and inserts it into the appropriate RuisHandler
* for this RuleNode. It instantiates a RuisHandler if this is the first report
* from a pattern antecedent received. It then applies the inference rules of this
* RuleNode on the current stored RUIs.
*
* @param report
* @param signature
* 		The instance that is being reported by the report.
* @return
*/
public ArrayList<RuleResponse> applyRuleHandler(Report report, Node signature) {
//System.out.println("---------------------");
ArrayList<RuleResponse> responseList = new ArrayList<RuleResponse>();
ArrayList<RuleResponse> response = new ArrayList<RuleResponse>();

RuleUseInfo rui;
PropositionSet propSet = report.getSupport();
FlagNodeSet fns = new FlagNodeSet();

if (report.isPositive()) {
	fns.insert(new FlagNode(signature, propSet, 1));
	rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns,
			report.getInferenceType());
} else {
	fns.insert(new FlagNode(signature, propSet, 2));
	rui = new RuleUseInfo(report.getSubstitutions(), 0, 1, fns,
			report.getInferenceType());
}

//System.out.println(rui);

if(antNodesWithoutVars.contains(signature)) {
	addConstantRui(rui);
	if (ruisHandler == null) {
		response = applyRuleOnRui(constantRUI);
		if(response != null)
			responseList.addAll(response);
	}
	else {
		RuleUseInfoSet combined  = ruisHandler.combineConstantRUI(constantRUI);
		for (RuleUseInfo tRui : combined) {
			response = applyRuleOnRui(tRui);
			if(response != null)
				responseList.addAll(response);
		}
	}
}
else {
	// This is the first report received from a pattern antecedent, so a
	// ruisHandler is created
	if(ruisHandler == null)
		ruisHandler = addRuiHandler();

	// The RUI created for the given report is inserted to the RuisHandler
	RuleUseInfoSet res = ruisHandler.insertRUI(rui);

	if(constantRUI != null) {
		RuleUseInfo combined;
		for (RuleUseInfo tRui : res) {
			combined = tRui.combine(constantRUI);
			if(combined != null) {
				response = applyRuleOnRui(combined);
				if(response != null)
					responseList.addAll(response);
			}
		}
	}
	else {
		for (RuleUseInfo tRui : res) {
			response = applyRuleOnRui(tRui);
			if(response != null)
				responseList.addAll(response);
		}
	}
}

		if(responseList.isEmpty())
			return null;

		return responseList;
	}

	abstract protected ArrayList<RuleResponse> applyRuleOnRui(RuleUseInfo tRui);

/**
*
* Clears all the information saved by this RuleNode about the instances received.
*/
public void clear() {
	if(ruisHandler != null)
		ruisHandler.clear();
	constantRUI = null;
	reportsToBeSent.clear();
}

/**
	 * Returns true if all the nodes in the given NodeSet share the same set of
	 * VariableNodes, and false otherwise.
	 *
	 * @param nodes
	 * @return boolean
	 */
	public boolean allShareVars(NodeSet nodes) {
		if (nodes.isEmpty())
			return false;

		Node n = nodes.getNode(0);
		for (int i = 1; i < nodes.size(); i++) {
			if (!(n.hasSameFreeVariablesAs(nodes.getNode(i)))) {
				return false;
			}
		}

		return true;
	}

	public void addAntecedent(Node ant) {
		if(ant instanceof VariableNode || ant.getTerm() instanceof Open)
			antNodesWithVars.addNode(ant);
		else
			antNodesWithoutVars.addNode(ant);
	}

	/**
	 * Returns a VarNodeSet of VariableNodes that are shared among all the Nodes in
	 * the given NodeSet.
	 *
	 * @param nodes
	 * @return VarNodeSet
	 */
	public Set<VariableNode> getSharedVarsNodes(NodeSet nodes) {
		Set<VariableNode> res = new HashSet<VariableNode>();

		if (nodes.isEmpty())
			return res;

		if(nodes.getNode(0) instanceof VariableNode)
			res.add((VariableNode) nodes.getNode(0));
		else if(nodes.getNode(0).getTerm() instanceof Open) {
			VarNodeSet freeVars = ((Open) nodes.getNode(0).getTerm()).getFreeVariables();
			for(VariableNode v : freeVars)
				res.add(v);
		}

		if(nodes.size() == 1)
			return res;

		for(int i = 1; i < nodes.size(); i++) {
			Set<VariableNode> vars = new HashSet<VariableNode>();
			if(nodes.getNode(i) instanceof VariableNode)
				vars.add((VariableNode) nodes.getNode(i));
			else if(nodes.getNode(i).getTerm() instanceof Open) {
				VarNodeSet freeVars = ((Open) nodes.getNode(i).getTerm()).getFreeVariables();
				for(VariableNode v : freeVars)
					vars.add(v);
			}

			res.retainAll(vars);
		}

		return res;
		}

		/**
	 * Returns a NodeSet of the antecedents down cable set, and in case of AndOr or
	 * Thresh, returns the arguments down cable set.
	 *
	 * @return NodeSet
	 */
	 public NodeSet getDownNodeSet(String name) {
 			if(term != null && term instanceof Molecular &&
 					((Molecular) term).getDownCableSet().getDownCable(name) != null)
 				return ((Molecular)term).getDownCableSet().getDownCable(name).getNodeSet();
 			return null;
 		}

		/**
			 * Returns a NodeSet of the antecedents down cable set, and in case of AndOr or
			 * Thresh, returns the arguments down cable set.
			 *
			 * @return NodeSet
			 */
	public abstract NodeSet getDownAntNodeSet();

	public abstract NodeSet getDownConsqNodeSet();



	public NodeSet getUpNodeSet(String name) {
		return this.getUpCableSet().getUpCable(name).getNodeSet();
	}

	/**
		 * Creates an appropriate RuisHandler for this RuleNode, according to whether all,
		 * some or none of the variables in the antecedents are shared.
		 *
		 * @return RuisHandler
		 */
	public RuisHandler addRuiHandler() {
			if (sharedVars.size() != 0) {
				SIndex si = null;
				// Antecedents with variables share the same set of variables
				if (shareVars)
					si = new SIndex(SIndex.SINGLETON, sharedVars, antNodesWithVars);
				// Antecedents share some but not all variables
				else
					si = new SIndex(getSIndexType(), sharedVars, antNodesWithVars);

				return si;
			}

			else {
				// PTree in case of and-entailment
				// RUISet otherwise
				return createRuisHandler();
			}
		}

		protected abstract RuisHandler createRuisHandler();

		/**
			 * Returns a byte that represents an appropriate SIndex type that is used in case
			 * the antecedents share some but not all variables. </br></br>
			 * <b>SIndex.PTree: </b> in AndEntailment </br>
			 * <b>SIndex.RUIS: </b> in other rule nodes
			 * @return byte
			 */
			protected abstract byte getSIndexType();

			public void splitToNodesWithVarsAndWithout(NodeSet allNodes, NodeSet withVars, NodeSet WithoutVars) {
				for (int i = 0; i < allNodes.size(); i++) {
					Node n = allNodes.getNode(i);
					addAntecedent(n);
				}
			}

			public RuleUseInfo addConstantRui(RuleUseInfo rui) {
				if (constantRUI != null)
					constantRUI = rui.combine(constantRUI);
				else
					constantRUI = rui;
				if (constantRUI == null)
					throw new NullPointerException(
							"The existed RUI could not be merged " +
					"with the given rui so check your code again");
				return constantRUI;
			}

			public RuleUseInfo getConstantRui(Context con) {
				RuleUseInfo tRui = contextConstantRUI.get(con.getName());
				return tRui;
			}

			/**
			 * This method returns all the rule to consequent channels corresponding to a
			 * given report. The send method filters which reports should actually be sent.
			 * @param r
			 *     Report
			 * @return
			 * 	   Set<Channel>
			 */
			protected Set<Channel> getOutgoingChannelsForReport(Report r) {
				// getOutgoingRuleConsequentChannels() returns all the RuleToConsequent
				// channels already established from before
				Set<Channel> outgoingChannels = getOutgoingRuleConsequentChannels();
				Set<Channel> replyChannels = new HashSet<Channel>();
				for(Node n : consequents) {
					if(outgoingChannels != null) {
						// Checking that the same channel has not already been established before
						for(Channel c : outgoingChannels) {
							if(c.getRequester().getId() == n.getId() &&
									r.getSubstitutions().isSubSet(c.getFilter().getSubstitution())) {
								replyChannels.add(c);
								break;
							}
						}
					}

					Channel ch = establishChannel(ChannelTypes.RuleCons, n,
							new LinearSubstitutions(), (LinearSubstitutions)
							r.getSubstitutions(), Controller.getCurrentContext(), -1);
					replyChannels.add(ch);
				}

				return replyChannels;
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
				} else {
					super.processSingleRequestsChannel(currentChannel);
					return;
				}
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
				return;
			}
		} else
			super.processSingleRequestsChannel(currentChannel);
	}

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
			VariableNodeStats ruleNodeStats = computeNodeStats(currentReportSubs);
			Substitutions ruleNodeExtractedSubs = ruleNodeStats.getVariableNodeSubs();
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
									ruleNodeExtractedSubs, false);
							sendRequestsToNodeSet(toBeSentToDom, ruleNodeExtractedSubs, currentChannelContextName,
									ChannelTypes.RuleAnt);
							List<Match> matchingNodes = Matcher.match(this, ruleNodeExtractedSubs);
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
