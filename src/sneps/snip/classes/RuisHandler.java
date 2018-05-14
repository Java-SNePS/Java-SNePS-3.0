package sneps.snip.classes;

import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.RuleUseInfoSet;

public abstract class RuisHandler {
	protected String context;
	private FlagNodeSet positiveNodes;

	public RuisHandler(){
		positiveNodes = new FlagNodeSet();
	}
	public RuisHandler(String contextID) {
		this();
		this.context = contextID;
	}

	public String getContext() {
		return context;
	}

	abstract public RuleUseInfoSet insertRUI(RuleUseInfo rui);
	public FlagNodeSet getPositiveNodes() {
		return positiveNodes;
	}
	public void setPositiveNodes(FlagNodeSet positiveNodes) {
		this.positiveNodes = positiveNodes;
	}
}
