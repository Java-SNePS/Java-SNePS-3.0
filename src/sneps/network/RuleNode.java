package sneps.network;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.Semantic;
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
import sneps.snip.Report;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.MatchChannel;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.classes.VariableNodeStats;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
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

	public void applyRuleHandler(Report report, Node signature) {
		String contextID = report.getContextName();
		// Context context = SNeBR.getContextByID(contextID);
		RuleUseInfo rui;
		if (report.isPositive()) {
			FlagNode fn = new FlagNode(signature, report.getSupports(), 1);
			FlagNodeSet fns = new FlagNodeSet();
			fns.putIn(fn);
			rui = new RuleUseInfo(report.getSubstitutions(), 1, 0, fns);
		} else {
			FlagNode fn = new FlagNode(signature, report.getSupports(), 2);
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

	@Override
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

	public void requestAntecedentsNotAlreadyWorkingOn(Channel currentChannel) {
		NodeSet antecedentNodeSet = getDownAntNodeSet();
		boolean ruleType = this instanceof ThreshNode || this instanceof AndOrNode;
		NodeSet toBeSentTo = alreadyWorking(antecedentNodeSet, currentChannel, ruleType);
		sendRequests(toBeSentTo, currentChannel.getFilter().getSubstitutions(), currentChannel.getContextName(),
				ChannelTypes.RuleAnt, currentChannel.getInferenceType());
	}

	/* Check error in COntext */
//	public boolean anySupportAssertedInContext(Report report)
//			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
//		String reportContextName = report.getContextName();
//		Set<Support> reportSupports = report.getSupports();
//		for (Support support : reportSupports) {
//			int supportId = support.getId();
//			PropositionNode supportNode = (PropositionNode) Network.getNodeById(supportId);
//			if (supportNode.assertedInContext(reportContextName))
//				return true;
//		}
//		return false;
//	}

	public boolean anySupportAssertedInContext(Report report)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		String reportContextName = report.getContextName();
		Context reportContext = Controller.getContextByName(reportContextName);
		Hashtable<String, PropositionSet> reportSupports = report.getSupports();
		PropositionSet contextHypothesisSet = reportContext.getHypothesisSet();
		Collection<PropositionSet> reportSupportsSet = reportSupports.values();
		for (PropositionSet assumptionHyps : reportSupportsSet) {
			if (assumptionHyps.isSubSet(contextHypothesisSet)) {
				return true;
			}
		}
		return false;
	}

	/***
	 * Request handling in Rule proposition nodes.
	 * 
	 * @param currentChannel
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws DuplicatePropositionException
	 */
	public void processSingleRequestsChannel(Channel currentChannel)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
		if (currentChannel instanceof RuleToConsequentChannel) {
			boolean closedTypeTerm = term instanceof Closed;
			String currentContextName = currentChannel.getContextName();
			Context currentContext = Controller.getContextByName(currentContextName);
			Substitutions filterSubs = currentChannel.getFilter().getSubstitutions();
			if (closedTypeTerm) {
				/* Case 1 */
				if (assertedInContext(currentContext)) {
					requestAntecedentsNotAlreadyWorkingOn(currentChannel);
					return;
				} else {
					super.processSingleRequestsChannel(currentChannel);
				}
			} else {
				VariableNodeStats ruleNodeStats = computeNodeStats(filterSubs);
				boolean ruleNodeAllVariablesBound = ruleNodeStats.areAllVariablesBound();
				Vector<Binding> ruleNodeExtractedSubs = ruleNodeStats.getVariableNodeSubs();
				if (ruleNodeAllVariablesBound) {
					/* Case 2 */
					ReportSet knownReportSet = knownInstances;
					for (Report report : knownReportSet) {
						Substitutions reportSubstitutions = report.getSubstitutions();
						VariableNodeStats reportNodeStats = computeNodeStats(reportSubstitutions);
						Vector<Binding> reportNodeExtractedSubs = reportNodeStats.getVariableNodeSubs();
						if (ruleNodeExtractedSubs.size() == reportNodeExtractedSubs.size()
								&& anySupportAssertedInContext(report)) {
							requestAntecedentsNotAlreadyWorkingOn(currentChannel);
							return;
						}
					}
					super.processSingleRequestsChannel(currentChannel);
					return;
				} else {
					/* Case 3 */
					// Case 3: fih one free variale le kol instance hab3at lel antecedents request
					// bel filter beta3 each istance while checking if alreadyWorking on it, we call
					// super.proccessSingle
					super.processSingleRequestsChannel(currentChannel);
				}

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
		for (Channel currentChannel : incomingChannels) {
			ReportSet channelReports = currentChannel.getReportsBuffer();
			for (Report currentReport : channelReports) {
				if (currentChannel instanceof AntecedentToRuleChannel) {
					applyRuleHandler(currentReport, currentChannel.getReporter());
				}
			}
			currentChannel.clearReportsBuffer();
		}
	}

}
