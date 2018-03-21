package sneps.network;

import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;

public class VariableNode extends Node {
	private VariableSet freeVariables;

	public VariableNode() {}

	public VariableNode(Term trm) {
		super(trm);
	}

	public VariableNode(Semantic sem) {
		super(sem);
	}

	public VariableNode(Semantic sem, Term trm) {
		super(sem, trm);
	}
	

	public boolean hasSameFreeVariablesAs(VariableNode node) {
		int i=0;
		for(Variable var : freeVariables){
			if(!var.equals(node.getFreeVariables().getVariable(i))){
				return false;
			}else{
				i++;
			}
		}
		return true;
	}

	public VariableSet getFreeVariables() {
		return freeVariables;
	}
}
