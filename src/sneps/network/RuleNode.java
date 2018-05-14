
package sneps.network;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import sneps.network.classes.Semantic;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;
import sneps.setClasses.ContextRuisSet;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.ReportSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.setClasses.VarNodeSet;
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

public abstract class RuleNode extends PropositionNode {
	private static final long serialVersionUID = 3891988384679269734L;
	private NodeSet consequents;
	protected NodeSet antNodesWithVars;
	protected NodeSet antNodesWithoutVars;
	protected Set<Integer> antNodesWithVarsIDs;
	protected Set<Integer> antNodesWithoutVarsIDs;
	protected boolean shareVars;
	protected VarNodeSet sharedVars;
	protected ContextRuisSet contextRuisSet;
	private Hashtable<Context, RuleUseInfo> contextConstantRUI;

	public RuleNode(Semantic sym){
		super(sym);
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		contextRuisSet = new ContextRuisSet();
		contextConstantRUI = new Hashtable<Context, RuleUseInfo>();
	}
	public RuleNode(Term trm){
		super(trm);
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		contextRuisSet = new ContextRuisSet();
		contextConstantRUI = new Hashtable<Context, RuleUseInfo>();
	}
	public RuleNode(Semantic sym, Term syn) {
		super(sym, syn);
		antNodesWithoutVars = new NodeSet();
		antNodesWithoutVarsIDs = new HashSet<Integer>();
		antNodesWithVars = new NodeSet();
		antNodesWithVarsIDs = new HashSet<Integer>();
		contextRuisSet = new ContextRuisSet();
		contextConstantRUI = new Hashtable<Context, RuleUseInfo>();
	}

	protected void sendReportToConsequents(Report reply) {
		if(!knownInstances.contains(reply))
			newInstances.addReport(reply);
		for (Channel outChannel : outgoingChannels)
			if(outChannel instanceof RuleToConsequentChannel)
				outChannel.addReport(reply);
	}

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

	public void applyRuleHandler(Report report, Node signature) {
		String contextID = report.getContextName();
		RuleUseInfo rui;
		if (report.isPositive()) {
			FlagNodeSet fns = report.getSupports();
			NodeSet temp = new NodeSet();
			temp.addNode(signature);
			fns.insert(new FlagNode(signature, temp, 1));
			rui = new RuleUseInfo(report.getSubstitutions(),
					1, 0, fns);
		} else {
			FlagNodeSet fns = report.getSupports();
			NodeSet temp = new NodeSet();
			temp.addNode(signature);
			fns.insert(new FlagNode(signature, temp, 2));
			rui = new RuleUseInfo(report.getSubstitutions(), 0, 1, fns);
		}
		RuisHandler crtemp = contextRuisSet.getByContext(contextID);
		if(crtemp == null){
			crtemp = addContextRUIS(contextID);
		}

		RuleUseInfoSet res = crtemp.insertRUI(rui);
		if (res == null)
			res = new RuleUseInfoSet();
		for (RuleUseInfo tRui : res) {
			applyRuleOnRui(tRui, contextID);
		}
	}

	abstract protected void applyRuleOnRui(RuleUseInfo tRui, String contextID);

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
			if (!n.hasSameFreeVariableAs((VariableNode) nodes.getNode(i))) {
				res = false;
				break;
			}
		}
		return res;
	}

	public NodeSet getConsequents() {
		return consequents;
	}

	public VarNodeSet getSharedVarsNodes(NodeSet nodes) {
		VarNodeSet res = new VarNodeSet();
		VarNodeSet temp = new VarNodeSet();
		if (nodes.isEmpty())
			return res;

		for(Node curNode : nodes){
			if(curNode instanceof VariableNode){
				if(temp.contains((VariableNode)curNode))
					res.addVarNode((VariableNode)curNode);
				else
					temp.addVarNode((VariableNode) curNode);
			}
			
			if(curNode.getTerm() instanceof Open){
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

	public NodeSet getDownNodeSet(String name) {
		return ((Molecular)term).getDownCableSet().getDownCable(name).getNodeSet();
	}

	public abstract NodeSet getDownAntNodeSet();

	public NodeSet getUpNodeSet(String name) {
		return this.getUpCableSet().getUpCable(name).getNodeSet();
	}

	public RuisHandler getContextRuiHandler(Context cntxt) {
		return contextRuisSet.getByContext((String) cntxt.getName());
	}

	public RuisHandler addContextRUIS(String contextName) {
		Context contxt = (Context) Controller.getContextByName(contextName);
		if (sharedVars.size() != 0) {
			SIndex si = null;
			if (shareVars)
				si = new SIndex(contextName, sharedVars, SIndex.SINGLETONRUIS, getPatternNodes());
			else
				si = new SIndex(contextName, sharedVars, getSIndexContextType(), getParentNodes());
			return this.addContextRUIS(si);
		} else {
			return this.addContextRUIS(contxt,createRuisHandler(contextName));
		}
	}

	private RuleUseInfoSet addContextRUIS(SIndex si) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getAntSize(){
		return antNodesWithoutVars.size() + antNodesWithVars.size();
	}

	public RuisHandler addContextRUIS(Context cntxt, RuisHandler cRuis) {
		return contextRuisSet.addHandlerSet((String) cntxt.getName(), cRuis);
	}

	protected abstract RuisHandler createRuisHandler(String contextName);

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

	public static boolean isConstantNode(Node n) {
		return !(n.getTerm() instanceof Molecular) || (n.getTerm() instanceof Variable);//TODO check
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
