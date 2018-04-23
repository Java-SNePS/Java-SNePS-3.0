package sneps.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuisHandler;

public class ContextRuisSet implements Iterable<RuisHandler> {
	private HashSet<RuisHandler> ruisHandlers;

	public ContextRuisSet() {
		ruisHandlers = new HashSet<RuisHandler>();
	}

	@Override
	public Iterator<RuisHandler> iterator() {
		return ruisHandlers.iterator();
	}

	public void addChannel(RuisHandler newChannel) {
		ruisHandlers.add(newChannel);
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public void putIn(RuisHandler cRuis) {
		// TODO Auto-generated method stub
		
	}

	public boolean hasContext(String contextID) {
		// TODO Auto-generated method stub
		return false;
	}

	public RuisHandler getContextRUIS(String contextID) {
		// TODO Auto-generated method stub
		return null;
	}

}
