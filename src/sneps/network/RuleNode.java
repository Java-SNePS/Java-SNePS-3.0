package sneps.network;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import sneps.network.classes.setClasses.ContextRuisSet;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;

public abstract class RuleNode extends PropositionNode implements Serializable{
	private static final long serialVersionUID = 3891988384679269734L;
	
	private NodeSet consequents;
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
	protected VarNodeSet sharedVars;
	
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
	 * A Hashtable used to map each context to a single RuleUseInfo 
	 * that contains all the constant instances that do not dominate variables.
	 */
	private Hashtable<Context, RuleUseInfo> contextConstantRUI;

	public RuleNode() {
		consequents = new NodeSet();
		antecedents = new NodeSet();
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		shareVars = false;
		sharedVars = new VarNodeSet();
		//contextRuisSet = new ContextRuisSet();
		contextConstantRUI = new Hashtable<Context, RuleUseInfo>();
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
		sharedVars = new VarNodeSet();
		//contextRuisSet = new ContextRuisSet();
		contextConstantRUI = new Hashtable<Context, RuleUseInfo>();
	}
	
	public NodeSet getConsequents() {
		return consequents;
	}

	public RuisHandler getRuisHandler() {
		return ruisHandler;
	}

	public int getAntSize(){
		return antNodesWithoutVars.size() + antNodesWithVars.size();
	}
	
	protected NodeSet getPatternNodes() {
		return antNodesWithVars;
	}

	protected void sendReportToConsequents(Report reply) {
		if(!knownInstances.contains(reply))
			newInstances.addReport(reply);
		for (Channel outChannel : outgoingChannels)
			if(outChannel instanceof RuleToConsequentChannel)
				outChannel.addReport(reply);
	}

	/**
	 * Process antecedent nodes, used for initialization.
	 * 
	 * @param antNodes
	 */
	protected void processNodes(NodeSet antNodes) {
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
	 * associated with the report's context, for this RuleNode. It instantiates a 
	 * RuisHandler if this is the first report received in a particular context. It 
	 * then applies the inference rules of this RuleNode on the current stored RUIs.
	 * 
	 * @param report
	 * @param signature
	 * 		The instance that is being reported by the report.
	 */
	public void applyRuleHandler(Report report, Node signature) {
		//String contextID = report.getContextName();
		RuleUseInfo rui;
		Collection<PropositionSet> propSet = report.getSupports();
		FlagNodeSet fns = new FlagNodeSet();
		
		if (report.isPositive()) {
			fns.insert(new FlagNode(signature, propSet, 1));
			rui = new RuleUseInfo(report.getSubstitutions(),
					1, 0, fns);
		} else {
			fns.insert(new FlagNode(signature, propSet, 2));
			rui = new RuleUseInfo(report.getSubstitutions(), 0, 1, fns);
		}
		
		//RuisHandler crtemp = contextRuisSet.getByContext(contextID);		
		
		// This is the first report received by this RuleNode, so a RuisHandler is 
		// created
		if(ruisHandler == null){
			ruisHandler = addRuiHandler();
		}

		// The RUI created for the given report is inserted to the RuisHandler
		RuleUseInfoSet res = ruisHandler.insertRUI(rui);
		if (res == null)
			res = new RuleUseInfoSet();
		
		for (RuleUseInfo tRui : res) {
			applyRuleOnRui(tRui);
		}
	}

	abstract protected void applyRuleOnRui(RuleUseInfo tRui);

	/**
	 * Clears all the information saved by this RuleNode about the instances received.
	 */
	public void clear() {
		//contextRuisSet.clear();
		contextConstantRUI.clear();
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
	 * Returns a VarNodeSet of VariableNodes that are shared among some/all the Nodes 
	 * in the given NodeSet.
	 * 
	 * @param nodes
	 * @return VarNodeSet
	 */
	public VarNodeSet getSharedVarsNodes(NodeSet nodes) {
		VarNodeSet res = new VarNodeSet();
		VarNodeSet temp = new VarNodeSet();
		
		if (nodes.isEmpty())
			return res;

		for(Node curNode : nodes) {
			if(curNode instanceof VariableNode) {
				if(temp.contains((VariableNode) curNode))
					res.addVarNode((VariableNode) curNode);
				else
					temp.addVarNode((VariableNode) curNode);
			}
			
			if(curNode.getTerm() instanceof Open) {
				VarNodeSet free = ((Open)curNode.getTerm()).getFreeVariables();
				for(VariableNode var : free)
					if(temp.contains(var))
						res.addVarNode(var);
					else
						temp.addVarNode(var);
			}
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
		if(term != null && term instanceof Molecular)
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

	public NodeSet getUpNodeSet(String name) {
		return this.getUpCableSet().getUpCable(name).getNodeSet();
	}

	/**
	 * Returns the RuisHandler associated with the given context.
	 * 
	 * @param cntxt
	 * @return RuisHandler
	 */
	/*public RuisHandler getContextRuiHandler(String cntxt) {
		return contextRuisSet.getByContext(cntxt);
	}*/

	/**
	 * Creates an appropriate RuisHandler for this RuleNode, according to whether all, 
	 * some or none of the variables in the antecedents are shared.
	 * 
	 * @param contextName
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
			//return this.addContextRuiHandler(contextName, createRuisHandler(contextName));
			return createRuisHandler();
		}
	}
	
	/*/**
	 * Adds the given RuisHandler by the given context name to this contextRuisSet.
	 * 
	 * @param cntxt
	 * 		Context name.
	 * @param cRuis
	 * 		RuisHandler to be added.
	 * @return RuisHandler
	 */
	/*public RuisHandler addContextRuiHandler(String cntxt, RuisHandler cRuis) {
		return this.contextRuisSet.addHandlerSet(cntxt, cRuis);
	}*/

	protected abstract RuisHandler createRuisHandler();

	/**
	 * Returns a RUISet to be used in case all the antecedents do not share a common 
	 * variable, and this RuleNode is not an AndEntailment.
	 * 
	 * @param contextName
	 * @return RuleUseInfoSet
	 */
	protected RuleUseInfoSet createContextRuiHandlerNonShared(String contextName) {
		return new RuleUseInfoSet(false);
	}

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

	public RuleUseInfo addConstantRuiToContext(String context, RuleUseInfo rui) {
		Context contxt = (Context) Controller.getContextByName(context);
		RuleUseInfo tRui = contextConstantRUI.get(contxt);
		if (tRui != null)
			tRui = rui.combine(tRui);
		else
			tRui = rui;
		if (tRui == null)
			throw new NullPointerException(
					"The existed RUI could not be merged " + "with the given rui so check your code again");
		contextConstantRUI.put(contxt, tRui);
		return tRui;
	}

	public RuleUseInfo getConstantRui(Context con) {
		RuleUseInfo tRui = contextConstantRUI.get(con);
		return tRui;
	}

	public RuleUseInfo getConstantRUI(String context) {
		return contextConstantRUI.get(context);
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
					applyRuleHandler(currentReport, currentChannel.getReporter());
				}
			}
			currentChannel.clearReportsBuffer();
		}
	}

}
