package sneps.snip.classes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import sneps.network.VariableNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;

public class SIndex implements RuisHandler {

	/**
	 * A Hashtable used to map different substitutions to different RuisHandlers. 
	 * ArrayList<Integer> represents the ids of the bound values of the substitutions 
	 * for the shared variables.
	 */
	public Hashtable<ArrayList<Integer>, RuisHandler> map;
	private byte ruiHandlerType;
	public static final byte RUIS = 0, SINGLETON = 1, PTREE = 2;
	private Set<VariableNode> sharedVars;
	private NodeSet nodesWithVars;

	/**
	 * @param context
	 * 		Context name.
	 * @param ruiHandlerType
	 * 		The type of RuisHandler used by a rule to be mapped to in this SIndex.
	 * @param sharedVars
	 * 		Set of shared variables whose bindings are used to hash on in this SIndex.
	 * @param nodesWithVars	
	 */
	public SIndex(byte ruiHandlerType, Set<VariableNode> sharedVars, 
			NodeSet nodesWithVars) {
		map = new Hashtable<ArrayList<Integer>, RuisHandler>();
		this.ruiHandlerType = ruiHandlerType;
		this.sharedVars = sharedVars;
		this.nodesWithVars = nodesWithVars;
	}
	
	public SIndex(byte ruiHandlerType, Set<VariableNode> sharedVars) {
		map = new Hashtable<ArrayList<Integer>, RuisHandler>();
		this.ruiHandlerType = ruiHandlerType;
		this.sharedVars = sharedVars;
	}

	/**
	 * Inserts the RuleUseInfo in the map based on the bound values for the 
	 * substitutions. If the RuisHandler based on the given index is null in the map, 
	 * a new one is created according to the ruiHandlerType. If not, the RUI will be 
	 * inserted to the corresponding RuisHandler according to the implementation of 
	 * its insert method.
	 * 
	 * @param rui
	 * 			RuleUseInfo
	 */
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		ArrayList<Integer> boundValues = new ArrayList<Integer>();
		int boundValueID;
		for(VariableNode var : sharedVars) {
			if(rui.getSubstitutions().getBindingByVariable(var) != null) {
				boundValueID = rui.getSubstitutions().getBindingByVariable(var).getNode().getId();
				boundValues.add(boundValueID);
			}
		}
		
		if(!boundValues.isEmpty()) {
			RuisHandler trui = map.get(boundValues);
			if (trui == null) {
				trui = getNewRUIType();
				map.put(boundValues, trui);
			}
			
			RuleUseInfoSet res = trui.insertRUI(rui);
			return res;
		}
		
		return null;
	}

	/**
	 * Creates a new RuisHandler based on the type of SIndex needed.
	 * 
	 */
	private RuisHandler getNewRUIType() {
		RuisHandler tempRui = null;
		switch (ruiHandlerType) {
		case SINGLETON:
			tempRui = new RuleUseInfoSet(true);
			break;
		case PTREE:
			tempRui = new PTree();
			((PTree) tempRui).buildTree(nodesWithVars);
			break;
		case RUIS:
			tempRui = new RuleUseInfoSet(false);
			break;
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

	public byte getRuiHandlerType() {
		return ruiHandlerType;
	}
	
	public void clear() {
		map.clear();
	}
	
	public RuleUseInfoSet combineConstantRUI(RuleUseInfo rui) {
		RuleUseInfoSet res = new RuleUseInfoSet();
		Set<ArrayList<Integer>> keys = map.keySet();
        for(ArrayList<Integer> key : keys) {
        	RuisHandler handler = map.get(key);
        	RuleUseInfoSet temp = handler.combineConstantRUI(rui);
        	res.addAll(temp);
        }
        
        return res;
	}


}