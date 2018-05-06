package sneps.snip.rules;

import java.util.HashSet;
import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

public class AndEntailment extends RuleNode {
	private NodeSet consequents;//TODO Proposition Nodes get

	public AndEntailment(Term syn) {
		super(syn);
		setConsequents(new NodeSet());
	}
	public AndEntailment(Semantic sym, Term syn) {
		super(sym, syn);
		setConsequents(new NodeSet());
	}

	@Override
	public void applyRuleHandler(Report report, Node signature) {
		String contxt = report.getContextName();
		if (report.isPositive()) {
			FlagNodeSet fns = new FlagNodeSet();
			fns.putIn(new FlagNode(signature, report.getSupports(), 1));
			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(),
					1, 0, fns);
			addNotSentRui(rui, contxt,signature);
		}
		if (contextRuisSet.getByContext(contxt).getPositiveNodes().size() >= antNodesWithoutVars.size() + antNodesWithVars.size())
			sendSavedRUIs(report.getContextName());
	}

	@Override
	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
		//addNotSentRui(Rui, contextID);
		if (Rui.getPosCount() != antNodesWithVars.size() + antNodesWithoutVars.size())
			return;
		Support originSupports = this.getBasicSupport();
		HashSet<Support> sup = new HashSet<Support>();
		sup.add(originSupports);
		
		//Send this V
		contextRuisSet.getByContext(contextID).getPositiveNodes().addNode(this);

		//contextRuisSet.getByContext((String) Controller.getContextByName(contextID)).insertRUI(Rui);

		Report reply = new Report(Rui.getSub(), Rui.getSupport(sup), true, contextID);
		broadcastReport(reply);
	}

	public void addNotSentRui(RuleUseInfo rui, String contxt, Node signature){
		PTree tree = (PTree) contextRuisSet.getByContext(contxt);
		if (tree == null)
			tree = (PTree) createRuisHandler(contxt);
		tree.insertRUI(rui);
		tree.getPositiveNodes().addNode(signature);
		contextRuisSet.addHandlerSet(contxt, tree);
	}
	private void sendSavedRUIs(String contextID) {
		RuleUseInfo addedConstant = getConstantRUI(contextID);
		if (addedConstant == null && antNodesWithoutVars.size() != 0)
			return;

		if (antNodesWithoutVars.size() != addedConstant.getPosCount())
			return;

		RuleUseInfoSet ruis = ((PTree)contextRuisSet.getByContext(contextID)).getAllRootRuis();
		if (ruis == null) {
			applyRuleOnRui(addedConstant, contextID);
			return;
		}

		RuleUseInfo combined;
		for (RuleUseInfo info : ruis) {
			combined = info.combine(addedConstant);
			if (combined != null)
				applyRuleOnRui(combined, contextID);
		}
	}

	@Override
	public RuisHandler createRuisHandler(String context) {
		Context contxt = (Context) Controller.getContextByName(context);
		PTree tree = new PTree(context);
		NodeSet ants = antNodesWithoutVars;
		ants.addAll(antNodesWithVars);
		tree.buildTree(ants);
		return this.addContextRUIS(contxt, tree);
	}
	@Override
	public NodeSet getDownAntNodeSet() {
		return this.getDownNodeSet("&ant");//ants for & TODO name convention
	}

	@Override
	public void clear(){
		super.clear();
		consequents.clear();
	}


	public NodeSet getConsequents() {
		return consequents;
	}
	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}

}
