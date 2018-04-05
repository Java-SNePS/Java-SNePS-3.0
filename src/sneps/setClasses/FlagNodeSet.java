package sneps.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.FlagNode;

public class FlagNodeSet implements Iterable<FlagNode> {
	private HashSet<FlagNode> flagNodes;

	@Override
	public Iterator<FlagNode> iterator() {
		return flagNodes.iterator();
	}

	public void putIn(FlagNode fn) {
		flagNodes.add(fn);
	}
	
	/**
	 * Check if the flag node set is new (empty)
	 * 
	 * @return true or false
	 */
	public boolean isNew() {
		return flagNodes.isEmpty();
	}

	

	/**
	 * Check if fn is in this
	 * 
	 * @param fn
	 *            FlagNode
	 * @return true or false
	 */
	public boolean isMember(FlagNode fn) {
		
		for (FlagNode tFn : flagNodes) {
			if (tFn.isEqual(fn))
				return true;
		}
		return false;
		
	}

	/**
	 * Insert fn in the flag node set if it is not in
	 * 
	 * @param fn
	 *            FlagNode
	 */
	public void insert(FlagNode fn) {
		if (!this.isMember(fn))
			flagNodes.add(fn);
	}

	

	/**
	 * Return the number of flagged nodes in this set
	 * 
	 * @return int
	 */
	public int cardinality() {
		return flagNodes.size();
	}

	/**
	 * Create a new FlagNodeSet and merge this and f in it
	 * 
	 * @param f
	 *            FlagNodeSet
	 * @return FlagNodeSet
	 */
	public FlagNodeSet union(FlagNodeSet f) {
		FlagNodeSet res = new FlagNodeSet();
		
		for (FlagNode fn : flagNodes) {
			res.insert(fn);
		}
		for (FlagNode fn : f) {
			res.insert(fn);
		}
		return res;
	}

}
