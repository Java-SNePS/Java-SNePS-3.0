package sneps.network;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleResponse;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.LinearSubstitutions;

public abstract class RuleNode extends PropositionNode implements Serializable{
	private static final long serialVersionUID = 3891988384679269734L;
	
	protected NodeSet consequents;
	protected NodeSet antecedents;
	
	/**
	 * A NodeSet containing all the pattern antecedents attached to this RuleNode.
	 */
	protected NodeSet antNodesWithVars;
	protected NodeSet antNodesWithoutVars;
	protected Set<Integer> antNodesWithVarsIDs;
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
	 * Returns a NodeSet of the nodes that are being pointed at, by this RuleNode, 
	 * with the DownCables that have the same relation name as the given String.
	 * 
	 * @param name
	 * 		Relation name.
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

	public RuleUseInfo getConstantRui() {
		return constantRUI;
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

	@Override
	public void processRequests() {
		for (Channel currentChannel : outgoingChannels) {
			if (currentChannel instanceof RuleToConsequentChannel) {
				VarNodeSet variablesList = ((Open)this.term).getFreeVariables();
				if (variablesList.isEmpty()) {
					//Proposition semanticType = (Proposition) this.getSemantic();//TODO change according to snebr
					if (this.semanticType.isAsserted(Controller.getContextByName(currentChannel.getContextName()))) {
						NodeSet antecedentNodeSet = this.getDownAntNodeSet();
						NodeSet toBeSentTo = new NodeSet();
						for (Node currentNode : antecedentNodeSet) {
							if (currentNode == currentChannel.getRequester()) {
								continue;
							}
							// TODO Akram: if not yet been requested for this
							// instance
							if (true) {
								toBeSentTo.addNode(currentNode);
							}
						}
						sendRequests(toBeSentTo, currentChannel.getFilter().getSubstitution(),
								currentChannel.getContextName(), ChannelTypes.RuleAnt);
					}
				} else if (true)

				{
					// TODO Akram: there are free variables but each is bound
				} else if (true)

				{
					// TODO Akram: there are free variable
				}

			} else {
				super.processSingleRequest(currentChannel);
			}
		}
	}

	@Override
	public void processReports() {
		for (Channel currentChannel : incomingChannels) {
			ReportSet channelReports = currentChannel.getReportsBuffer();
			for (Report currentReport : channelReports) {
				if (currentChannel instanceof AntecedentToRuleChannel) {
					//applyRuleHandler(currentReport, currentChannel.getReporter());
				}
			}
			currentChannel.clearReportsBuffer();
		}
	}
	
}
