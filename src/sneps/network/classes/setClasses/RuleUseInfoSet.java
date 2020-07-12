package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuleUseInfo;

public class RuleUseInfoSet implements RuisHandler, Iterable<RuleUseInfo> {
	private HashSet<RuleUseInfo> ruis;

	/**
	 * A boolean indicating whether this RuleUseInfoSet is treated as a single
	 * RuleUseInfo.
	 */
	private boolean singleton;

	public RuleUseInfoSet() {
		ruis = new HashSet<RuleUseInfo>();
	}

	public RuleUseInfoSet(boolean singleton) {
		ruis = new HashSet<RuleUseInfo>();
		this.singleton = singleton;
	}

	@Override
	public Iterator<RuleUseInfo> iterator() {
		return ruis.iterator();
	}

	public RuleUseInfoSet add(RuleUseInfo rui) {
		ruis.add(rui);
		return this;
	}

	@Override
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		//In case of inserting into a RUI set
		if(!singleton)
			return combineAdd(rui);

		//In case of inserting into a singleton RUI
		if(isEmpty())
			return add(rui);

		ruis = combine(rui).getRuis();
		return this;
	}

	/**
	 * Combines rui with every RUI in this RUISet.
	 * @param rui
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
	 * Combines every RUI in this RUISet with every RUI in second.
	 * @param second
	 * 		RUISet
	 * @return
	 * 		Combined RUISet
	 */
	public RuleUseInfoSet combine(RuleUseInfoSet second) {
		RuleUseInfoSet res = new RuleUseInfoSet(false);
		for(RuleUseInfo rui1 : this){
			for(RuleUseInfo rui2 : second){
				//if(rui1.isDisjoint(rui2))
					res.add(rui1.combine(rui2));
			}
		}

		return res;
	}

	/**
	 * Adds all the RUIs in rootRUIS to this RUISet.
	 * @param rootRUIS
	 * @return boolean
	 */
	public RuleUseInfoSet addAll(RuleUseInfoSet rootRUIS) {
		for(RuleUseInfo rui : rootRUIS)
			ruis.add(rui);

		return this;
	}

	public RuleUseInfoSet combineAdd(RuleUseInfo rui) {
		RuleUseInfoSet temp = this.combine(rui);
		temp.add(rui);
		this.addAll(temp);

		return this;
	}

	public boolean contains(RuleUseInfo rui){
		return ruis.contains(rui);
	}

	public boolean isEmpty() {
		return ruis.isEmpty();
	}

	public int size() {
		return ruis.size();
	}

	public void clear() {
		ruis.clear();
	}

	public HashSet<RuleUseInfo> getRuis() {
		return ruis;
	}

	public String toString() {
		String res = null;
		for(RuleUseInfo rui : ruis) {
			res = rui.toString() + " ";
		}

		return res;
	}

	@Override
	public RuleUseInfoSet combineConstantRUI(RuleUseInfo rui) {
		return combine(rui);
	}
}
