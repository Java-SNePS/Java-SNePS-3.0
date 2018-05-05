package sneps.setClasses;

import java.util.Hashtable;

public class ContextRuisSet{
	private Hashtable<String, RUIHandlerSet> ruisHandlers;

	public ContextRuisSet() {
		ruisHandlers = new Hashtable<String, RUIHandlerSet>();
	}

	public void addHandlerSet(String contextName, RUIHandlerSet handlerSet){
		ruisHandlers.put(contextName, handlerSet);
	}
	
	public boolean hasContext(String contextID) {
		RUIHandlerSet set = ruisHandlers.get(contextID);
		if(set == null || set.isEmpty())
			return false;
		return true;
	}

	public RUIHandlerSet getHandlerSet(String contextID) {
		RUIHandlerSet set = ruisHandlers.get(contextID);
		if(set == null || set.isEmpty())
			set = new RUIHandlerSet();
		return set;
	}

}
