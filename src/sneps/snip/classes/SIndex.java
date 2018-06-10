package sneps.snip.classes;

import java.util.Hashtable;
import java.util.Set;

import sneps.network.VariableNode;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.setClasses.VarNodeSet;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.matching.Substitutions;

public class SIndex extends RuisHandler {
	
	private Hashtable<Integer, RuisHandler> map;
	private byte ruiType;
	public static final byte RUIS = 0, SINGLETONRUIS = 1, PTREE = 2;
	private VarNodeSet sharedVars;
	private NodeSet nodesWithVars;

	/**
	 * 
	 * @param context
	 * 			context Name
	 * @param SharedVars
	 * 			set<integers>
	 * @param ruisType
	 * 			byte
	 */
	
	public SIndex(String context, VarNodeSet SharedVars, byte ruisType) {
		super(context);
		this.sharedVars=SharedVars;
		this.ruiType=ruisType;
		map = new Hashtable<Integer, RuisHandler>();
	}

	/**
	 * Insert the rule use info in the map based on the substitution.
	 * If this rule use info is null from the map, a new one is created based on the type
	 * and if not, it will be replaced by combining this rui and the existing one
	 * 
	 * @param rui
	 * 			Rule Use Info
	 * 
	 * 
	 * 
	 */
	
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		
		int[] vars = new int[sharedVars.size()];
		int index = 0;
		for (VariableNode varId : sharedVars) {
			vars[index++] = sharedVars.getVarNode(index++).getId();
		}
		
		int x = getIndex(vars);

		
		RuisHandler trui= map.get(x);
		if (trui == null) {
			trui = getNewRUIS();
			map.put(x, trui);

		}
		
		RuleUseInfoSet res = trui.insertRUI(rui);
		return res;
		
	}
	
	private int getIndex(int[] x) {
		int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < x.length; ++i) {
			hash += (hash ^ x[i]) * p;
		}
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return hash;
	}
	

	/**
	 * create a new rule use info based on its type
	 * 
	 */

	private RuisHandler getNewRUIS() {
		RuisHandler tempRui = null;
		switch (ruiType) {
		case PTREE:
			tempRui = new PTree();
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

	/**
	 * get the size of the hashtable
	 * used in testing
	 * 
	 * @return int
	 */
	
	public int getSize() {
		return map.size();
	}


}