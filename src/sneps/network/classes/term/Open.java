package sneps.network.classes.term;

import java.util.Enumeration;
import java.util.LinkedList;

import sneps.network.Node;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.Relation;
import sneps.network.classes.setClasses.NodeSet;

public class Open extends Molecular {

	private LinkedList<Node> freeVariables;

	/**
	 * The constructor of this class.
	 * 
	 * @param identifier
	 * 			the name of the node that will be created.
	 * @param downCableSet
	 * 			the down cable set of the node that will be
	 * 			created.
	 */
	public Open(String identifier, DownCableSet downCableSet){
		super(identifier, downCableSet);
		this.freeVariables = new LinkedList<Node>();
		this.updateFreeVariables();
	}
	
	/**
	 * 
	 * @return the list of free variables dominated by the current 
	 */
	public LinkedList<Node> getFreeVariables(){
		return this.freeVariables;
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
					this.freeVariables.add((Node) ns.getNode(j));
				// if node is pattern node (means it dominates free variables)
				if (nodeType.equals("Pattern")){
					Open open = (Open) ns.getNode(j).getSyntactic();
					LinkedList<Node> patternFVars = new LinkedList<Node>();
					patternFVars.addAll(open.getFreeVariables());
					// looping over free variables of the pattern node
					for (int k = 0; k < patternFVars.size(); k++){
						Node vNode = patternFVars.get(k);
						// looping over the down cables
						Enumeration<DownCable> dCs = dCableSet.getDownCables().elements();
						while(dCs.hasMoreElements()){
							DownCable d = dCs.nextElement();
							if (d.getNodeSet().contains(vNode))
								patternFVars.remove(vNode);
						}
					}
					this.freeVariables.addAll(patternFVars);
				}
			}
		}
	}
	
}
