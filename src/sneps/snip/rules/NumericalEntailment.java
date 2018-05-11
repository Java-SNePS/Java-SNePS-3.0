package sneps.snip.rules;

import java.util.HashSet;
import java.util.Set;

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
import sneps.snip.classes.SIndex;
/**
 * @author Amgad Ashraf
 */
public class NumericalEntailment extends RuleNode {
	private static final long serialVersionUID = 3546852401118194013L;
	private NodeSet consequents;
	private int i;

	public NumericalEntailment(Term syn) {
		super(syn);
	}
	public NumericalEntailment(Semantic sym, Term syn) {
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
			FlagNodeSet fns = new FlagNodeSet();
			fns.putIn(new FlagNode(signature, report.getSupports(), 1));
			RuleUseInfo rui = new RuleUseInfo(report.getSubstitutions(),
					1, 0, fns);
			addNotSentRui(rui, contxt, signature);
		}
		int curPos = contextRuisSet.getByContext(contxt).getPositiveNodes().size();
		int n = antNodesWithoutVars.size()+antNodesWithVars.size();
		if ((curPos >= i) && ((curPos < n-i+1) || (curPos < n-1) ) )
			sendSavedRUIs(report.getContextName());
	}
	/**
	 * Creates a Report from a given RuleUseInfo to be broadcasted to outgoing channels
	 * @param rui
	 * @param contextID 
	 */
	@Override
	protected void applyRuleOnRui(RuleUseInfo rui, String contextID) {
		if (rui.getPosCount() >= i){
			Set<Support> originSupports = new HashSet<Support>();
			originSupports.add(this.getBasicSupport());
			Report reply = new Report(rui.getSub(),rui.getSupport(originSupports), true, contextID);
			broadcastReport(reply);
		}
	}

	/**
	 * Inserts given RuleUseInfo into the appropriate SIndex and updates the corresponding SIndex
	 * @param rui
	 * @param contxt
	 * @param signature
	 */
	public void addNotSentRui(RuleUseInfo rui, String contxt, Node signature){
		SIndex set = (SIndex) contextRuisSet.getByContext(contxt);
		//if (set == null) 
		//set = new SIndex(contxt, sharedVars, 0, consequents);
		set.insertRUI(rui);
		set.getPositiveNodes().addNode(signature);
		contextRuisSet.addHandlerSet(contxt, set);
	}
	/**
	 * Prepares the appropriate SIndex and all its root RuleUseInfo for broadcasting  
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

	@Override
	public RuisHandler createRuisHandler(String contextName) {
		Context contxt = (Context) Controller.getContextByName(contextName);
		SIndex index = new SIndex(contextName, getSharedVarsNodes(antNodesWithVars), (byte) 0, getDominatingRules());
		return this.addContextRUIS(contxt, index);
	}

	@Override
	public NodeSet getDownAntNodeSet(){
		return this.getDownNodeSet("iant");
	}

	public NodeSet getConsequents() {
		return consequents;
	}
	public int getI() {
		return i;
	}

}
