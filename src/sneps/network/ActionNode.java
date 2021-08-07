package sneps.network;
import sneps.network.classes.term.Term;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Base;

public abstract class ActionNode extends Node {

	
	private boolean primitive;
	
	public ActionNode() {
		
		
	}
	
	public ActionNode(String identifier) {
		super(Semantic.action,new Base(identifier));
		
	}
	/*
	 * public ActionNode(Semantic semantic,Term trm) { super(semantic,trm);
	 * 
	 * }
	 */
	
	public abstract void act(ActNode actNode);
	
	
	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}

	public boolean isPrimitive() {
		return primitive;
	}
}
