package sneps.network.classes.term;

import java.io.Serializable;
import java.util.Enumeration;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.Relation;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.VarNodeSet;

public class Open extends Molecular implements Serializable{
	protected VarNodeSet variables;

	/**
	 * The constructor of this class.
	 * 
	 * @param identifier
	 * 			the name of the node that will be created.
	 * @param downCableSet
	 * 			the down cable set of the node that will be
	 * 			created.
	 */
	public Open(String identifier, DownCableSet dCableSet) {
		super(identifier, dCableSet);
		this.variables = new VarNodeSet();
		this.updateFreeVariables();
	}
	
	public Open(String id) {
		super(id);
		this.variables = new VarNodeSet();
	}
	
	public VarNodeSet getFreeVariables() {
		return variables;
	}
	
	public void addFreeVariable(VariableNode n) {
		variables.addVarNode(n);
	}

	/**
	 * The method that populate the list of free variables by the 
	 * 	free variables dominated by the current node.
	 */
	public void updateFreeVariables(){
		DownCableSet dCableSet = this.getDownCableSet();
		Enumeration<DownCable> dCables = dCableSet.getDownCables().elements();
		while (dCables.hasMoreElements()){
			DownCable dCable = dCables.nextElement();
			NodeSet ns = dCable.getNodeSet();
			Relation r = dCable.getRelation();
			for (int j = 0; j < ns.size(); j++){
				// if node is variable node
				String nodeType = ns.getNode(j).getSyntacticType();
				if (nodeType.equals("Variable") && !r.isQuantifier())
					this.variables.addVarNode((VariableNode)ns.getNode(j));
				// if node is pattern node (means it dominates free variables)
				if (nodeType.equals("Open")){
					Open open = (Open) ns.getNode(j).getTerm();
					VarNodeSet patternFVars = new VarNodeSet();
					patternFVars.addAll(open.getFreeVariables());
					// looping over free variables of the pattern node
					for (int k = 0; k < patternFVars.size(); k++){
						VariableNode variableNode = patternFVars.getVarNode(k);
						// looping over the down cables
						Enumeration<DownCable> dCs = dCableSet.getDownCables().elements();
						while(dCs.hasMoreElements()){
							DownCable d = dCs.nextElement();
							VarNodeSet vars = new VarNodeSet();
							NodeSet nodes = d.getNodeSet();
							for(Node node : nodes){
								Term term = node.getTerm();
								if(term instanceof Variable)
									vars.addVarNode((VariableNode) node);
								if(term instanceof Open)
									vars.addAll(((Open)term).getFreeVariables());
							}
							if (vars.contains(variableNode))
								patternFVars.remove(variableNode);
						}
					}
					this.variables.addAll(patternFVars);
				}
			}
		}
	}

}
