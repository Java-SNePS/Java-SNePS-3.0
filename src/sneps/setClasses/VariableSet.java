package sneps.setClasses;

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

	public Variable getVariable(int index){
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

	public boolean contains(Variable v){
		return variables.contains(v);
	}

	public boolean isEmpty() {
		return variables.isEmpty();
	}

	public void remove(Variable variable) {
		variables.remove(variable);
	}

	public static VariableSet union(VariableSet v1, VariableSet v2){
		VariableSet v3 = v2;
		for(Variable var : v1)
			if(!v3.contains(var))
				v3.addVariable(var);
		return v3;
	}
}
