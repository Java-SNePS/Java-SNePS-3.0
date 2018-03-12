package sneps.network.classes.term;

import java.util.LinkedList;

import sneps.network.Node;

public class Open extends Molecular {

	private LinkedList<Node> freeVariables;
	
	public Open(String idenitifier) {
		super(idenitifier);
	}

	/**
	 * 
	 * @return the list of free variables dominated by the current 
	 */
	public LinkedList<Node> getFreeVariables(){
		return this.freeVariables;
	}
	
}
