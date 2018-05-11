package sneps.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

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

	public void add(RuleUseInfo rui) {
		ruis.add(rui);
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

	public void addAll(RuleUseInfoSet rootRUIS) {
		ruis.addAll(ruis);
	}

	public boolean contains(RuleUseInfo rui){
		return ruis.contains(rui);
	}

	public boolean isEmpty() {
		return ruis.isEmpty();
	}
}
