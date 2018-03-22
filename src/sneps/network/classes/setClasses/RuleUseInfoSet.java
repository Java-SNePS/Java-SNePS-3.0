package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuleUseInfo;

public class RuleUseInfoSet implements Iterable<RuleUseInfo> {
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

	public RuleUseInfoSet add(RuleUseInfo r) {
		ruis.add(r);
		return null;
	}

}
