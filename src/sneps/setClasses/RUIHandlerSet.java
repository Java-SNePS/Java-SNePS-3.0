package sneps.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.RuisHandler;

public class RUIHandlerSet implements Iterable<RuisHandler> {
	private HashSet<RuisHandler> handlers;

	public RUIHandlerSet() {
		handlers = new HashSet<RuisHandler>();
	}
	
	@Override
	public Iterator<RuisHandler> iterator(){
		return handlers.iterator();
	}
	
	public boolean contains(RuisHandler handler){
		return handlers.contains(handler);
	}
	
	public void remove(RuisHandler hand){
		handlers.remove(hand);
	}

	public void clear() {
		handlers.clear();
	}

	public void addHandler(RuisHandler cRuis) {
		handlers.add(cRuis);
	}

	public boolean isEmpty() {
		return handlers.isEmpty();
	}
}
