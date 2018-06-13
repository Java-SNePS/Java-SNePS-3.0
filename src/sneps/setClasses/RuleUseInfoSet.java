package sneps.setClasses;

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

	public RuleUseInfoSet(String contextName, boolean b) {
		super(contextName);
		ruis = new HashSet<RuleUseInfo>();
	}

	public RuleUseInfoSet() {
		ruis = new HashSet<RuleUseInfo>();
	}

	@Override
	public Iterator<RuleUseInfo> iterator() {
		return ruis.iterator();
	}

	public boolean add(RuleUseInfo rui) {
		return ruis.add(rui);
	}

	public RuleUseInfoSet combine(RuleUseInfoSet second) {
		RuleUseInfoSet res = new RuleUseInfoSet(this.context, false);
		for(RuleUseInfo rui1 : this){
			for(RuleUseInfo rui2: second){
				if(rui1.isDisjoint(rui2))
					res.add(rui1.combine(rui2));
			}
		}
		return res;
	}

	@Override
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		ruis.add(rui);
		return this;
	}

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
