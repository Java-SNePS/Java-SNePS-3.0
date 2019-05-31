package sneps.snip.classes;

import sneps.network.classes.setClasses.RuleUseInfoSet;

/**
 * @className RuisHandler.java
 * 
 * @ClassDescription To deal with the large number of RUIs generated as more nodes are built in the network, 
 * RuisHandler classes are built to store and combine RUIs.
 */
public interface RuisHandler {
	
	/**
	 * Inserts the given RuleUseInfo into this RuisHandler and returns the 
	 * RuleUseInfoSet resulted from combining it with the RuleUseInfos in this 
	 * RuisHandler.
	 * @param rui
	 * RuleUseInfo
	 * @return RuleUseInfoSet
	 */
	public RuleUseInfoSet insertRUI(RuleUseInfo rui);
	
	public RuleUseInfoSet combineConstantRUI(RuleUseInfo rui);
	
	public void clear();
}
