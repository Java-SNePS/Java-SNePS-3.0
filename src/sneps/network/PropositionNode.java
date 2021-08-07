package sneps.network;

import java.io.Serializable;

//import java.lang.ModuleLayer.Controller;

import tests.ActingTest;
import java.util.ArrayList;
import java.util.HashSet;

import sneps.network.cables.DownCable;
import sneps.snebr.Controller;
import sneps.snip.channels.ActToPropositionChannel;
import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Term;

import java.util.Hashtable;

import sneps.snebr.Support;
import sneps.snip.Pair;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.MatchChannel;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
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
		// habd starting!!!!
		if (currentChannel instanceof ActToPropositionChannel) {
			ArrayList<Node> nodes = Network.getNodesWithIDs();
			Node act = currentChannel.getRequester();
			Agenda requesterAgenda = ((ActNode) currentChannel.getRequester()).getAgenda();
			switch (requesterAgenda) {
			case FIND_PRECONDITIONS:
				//ArrayList<Node> nodes = Network.getNodesWithIDs();
				//Node act = currentChannel.getRequester();
				for (Node node : nodes) {
					if(!node.equals(this)) {
					if (node.getTerm() instanceof Molecular) {
						DownCable preconditions = node.getDownCableSet().getDownCable("precondition");
						DownCable acts = node.getDownCableSet().getDownCable("act");
						if (preconditions != null && acts != null) {
							ActNode actTest = (ActNode) acts.getNodeSet().getNode(0);
							Node precondition = preconditions.getNodeSet().getNode(0);
							if (actTest.equals(act) && !(precondition instanceof VariableNode)) {

								VariableNode var = (VariableNode) currentChannel.getReporter().getDownCableSet()
										.getDownCable("precondition").getNodeSet().getNode(0);

								LinearSubstitutions sub = new LinearSubstitutions();
								Binding B = new Binding(var, precondition);
								sub.putIn(B);
								Report report = new Report(sub, new HashSet<Support>(), true,
										Controller.getCurrentContextName());
								currentChannel.addReport(report);
							}

						}

					}
					}
				}
				break;
				
			case TEST:
				try {
				if (Controller.getCurrentContext().isAsserted(this)) {
						VariableNode v = Network.buildVariableNode();
						LinearSubstitutions sub = new LinearSubstitutions();
					Binding B = new Binding(v, this);
						sub.putIn(B);
						Report report = new Report(sub, new HashSet<Support>(), true,
								Controller.getCurrentContextName());
						currentChannel.addReport(report);

					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
				
				
			case FIND_EFFECTS:
			
				
				for (Node node : nodes) {
					if(!node.equals(this)) {
					if (node.getTerm() instanceof Molecular) {
						DownCable effects = node.getDownCableSet().getDownCable("effect");
						DownCable acts = node.getDownCableSet().getDownCable("act");
						if (effects != null && acts != null) {
							ActNode actTest = (ActNode) acts.getNodeSet().getNode(0);
							Node effect = effects.getNodeSet().getNode(0);
							if (actTest.equals(act) && !(effect instanceof VariableNode)) {

								VariableNode var = (VariableNode) currentChannel.getReporter().getDownCableSet()
										.getDownCable("effect").getNodeSet().getNode(0);

								LinearSubstitutions sub = new LinearSubstitutions();
								Binding B = new Binding(var, effect);
								sub.putIn(B);
								Report report = new Report(sub, new HashSet<Support>(), true,
										Controller.getCurrentContextName());
								currentChannel.addReport(report);
							}

						}

					}
					}
				}
				
				
				
				
				
				break;
				
				
			case FIND_PLANS:
				for (Node node : nodes) {
					if(!node.equals(this)) {
					if (node.getTerm() instanceof Molecular) {
						DownCable plans = node.getDownCableSet().getDownCable("plan");
						DownCable acts = node.getDownCableSet().getDownCable("act");
						DownCable goals= node.getDownCableSet().getDownCable("goal");
						if (plans != null && acts != null) {
							ActNode actTest = (ActNode) acts.getNodeSet().getNode(0);
							Node plan = plans.getNodeSet().getNode(0);
							if (actTest.equals(act) && !(plan instanceof VariableNode)) {

								VariableNode var = (VariableNode) currentChannel.getReporter().getDownCableSet()
										.getDownCable("plan").getNodeSet().getNode(0);

								LinearSubstitutions sub = new LinearSubstitutions();
								Binding B = new Binding(var, plan);
								sub.putIn(B);
								Report report = new Report(sub, new HashSet<Support>(), true,
										Controller.getCurrentContextName());
								currentChannel.addReport(report);
							}

						}
						else if (plans != null && goals != null) {
							PropositionNode goalTest = (PropositionNode) goals.getNodeSet().getNode(0);
							Node plan = plans.getNodeSet().getNode(0);
							Node goal = getDownCableSet().getDownCable("goal").getNodeSet().getNode(0);
							if (goalTest.equals(goal) && !(plan instanceof VariableNode)) {

								VariableNode var = (VariableNode) currentChannel.getReporter().getDownCableSet()
										.getDownCable("plan").getNodeSet().getNode(0);

								LinearSubstitutions sub = new LinearSubstitutions();
								Binding B = new Binding(var, plan);
								sub.putIn(B);
								Report report = new Report(sub, new HashSet<Support>(), true,
										Controller.getCurrentContextName());
								currentChannel.addReport(report);
							}

						}

					}
					}
				}
				
				break;
				
				
			case IDENTIFY_OBJECTS:
				Node property=getDownCableSet().getDownCable("property").getNodeSet().getNode(0);
				Node object= getDownCableSet().getDownCable("obj").getNodeSet().getNode(0);
				for (Node node : nodes) {
					if(!node.equals(this)) {
					if (node.getTerm() instanceof Molecular) {
						DownCable propertiesTest = node.getDownCableSet().getDownCable("property");
						DownCable objectsTest = node.getDownCableSet().getDownCable("obj");
						if (propertiesTest != null && objectsTest != null) {
							Node objectTest =  objectsTest.getNodeSet().getNode(0);
							Node propertyTest = propertiesTest.getNodeSet().getNode(0);
							if (property.equals(propertyTest) && !(objectTest instanceof VariableNode)) {

								VariableNode var = (VariableNode)object;

								LinearSubstitutions sub = new LinearSubstitutions();
								Binding B = new Binding(var, objectTest);
								sub.putIn(B);
								Report report = new Report(sub, new HashSet<Support>(), true,
										Controller.getCurrentContextName());
								currentChannel.addReport(report);
							}

						}

					}
					}
				}
				
				
				
				break;

			}
			
			
		

			
			

		}
		
		
		
//		if (currentChannel instanceof ActToPropositionChannel) {
////			try {
////				
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
//			Agenda requesterAgenda = ((ActNode) currentChannel.getRequester()).getAgenda();
//			switch (requesterAgenda) {
//			case FIND_PRECONDITIONS:
//				VariableNode var = (VariableNode) currentChannel.getReporter().getDownCableSet()
//						.getDownCable("precondition").getNodeSet().getNode(0);
//				for (PropositionNode precondition : ActingTest.preconditions) {
//					LinearSubstitutions sub = new LinearSubstitutions();
//					Binding B = new Binding(var, precondition);
//					sub.putIn(B);
//					Report report = new Report(sub, new HashSet<Support>(), true, Controller.getCurrentContextName());
//					currentChannel.addReport(report);
//				}
//				break;
//
//			case TEST:
//				try {
//					if (Controller.getCurrentContext().isAsserted(this)) {
//						VariableNode v = Network.buildVariableNode();
//						LinearSubstitutions sub = new LinearSubstitutions();
//						Binding B = new Binding(v, this);
//						sub.putIn(B);
//						Report report = new Report(sub, new HashSet<Support>(), true,
//								Controller.getCurrentContextName());
//						currentChannel.addReport(report);
//
//					}
//				}
//
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//
//			case FIND_EFFECTS:
//				VariableNode var2 = (VariableNode) currentChannel.getReporter().getDownCableSet().getDownCable("effect")
//						.getNodeSet().getNode(0);
//				for (PropositionNode effects : ActingTest.effects) {
//					LinearSubstitutions sub = new LinearSubstitutions();
//					Binding B = new Binding(var2, effects);
//					sub.putIn(B);
//					Report report = new Report(sub, new HashSet<Support>(), true, Controller.getCurrentContextName());
//					currentChannel.addReport(report);
//				}
//				break;
//			case FIND_PLANS:
//				
//				break;
//			default:
//				break;
//
//			}
//		}

		/*
		 * if(currentChannel instanceof ActToPropositionChannel) {
		 * if(currentChannel.get) {
		 * 
		 * for(Node n:Network.getNodesWithIDs()) {
		 * if(n.getDownCableSet().getDownCable("act")!=null &&
		 * n.getDownCableSet().getDownCable("act").getNodeSet().getNode(0).equals(
		 * currentChannel.getRequester())) {
		 * if(n.getDownCableSet().getDownCable("precondition")!=null) { Node
		 * precond=n.getDownCableSet().getDownCable("precondition").getNodeSet().getNode
		 * (0);
		 * 
		 * }
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * }
		 */
		//

		// TODO check correctness
		/*
		 * PropositionSet propSet = new PropositionSet();
		 * propSet.addProposition((PropositionNode) this); Context desiredContext =
		 * SNeBR.getContextByID(currentChannel.getContextID()); if
		 * (propSet.assertedInContext(desiredContext)) { // TODO change the subs to
		 * hashsubs // System.out.println("#$#$#$#$# -1 " + desiredContext.getId());
		 * Set<Support> support = new HashSet<Support>(); support.add(new
		 * Support((PropositionNode) this)); Report reply = new Report(new
		 * LinearSubstitutions(), support, true, currentChannel.getContextID());
		 * knownInstances.add(reply); broadcastReport(reply); } else { boolean
		 * sentAtLeastOne = false; for (Report currentReport : knownInstances) {
		 * sentAtLeastOne = sendReport(currentReport, currentChannel); } // TODO Akram:
		 * passed the filter subs to isWhQuest, is that correct // ? //
		 * System.out.println("#$#$#$#$# 0"); if (!sentAtLeastOne ||
		 * isWhQuestion(currentChannel.getFilter().getSubstitution())) { if
		 * (!alreadyWorking(currentChannel)) { NodeSet dominatingRules =
		 * getDominatingRules(); sendRequests(dominatingRules,
		 * currentChannel.getFilter().getSubstitution(), currentChannel.getContextID(),
		 * ChannelTypes.RuleCons); // System.out.println("#$#$#$#$# 1"); if
		 * (!(currentChannel instanceof MatchChannel)) { try { List<Object[]>
		 * matchesReturned = Matcher.Match(this); if(matchesReturned != null) {
		 * ArrayList<Pair> matches = new ArrayList<Pair>(); for(Object[] match :
		 * matchesReturned) { Pair newPair = new Pair((Substitutions)match[1],
		 * (Substitutions)match[2], (Node)match[0]); matches.add(newPair); }
		 * sendRequests(matches, currentChannel.getContextID(), ChannelTypes.MATCHED); }
		 * } catch (Exception e) { e.printStackTrace(); } } } } }
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
		// TODO
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