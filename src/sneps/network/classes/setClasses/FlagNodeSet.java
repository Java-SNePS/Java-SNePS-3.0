package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.FlagNode;

public class FlagNodeSet implements Iterable<FlagNode> {
	private HashSet<FlagNode> flagNodes;

	public FlagNodeSet(){
		flagNodes = new HashSet<FlagNode>();
	}

	@Override
	public Iterator<FlagNode> iterator() {
		return flagNodes.iterator();
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
	 * Insert fn in the flag node set if it is not in
	 * 
	 * @param fn
	 *            FlagNode
	 */
	public void insert(FlagNode fn) {
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

	public int size() {
		return flagNodes.size();
	}
	
	/**
	 * Check if fn is in this
	 * 
	 * @param fn
	 *            FlagNode
	 * @return true or false
	 */
	public boolean contains(FlagNode fn){
		if(flagNodes.isEmpty())
			return false;
		return flagNodes.contains(fn);
	}

	public void addAll(FlagNodeSet fns) {
		for(FlagNode fn : fns)
			flagNodes.add(fn);
	}
	
	public String toString() {
		String res = null;
		for(FlagNode fn : flagNodes) {
			if(res != null)
				res += fn.toString() + "\n";
			else
				res = fn.toString() + "\n";
		}
		
		return res;
	}
	
	public NodeSet getAllNodes() {
		NodeSet res = new NodeSet();
		for(FlagNode fn : flagNodes) {
			res.addNode(fn.getNode());
		}
		
		return res;
	}
	
	public void clear() {
		flagNodes.clear();
	}
}
