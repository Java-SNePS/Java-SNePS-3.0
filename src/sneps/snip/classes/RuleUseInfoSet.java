package sneps.snip.classes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class RuleUseInfoSet {
	
	private String context;
	
	public String getContext() {
		return context;
	}
	
	private HashSet<RuleUseInfo> ruis;

	/**
	 * Create a new empty rule use info set for general use
	 */
	public RuleUseInfoSet() {
		ruis = new HashSet<RuleUseInfo>();
	}

	/**
	 * Create a new empty rule use info set for ContextRUIS use
	 */
	public RuleUseInfoSet(int contextID) {
		ruis = new HashSet<RuleUseInfo>();
	}

	/**
	 * Check is the rule use info set is new or not
	 * 
	 * @return true if new otherwise false
	 */
	public boolean isEmpty() {
		return ruis.isEmpty();
	}

	/**
	 * Add r to the rule use info set
	 * 
	 * @param r
	 *            rule use info
	 */
	public void add(RuleUseInfo r) {
		ruis.add(r);
	}

	

	/**
	 * Removes RuleUseInfo rui from the set and returns it
	 * 
	 * @param rui
	 *            RuleUseInfo
	 * @return RuleUseInfo
	 */
	public RuleUseInfo remove(RuleUseInfo rui) {
		ruis.remove(rui);
		return rui;
	}

	/**
	 * Combine rui with the rule use info set
	 * 
	 * @param rui
	 *            RuleUseInfo
	 * @return RuleUseInfoSet
	 */
	public RuleUseInfoSet combine(RuleUseInfo rui) {
		RuleUseInfoSet res = new RuleUseInfoSet();
		for (RuleUseInfo tRui : ruis) {
			RuleUseInfo tmp = rui.combine(tRui);
			if (tmp != null)
				res.add(tmp);
		}
		return res;
	}

	/**
	 * Combine ruis with the rule use info set
	 * 
	 * @param ruis
	 *            RuleUseInfoSet
	 * @return RuleUseInfoSet
	 */
	public RuleUseInfoSet combine(RuleUseInfoSet ruis) {
		RuleUseInfoSet res = new RuleUseInfoSet();
		for (RuleUseInfo rui : this.ruis) {
			RuleUseInfoSet temp = ruis.combine(rui);
			for (RuleUseInfo tRui : temp.ruis) {
				res.add(tRui);
			}
		}
		return res;
	}

	/**
	 * Return the number of rule use infos in this set
	 * 
	 * @return int
	 */
	public int cardinality() {
		return ruis.size();
	}

	public Iterator<RuleUseInfo> iterator() {
		return ruis.iterator();
	}
	
	
}
