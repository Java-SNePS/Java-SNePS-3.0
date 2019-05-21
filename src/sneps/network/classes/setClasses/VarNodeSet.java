package sneps.network.classes.setClasses;

import java.util.Iterator;
import java.util.Vector;

import sneps.network.VariableNode;

public class VarNodeSet implements Iterable<VariableNode> {
	protected Vector<VariableNode> variables;

	public VarNodeSet() {
		variables = new Vector<VariableNode>();
	}

	@Override
	public Iterator<VariableNode> iterator() {
		return variables.iterator();
	}

	public VariableNode getVarNode(int index) {
		return variables.get(index);
	}

	public void addVarNode(VariableNode n) {
		if(!(variables.contains(n))) {
			variables.add(n);
		}
	}

	public void addAll(VarNodeSet allVariables) {
		for (VariableNode v : allVariables)
			this.addVarNode(v);		
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

	public static VarNodeSet union(VarNodeSet v1, VarNodeSet v2) {
		VarNodeSet union = new VarNodeSet();
		for(VariableNode v : v1)
			union.addVarNode(v);
		for(VariableNode v : v2)
			union.addVarNode(v);
		
		return union;
	}
	
	public String toString() {
		String res = null;
		for(VariableNode v : variables) {
			if(res == null)
				res = v + " ";
			else
				res += v + " ";
		}
		return res;
	}
}
