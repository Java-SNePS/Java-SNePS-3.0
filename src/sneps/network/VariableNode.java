package sneps.network;

import java.awt.image.RescaleOp;
import java.io.Serializable;
import java.util.ArrayList;

import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.VariableSet;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;

public class VariableNode extends Node implements Serializable{
	
	private VariableSet freeVariables;

	public boolean snepslogFlag;
	
	
	/** the array list restrictions represent the restrictions of 
	 * the structured variables and is initialized in the basic variable 
	 * with null
	 */
	public ArrayList<PropositionNode> restrictions;

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
	
	/**This method returns an gets the restrictions of the node
	 * 
	 * @return arraylist of the restrictions of the node
	 */
	public ArrayList<PropositionNode> getRestrictions(){
		return restrictions;
	}
}
