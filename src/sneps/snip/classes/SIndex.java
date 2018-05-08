package sneps.snip.classes;

import java.util.Hashtable;

import sneps.network.VariableNode;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.setClasses.VarNodeSet;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.matching.Substitutions;

public class SIndex extends RuisHandler {
	
	private Hashtable<Substitutions, RuisHandler> map;
	private byte ruiType;
	public static final byte RUIS = 0, SINGLETONRUIS = 1, PTREE = 2;
	private VarNodeSet sharedVars;
	private NodeSet nodesWithVars;

	public SIndex(String context, VarNodeSet SharedVars, byte ruisType, NodeSet parentNodes) {
		super(context);
		this.sharedVars=SharedVars;
		this.ruiType=ruisType;
		map = new Hashtable<Substitutions, RuisHandler>();
	}

	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		
		RuisHandler trui= map.get(rui.getSub());
		if (trui == null) {
			trui = getNewRUIS();
			map.put(rui.getSub(), trui);
		}

		RuleUseInfoSet res = trui.insertRUI(rui);
		return res;
	}

	private RuisHandler getNewRUIS() {
		RuisHandler tempRui = null;
		switch (ruiType) {
		case PTREE:
			tempRui = new PTree(getContext());
			((PTree) tempRui).buildTree(nodesWithVars);
			break;
		case SINGLETONRUIS:
			tempRui = new RuleUseInfoSet(getContext() , true);
			break;
		case RUIS:
			tempRui = new RuleUseInfoSet(getContext() , false);
		default:
			break;
		}
		return tempRui;
	}

	
	public int getSize() {
		return map.size();
	}


}