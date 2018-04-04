package sneps.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;

public class RuleUseInfoSet extends RuisHandler implements Iterable<RuleUseInfo> {
	private HashSet<RuleUseInfo> ruis;

	public RuleUseInfoSet(String contextName, boolean b) {
		// TODO Auto-generated constructor stub
	}

	public RuleUseInfoSet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Iterator<RuleUseInfo> iterator() {
		return ruis.iterator();
	}

	
	public void add(RuleUseInfo rui) {
		ruis.add(rui);
	}

}
