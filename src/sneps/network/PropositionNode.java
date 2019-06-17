package sneps.network;

import java.io.Serializable;
import java.util.ArrayList;

import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.ContradictionFoundException;
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

import sneps.snebr.Context;
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
import sneps.snip.matching.Substitutions;

public class PropositionNode extends Node implements Serializable{
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
	

	public void processSingleChannelReports(Channel currentChannel) {
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
			processSingleChannelReports(inChannel);
	}

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

	public void processSingleRequest(Channel currentChannel) {
		//TODO check correctness
		/*
		PropositionSet propSet = new PropositionSet();
		propSet.addProposition((PropositionNode) this);
		Context desiredContext = SNeBR.getContextByID(currentChannel.getContextID());
		if (propSet.assertedInContext(desiredContext)) {
			// TODO change the subs to hashsubs
			// System.out.println("#$#$#$#$# -1 " + desiredContext.getId());
			Set<Support> support = new HashSet<Support>();
			support.add(new Support((PropositionNode) this));
			Report reply = new Report(new LinearSubstitutions(), support, true, currentChannel.getContextID());
			knownInstances.add(reply);
			broadcastReport(reply);
		} else {
			boolean sentAtLeastOne = false;
			for (Report currentReport : knownInstances) {
				sentAtLeastOne = sendReport(currentReport, currentChannel);
			}
			// TODO Akram: passed the filter subs to isWhQuest, is that correct
			// ?
			// System.out.println("#$#$#$#$# 0");
			if (!sentAtLeastOne || isWhQuestion(currentChannel.getFilter().getSubstitution())) {
				if (!alreadyWorking(currentChannel)) {
					NodeSet dominatingRules = getDominatingRules();
					sendRequests(dominatingRules, currentChannel.getFilter().getSubstitution(),
							currentChannel.getContextID(), ChannelTypes.RuleCons);
					// System.out.println("#$#$#$#$# 1");
					if (!(currentChannel instanceof MatchChannel)) {
						try {
							List<Object[]> matchesReturned = Matcher.Match(this);
							if(matchesReturned != null) {
								ArrayList<Pair> matches = new ArrayList<Pair>();
								for(Object[] match : matchesReturned) {
									Pair newPair = new Pair((Substitutions)match[1], (Substitutions)match[2], (Node)match[0]);
									matches.add(newPair);
								}
								sendRequests(matches, currentChannel.getContextID(), ChannelTypes.MATCHED);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
*/
	}

	public void processRequests() {
		for (Channel outChannel : outgoingChannels)
			processSingleRequest(outChannel);
	}

	public void sendRequests(ArrayList<Pair> list, String conetxtID, ChannelTypes channelType) {
		for (Pair currentPair : list) {
			Substitutions switchSubs = currentPair.getSwitch();
			Substitutions filterSubs = currentPair.getFilter();
			Channel newChannel;
			if (channelType == ChannelTypes.MATCHED) {
				newChannel = new MatchChannel(switchSubs, filterSubs, conetxtID, this, currentPair.getNode(), true);
			} else if (channelType == ChannelTypes.RuleAnt) {
				newChannel = new AntecedentToRuleChannel(switchSubs, filterSubs, conetxtID, this, currentPair.getNode(),
						true);
			} else {
				newChannel = new RuleToConsequentChannel(switchSubs, filterSubs, conetxtID, this, currentPair.getNode(),
						true);
			}
			incomingChannels.addChannel(newChannel);
			currentPair.getNode().receiveRequest(newChannel);
		}
	}

	public void sendRequests(NodeSet ns, Substitutions filterSubs, String contextID, ChannelTypes channelType) {
		for (Node sentTo : ns) {
			Channel newChannel = null;
			if (channelType == ChannelTypes.MATCHED) {
				newChannel = new MatchChannel(new LinearSubstitutions(), filterSubs, contextID, this, sentTo, true);
			} else if (channelType == ChannelTypes.RuleAnt) {
				newChannel = new AntecedentToRuleChannel(new LinearSubstitutions(), filterSubs, contextID, this, sentTo,
						true);
			} else {
				newChannel = new RuleToConsequentChannel(new LinearSubstitutions(), filterSubs, contextID, this, sentTo,
						true);
			}
			incomingChannels.addChannel(newChannel);
			sentTo.receiveRequest(newChannel);
		}
	}

	public void receiveRequest(Channel channel) {
		outgoingChannels.addChannel(channel);
		Runner.addToLowQueue(this);
	}

	public void receiveReports(Channel channel) {
		//TODO
	}

	public boolean alreadyWorking(Channel channel) {
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
	public Hashtable<String, PropositionSet> getTelescopedSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.getTelescopedSupport();
	}
	public Hashtable<String, PropositionSet> getJustificationSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.getJustificationSupport();
	}
	public void addJustificationBasedSupport(PropositionSet propSet) throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException, CannotInsertJustificationSupportException{
		basicSupport.addJustificationBasedSupport(propSet);
	}
	public Context addTelescopedSupport(String contextName, PropositionNode parent) throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException, ContradictionFoundException, ContextNameDoesntExistException {
		return basicSupport.addTelescopedSupport(contextName, parent);
	}
	public void removeNodeFromSupports(PropositionNode propNode) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		basicSupport.removeNodeFromSupports(propNode);
		
	}
	
	public Hashtable<String, ArrayList<Integer>> getGrades() {
		return basicSupport.getGrades();
	}
	
	public void addParentNode(int id) throws DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		 basicSupport.addParentNode(id);
		
	}
	
	public ArrayList<Integer> getParentSupports() {
		return basicSupport.getParentSupports();
	}
	
	
	public boolean HasChildren() {
		return basicSupport.HasChildren();
	}
	public String toString() {
		return basicSupport.toString();
	}
	public ArrayList<ArrayList<ArrayList<Integer>>> getMySupportsTree() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.getMySupportsTree();
	}

	public boolean reStructureJustifications() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		return basicSupport.reStructureJustifications();
	}

	public void setHyp(boolean isHyp) throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		basicSupport.setHyp(isHyp);
	}
	public boolean isHyp() {
		return basicSupport.isHyp();
	}
}