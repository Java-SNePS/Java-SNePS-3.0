package sneps.snip.rules;

import sneps.network.Node;
import sneps.network.RuleNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
/**
 * @author Amgad Ashraf
 */
public class AndEntailment extends RuleNode {
	private static final long serialVersionUID = -8545987005610860977L;

	/**
	 * Constructor for the AndEntailment rule node
	 * @param syn
	 */
	public AndEntailment(Term syn) {
		super(syn);
	}
	/**
	 * Constructor for the AndEntailment rule node
	 * @param sym
	 * @param syn
	 */
	public AndEntailment(Semantic sym, Term syn) {
		super(sym, syn);
	}

	/**
	 * Creates the first RuleUseInfo from a given Report and stores it(if positive)
	 * Also checks if current number of positive Reports satisfy rule
	 * @param report
	 * @param signature
	 */
	@Override
	public void applyRuleHandler(Report report, Node signature) {
		String contxt = report.getContextName();
		if (report.isPositive()) {
			FlagNodeSet fns = report.getSupports();
			NodeSet temp = new NodeSet();
			temp.addNode(signature);
			fns.insert(new FlagNode(signature, temp, 1));
			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(),
					1, 0, fns);
			addNotSentRui(rui, contxt, signature);
		}
		if (contextRuisSet.getByContext(contxt).getPositiveNodes().size() >= getAntSize())
			sendSavedRUIs(report.getContextName());
	}

	/**
	 * Creates a Report from a given RuleUseInfo to be broadcasted to outgoing channels
	 * @param Rui
	 * @param contextID 
	 */
	@Override
	protected void applyRuleOnRui(RuleUseInfo Rui, String contextID) {
		if (Rui.getPosCount() >= getAntSize()){
			FlagNodeSet justification = contextRuisSet.getByContext(contextID).getPositiveNodes();
			NodeSet temp = new NodeSet();
			temp.addNode(this);
			FlagNode fn = new FlagNode(this, temp, 1);
			justification.insert(fn);

			Report reply = new Report(Rui.getSub(), justification, true, contextID);
			sendReportToConsequents(reply);
		}
	}

	/**
	 * Inserts given RuleUseInfo into the appropriate PTree and updates the corresponding PTree
	 * @param rui
	 * @param contxt
	 * @param signature
	 */
	public void addNotSentRui(RuleUseInfo rui, String contxt, Node signature){
		PTree tree = (PTree) contextRuisSet.getByContext(contxt);
		if (tree == null)
			tree = (PTree) createRuisHandler(contxt);
		tree.insertRUI(rui);
		NodeSet temp = new NodeSet();
		temp.addNode(signature);
		tree.getPositiveNodes().insert(new FlagNode(signature, temp, 1));
		contextRuisSet.addHandlerSet(contxt, tree);
	}
	/**
	 * Prepares the appropriate PTree and all its root RuleUseInfo for broadcasting  
	 * @param contextID
	 */
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

	/**
	 * Creates an appropriate PTree as a RuisHandler, builds it and inserts it into ContextRuisSet by Context
	 * @param context
	 * @return
	 */
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
		return this.getDownNodeSet("&ant");//ants for & name convention
	}

}
