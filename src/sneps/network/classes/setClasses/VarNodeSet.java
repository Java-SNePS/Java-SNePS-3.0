package sneps.network.classes.setClasses;

import java.util.Iterator;
import java.util.Vector;

import sneps.network.VariableNode;

/**
 * @className VarNodeSet.java
 * 
 * @author Amgad Ashraf
 * 
 * @version 3.00 31/5/2018
 */
public class VarNodeSet implements Iterable<VariableNode> {
	protected Vector<VariableNode> variables;

	public VarNodeSet() {
		variables = new Vector<VariableNode>();
	}

	@Override
	public Iterator<VariableNode> iterator() {
		return variables.iterator();
	}

	public VariableNode getVarNode(int index){
		return variables.get(index);
	}

	public void addVarNode(VariableNode n) {
		if(!(variables.contains(n))) {
			variables.add(n);
		}
	}

	public void addAll(VarNodeSet allVariables) {
		for (int i = 0; i < variables.size(); i++) {
			this.addVarNode(allVariables.getVarNode(i));
		}		
	}

	public int size() {
		return variables.size();
	}

	public boolean contains(VariableNode v){
		return variables.contains(v);
	}

	public boolean isEmpty() {
		return variables.isEmpty();
	}

	public void remove(VariableNode variable) {
		variables.remove(variable);
	}

	public static VarNodeSet union(VarNodeSet v1, VarNodeSet v2){
		VarNodeSet v3 = v2;
		for(VariableNode var : v1)
			if(!v3.contains(var))
				v3.addVarNode(var);
		return v3;
	}
}
