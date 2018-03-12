package sneps.network;

import java.util.Collections;
import java.util.LinkedList;

public class SubDomainConstraint {
	/**
	 * the name of the relation included in this sub-domain constraint.
	 */
	private String relation;
	
	/**
	 * a list of cableTypeConstraints specifying the constraints on the nodes pointed to 
	 * 	by the arcs labeled with the relation included in this sub-domain constraint. 
	 */
	private LinkedList<CableTypeConstraint> nodeChecks;
	
	/**
	 * the string id of the current sub-domain constraint.
	 */
	private String id;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param relName
	 * 			the name of the relation included in this sub-domain constraint.
	 * @param checks
	 * 			the list of constraints specified by this sub-domain constraint.
	 */
	public SubDomainConstraint(String relName, LinkedList<CableTypeConstraint> checks){
		this.relation = relName;
		this.nodeChecks = checks;
		this.id = generateId();
	}
	
	/**
     * @return the name of the relation included in the current sub-domain constraint.
     */
	public String getRelation(){
		return this.relation;
	}
	
	/**
     * @return the list constraints specified by the current sub-domain constraint.
     */
	public LinkedList<CableTypeConstraint> getNodeChecks(){
		return this.nodeChecks;
	}
	
	/**
     * @return the string id of the current sub-domain constraint.
     */
	public String getId(){
		return this.id;
	}
	
	/**
     * This method is invoked from the constructor of this class to generate the id of
     * 	the newly created sub-domain constraint.
     */
	private String generateId(){
		id = "";
		LinkedList<String> types = new LinkedList<String>();
		for(int i = 0; i < nodeChecks.size(); i++){
			types.add(nodeChecks.get(i).getSemanticType());
		}
		Collections.sort(types);
		for(int i = 0; i < types.size(); i++){
			for (int j = 0; j < nodeChecks.size(); j++){
				if(nodeChecks.get(j).getSemanticType().equals(types.get(i))){
					id += nodeChecks.get(j).getId();
					if(i < nodeChecks.size()-1)
						id += "-";
				}
			}
		}
		return id;
	}
	
}
