package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuleUseInfoSet;

public class ContextRuisSet implements Iterable<RuleUseInfoSet> {
	private HashSet<RuleUseInfoSet> RuleUseInfoSets;

	public ContextRuisSet() {
		RuleUseInfoSets = new HashSet<RuleUseInfoSet>();
	}

	@Override
	public Iterator<RuleUseInfoSet> iterator() {
		return RuleUseInfoSets.iterator();
	}

	public void addChannel(RuleUseInfoSet newChannel) {
		RuleUseInfoSets.add(newChannel);
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public void putIn(sneps.network.classes.setClasses.RuleUseInfoSet cRuis) {
		// TODO Auto-generated method stub
		
	}

	public boolean hasContext(String contextID) {
		// TODO Auto-generated method stub
		return false;
	}

	public sneps.network.classes.setClasses.RuleUseInfoSet getContextRUIS(String contextID) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
