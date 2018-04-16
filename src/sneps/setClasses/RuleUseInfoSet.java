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

	public RuleUseInfoSet() {}

	@Override
	public Iterator<RuleUseInfo> iterator() {
		return ruis.iterator();
	}

	
	public void add(RuleUseInfo rui) {
		ruis.add(rui);
	}

	public RuleUseInfoSet combine(RuleUseInfoSet second) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleUseInfoSet insertRUI(RuleUseInfo rui) {
		ruis.add(rui);
		return this;
	}

}
