package sneps.snip.classes;

import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;

/**
 * @className RuisHandler.java
 * 
 * @ClassDescription To deal with the large number of RUIs generated as more nodes are built in the network, RuisHandler classes are built to store and combine RUIs.
 * A RuisHandler is stored in a rule Node and a single rule Node can store multiple instances of a single RuisHandler class.
 * 
 * @author Amgad Ashraf
 * 
 * @version 3.00 31/5/2018
 */
public abstract class RuisHandler {
	protected String context;
	private NodeSet positiveNodes;

	public RuisHandler(){
		positiveNodes = new NodeSet();
	}
	
	public RuisHandler(String contextID) {
		this();
		this.context = contextID;
	}

	public String getContext() {
		return context;
	}

	/**
	 *Inserts the given RuleUseInfo into this RuisHandler and returns the RuleUseInfoSet 
	 *resulted from combining it with the RuleUseInfos in this RuisHandler
	 * @param rui
	 * RuleUseInfo
	 * @return RuleUseInfoSet
	 */
	abstract public RuleUseInfoSet insertRUI(RuleUseInfo rui);
	
	public NodeSet getPositiveNodes() {
		return positiveNodes;
	}
	
	public void setPositiveNodes(NodeSet positiveNodes) {
		this.positiveNodes = positiveNodes;
	}
}
