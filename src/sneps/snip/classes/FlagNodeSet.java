package sneps.snip.classes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



public class FlagNodeSet implements Iterable<FlagNode> {

	private Set<FlagNode> fns;


	/**
	 * Create a new flag node set
	 */
	public FlagNodeSet() {
		fns = new HashSet<FlagNode>();
	}


	/**
	 * Check if the flag node set is new (empty)
	 * 
	 * @return true or false
	 */
	public boolean isEmpty() {
		return fns.isEmpty();
	}

	/**
	 * Check if fn is in this
	 * 
	 * @param fn
	 *            FlagNode
	 * @return true or false
	 */
	public boolean isMember(FlagNode fn) {
		
		for (FlagNode tFn : fns) {
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
			fns.add(fn);
	}

	
	/**
	 * Return the number of flagged nodes in this set
	 * 
	 * @return int
	 */
	public int cardinality() {
		return fns.size();
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
		
		for (FlagNode fn : fns) {
			res.insert(fn);
		}
		for (FlagNode fn : f) {
			res.insert(fn);
		}
		return res;
	}

	@Override
	public Iterator<FlagNode> iterator() {
		return fns.iterator();
	}
	
}
