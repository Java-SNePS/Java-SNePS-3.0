package sneps.network.classes.setClasses;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import sneps.network.classes.term.Variable;

public class VariableSet implements Iterable<Variable>, Serializable {
	protected Vector<Variable> variables;

	public VariableSet() {
		variables = new Vector<Variable>();
	}

	@Override
	public Iterator<Variable> iterator() {
		return variables.iterator();
	}

	public Variable getVariable(int index) {
		return variables.get(index);
	}

	public void addVariable(Variable n) {
		variables.add(n);
	}

	public void addAll(VariableSet allVariables) {
		for (int i = 0; i < variables.size(); i++) {
			this.addVariable(allVariables.getVariable(i));
		}
	}

	public int size() {
		return variables.size();
	}

	public boolean isEmpty() {
		return variables.isEmpty();
	}

	/***
	 * Method to iterate over all variables in the Vector<Variable> to check for
	 * their bound status
	 * 
	 * @return boolean expressing no none-bound free variables
	 */
	public boolean areVariablesBound() {
		// TODO Youssef
		return false;
	}
}
