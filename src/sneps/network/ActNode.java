package sneps.network;

import java.io.Serializable;

import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;

public class ActNode extends Node implements Serializable{

	public ActNode() {}
	
	public ActNode(Term syn) {
		super(syn);
	}

	public ActNode(Semantic sem, Term term) {
		super(sem, term);
	}

	public ActNode getAgenda() {
		// TODO Auto-generated method stub
		return null;
	}

	public void processIntends() {
		// TODO Auto-generated method stub
		
	}

}
