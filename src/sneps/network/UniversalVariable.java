package sneps.network;

import java.io.Serializable;

import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;

public class UniversalVariable extends VariableNode implements Serializable {
	
	
	
	
	
	public UniversalVariable(){
		snepslogFlag = false;
	}
	
	public UniversalVariable(Term trm){
		super(trm);
		snepslogFlag = false;
		
	}
	
	public UniversalVariable(Semantic sem) {
		super(sem);
		snepslogFlag = false;
	}

	public UniversalVariable(Semantic sem, Term trm) {
		super(sem, trm);
		snepslogFlag = false;
	}
	
	
public static boolean subsume(UniversalVariable x, Node y){
		
		if(x.equals(y))
			return true;
		
		if(reduce(x,y))
			return true;
		
		
		
		
		
		
		return false;
		
	}

	

}
