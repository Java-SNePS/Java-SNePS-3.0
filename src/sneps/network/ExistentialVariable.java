package sneps.network;

import java.io.Serializable;

import sneps.network.classes.Semantic;
import sneps.network.classes.term.Term;

public class ExistentialVariable extends VariableNode implements Serializable {
	
	
	
	
	/**the dependency array is an array of universal variables
	 * the existential variable depend on
	 */
	 
	
	public UniversalVariable[] dependency;
	
	
	public ExistentialVariable(){
		snepslogFlag = false;
	}
	
	public ExistentialVariable(Term trm){
		super(trm);
		snepslogFlag = false;
		
	}
	
	public ExistentialVariable(Semantic sem) {
		super(sem);
		snepslogFlag = false;
	}

	public ExistentialVariable(Semantic sem, Term trm) {
		super(sem, trm);
		snepslogFlag = false;


	}
	
	
	
}	
