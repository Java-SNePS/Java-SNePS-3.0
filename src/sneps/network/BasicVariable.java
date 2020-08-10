package sneps.network;

import java.io.Serializable;

import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;

public class BasicVariable extends VariableNode implements Serializable {
	


	
	
	public BasicVariable(){
		
		restrictions = null;
		snepslogFlag = false;
		
	}
	
	public BasicVariable(Term trm){
		super(trm);
		restrictions = null;
		snepslogFlag = false;
		
	}
	
	public BasicVariable(Semantic sem) {
		super(sem);
		restrictions = null;
		snepslogFlag = false;
	}

	public BasicVariable(Semantic sem, Term trm) {
		super(sem, trm);
		restrictions = null;
		snepslogFlag = false;


	}

}
