package sneps.snip.classes;

import sneps.network.classes.setClasses.RuleUseInfoSet;

/**
 * @className RuisHandler.java
 * 
 * @ClassDescription To deal with the large number of RUIs generated as more nodes are built in the network, RuisHandler classes are built to store and combine RUIs.
 * A RuisHandler is stored in a rule Node and a single rule Node can store multiple instances of a single RuisHandler class.
 */
public abstract class RuisHandler {
	
	/**
	 * Inserts the given RuleUseInfo into this RuisHandler and returns the 
	 * RuleUseInfoSet resulted from combining it with the RuleUseInfos in this 
	 * RuisHandler.
	 * @param rui
	 * RuleUseInfo
	 * @return RuleUseInfoSet
	 */
	abstract public RuleUseInfoSet insertRUI(RuleUseInfo rui);
	
	abstract public RuleUseInfoSet combineConstantRUI(RuleUseInfo rui);
	
	abstract public boolean isEmpty();
	
	abstract public void clear();
}
