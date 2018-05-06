package sneps.setClasses;

import java.util.Enumeration;
import java.util.Hashtable;

import sneps.snip.classes.RuisHandler;

public class ContextRuisSet{
	private Hashtable<String, RuisHandler> ruisHandlers;

	public ContextRuisSet() {
		ruisHandlers = new Hashtable<String, RuisHandler>();
	}

	public RuisHandler addHandlerSet(String contextName, RuisHandler handler){
		return ruisHandlers.put(contextName, handler);
	}
	
	public boolean hasContext(String contextID) {
		RuisHandler set = ruisHandlers.get(contextID);
		if(set == null)
			return false;
		return true;
	}

	public RuisHandler getByContext(String contextID) {
		return ruisHandlers.get(contextID);
	}
	
	public boolean contains(RuisHandler handler){
		return ruisHandlers.contains(handler);
	}
	public boolean containsKey(String handlerKey){
		return ruisHandlers.containsKey(handlerKey);
	}
	public RuisHandler getHandler(RuisHandler handler){
		return ruisHandlers.get(handler);
	}
	public Enumeration<String> getKeys(){
		return ruisHandlers.keys();
	}
	public int size(){
		return ruisHandlers.size();
	}
	public void remove(String contextName, RuisHandler handler){
		ruisHandlers.remove(handler, handler);
	}

	public void clear() {
		ruisHandlers.clear();
	}
}
