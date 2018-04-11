package sneps.network.classes.term;

import java.util.Enumeration;

import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.Relation;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.VariableSet;

public class Open extends Molecular {
	protected VariableSet variables;

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
		this.variables = new VariableSet();
		this.updateFreeVariables();
	}
	public VariableSet getFreeVariables() {
		return variables;
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
					this.variables.addVariable((Variable)ns.getNode(j).getTerm());
				// if node is pattern node (means it dominates free variables)
				if (nodeType.equals("Open")){
					Open open = (Open) ns.getNode(j).getTerm();
					VariableSet patternFVars = new VariableSet();
					patternFVars.addAll(open.getFreeVariables());
					// looping over free variables of the pattern node
					for (int k = 0; k < patternFVars.size(); k++){
						Variable variable = patternFVars.getVariable(k);
						// looping over the down cables
						Enumeration<DownCable> dCs = dCableSet.getDownCables().elements();
						while(dCs.hasMoreElements()){
							DownCable d = dCs.nextElement();
							// TODO implement contains and remove in VariableSet
							/*if (d.getNodeSet().contains(variable))
								patternFVars.remove(variable);*/
						}
					}
					this.variables.addAll(patternFVars);
				}
			}
		}
	}

}
