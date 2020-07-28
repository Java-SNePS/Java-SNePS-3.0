package sneps.network;

import java.io.Serializable;

import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;

public class VariableNode extends Node implements Serializable{
	private VariableSet freeVariables;

	private boolean snepslogFlag;

	public VariableNode() {
		snepslogFlag = false;
	}

	public VariableNode(Term trm) {
		super(trm);
		snepslogFlag = false;
	}

	public VariableNode(Semantic sem) {
		super(sem);
		snepslogFlag = false;
	}

	public VariableNode(Semantic sem, Term trm) {
		super(sem, trm);
		snepslogFlag = false;
	}

	public boolean hasSameFreeVariablesAs(VariableNode node) {
		int i = 0;
		for (Variable var : freeVariables) {
			if (!var.equals(node.getFreeVariables().getVariable(i))) {
				return false;
			} else {
				i++;
			}
		}
		return true;
	}

	public boolean isSnepslogFlag() {
		return snepslogFlag;
	}

	public void setSnepslogFlag(boolean snepslogFlag) {
		this.snepslogFlag = snepslogFlag;
	}

	public VariableSet getFreeVariables() {
		return freeVariables;
	}

	public void updateLevel() {
		int min=1;
		for(int i=0; i<getParentNodes().size();i++) {
			if(min>getParentNodes().getNode(i).getLevel())
				min=getParentNodes().getNode(i).getLevel();
		}
		updateLevel(min-1);
	}
}
