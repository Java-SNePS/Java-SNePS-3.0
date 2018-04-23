package sneps.snip.classes;

import java.util.Hashtable;
import java.util.Set;

import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.matching.Substitutions;
public class SIndex extends RuisHandler {

	private Hashtable<int [], RuisHandler> map;
	private byte ruiType;
	public static final byte RUIS = 0, SINGLETONRUIS = 1, PTREE = 2;
	private Set<Integer>sharedVars;
	private NodeSet nodesWithVars;

	public SIndex(String context,Set<Integer> SharedVars, byte ruisType, NodeSet parentNodes) {
		super(context);
		this.sharedVars=SharedVars;
		this.ruiType=ruisType;
		map = new Hashtable<int[], RuisHandler>();
	}
	
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		int[] vars = new int[sharedVars.size()];
		int index = 0;
		for (int varId : sharedVars) {
			vars[index++] = rui.getSub().termID(varId);
		}
		
		RuisHandler trui= map.get(vars);
		if (trui == null) {
			trui = getNewRUIS();
			map.put(vars, trui);
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
	
}