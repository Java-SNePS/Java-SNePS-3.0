package sneps.network;

import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;

public class ActNode extends Node {

	public ActNode() {}
	
	public ActNode(Term syn) {
		super(syn);
	}
	
	public ActNode(Semantic sem, Term syn) {
		super(sem,syn);
	}

	public ActNode getAgenda() {
		// TODO Auto-generated method stub
		return null;
	}

	public void processIntends() {
		// TODO Auto-generated method stub
		
	}

}
