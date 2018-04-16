package sneps.snip.classes;

import sneps.setClasses.RuleUseInfoSet;

public abstract class RuisHandler {
		protected String context;

	public RuisHandler(String contextID) {
		this.context = contextID;
	}

	public String getContext() {
		return context;
	}

	abstract public RuleUseInfoSet insertRUI(RuleUseInfo rui);
}
