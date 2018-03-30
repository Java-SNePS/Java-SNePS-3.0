package sneps.snip.classes;

import java.util.Hashtable;

public class ContextRuisSet {

	Hashtable<String, RuleUseInfoSet> crs;

	/**
	 * Create a new ContextRUISSet
	 */
	public ContextRuisSet() {
		crs = new Hashtable<String, RuleUseInfoSet>();
	}

	/**
	 * Add a new ContextRUIS to the ContextRUISSet
	 * 
	 * @param c
	 *            ContextRUIS
	 */
	public void putIn(RuleUseInfoSet c) {
		// crs.add(c);
		crs.put(c.getContext(), c);
	}

	/**
	 * Return the number of ContextRUIS in the ContextRUISSet
	 * 
	 * @return int
	 */
	public int cardinality() {
		return crs.size();
	}


	/**
	 * Clears the ContextRUISet
	 */
	public void clear() {
		crs.clear();
	}
	
	
}
