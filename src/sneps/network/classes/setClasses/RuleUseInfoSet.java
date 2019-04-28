package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

/**
 * @className RuleUseInfoSet.java
 * 
 * @author Amgad Ashraf
 * 
 * @version 3.00 31/5/2018
 */
public class RuleUseInfoSet extends RuisHandler implements Iterable<RuleUseInfo> {
	private HashSet<RuleUseInfo> ruis;
	
	/**
	 * A boolean indicating whether this RuleUseInfoSet is treated as single 
	 * RuleUseInfo.
	 */
	private boolean singleton;
	
	public RuleUseInfoSet() {
		ruis = new HashSet<RuleUseInfo>();
	}

	public RuleUseInfoSet(String contextName, boolean singleton) {
		super(contextName);
		ruis = new HashSet<RuleUseInfo>();
		this.singleton = singleton;
	}

	@Override
	public Iterator<RuleUseInfo> iterator() {
		return ruis.iterator();
	}

	public boolean add(RuleUseInfo rui) {
		return ruis.add(rui);
	}

	/**
	 * Combines every RUI in this RUISet with every RUI in second.
	 * @param second
	 * 		RUISet
	 * @return
	 * 		Combined RUISet
	 */
	public RuleUseInfoSet combine(RuleUseInfoSet second) {
		RuleUseInfoSet res = new RuleUseInfoSet(this.context, false);
		for(RuleUseInfo rui1 : this){
			for(RuleUseInfo rui2 : second){
				//if(rui1.isDisjoint(rui2))
					res.add(rui1.combine(rui2));
			}
		}
		
		return res;
	}

	@Override
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		if(!singleton) {
			ruis.add(rui);
		}
	}

	/**
	 * Adds all the RUIs in rootRUIS to this RUISet.
	 * @param rootRUIS
	 * @return boolean
	 */
	public boolean addAll(RuleUseInfoSet rootRUIS) {
		boolean flag = true;
		for(RuleUseInfo rui : rootRUIS){
			if(!ruis.add(rui))
				flag = false;
		}
		
		return flag;
	}

	public boolean contains(RuleUseInfo rui){
		return ruis.contains(rui);
	}

	public boolean isEmpty() {
		return ruis.isEmpty();
	}
}
