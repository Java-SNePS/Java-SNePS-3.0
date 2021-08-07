package sneps.network.classes;

import java.io.Serializable;
import java.util.LinkedList;

public class CFSignature implements Serializable{

	/**
	 * a string specifying the name of the resulting semantic class that will replace the
	 * 	default semantic class specified by the case frame if all the constraints specified
	 * 	in the current case frame signature were satisfied.
	 */
	private String resultingType;
	
	/**
	 * a linked list of constraints on the semantic type and number of nodes pointed to 
	 * 	be some or all the relations included in the case frame that has this CFSignature.
	 */
	private LinkedList<SubDomainConstraint> sdConstraints;
	
	/**
	 * a string id for the current case frame signature.
	 */
	private String id;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param result
	 * 			the name of the resulting semantic class specified by the 
	 * 			case frame signature.
	 * @param rules
	 * 			the list of constraints specified by the case frame signature.
	 * @param caseframeId
	 * 			the string id of the case frame having this case frame signature.
	 * 			(The case frame id will be used in generating the id  of this 
	 * 			case frame signature).
	 */
	public CFSignature(String result, LinkedList<SubDomainConstraint> rules, String caseframeId){
		this.resultingType = result;
		this.sdConstraints = rules;
		generateId(caseframeId);
	}
	
	/**
     * @return the name of the semantic class specified by the current 
     * 	case frame signature.
     */
	public String getResultingType(){
		return this.resultingType;
	}
	
	/**
     * @return the linked list of constraints specified by the current
     * 	case frame signature.
     */
	public LinkedList<SubDomainConstraint> getSubDomainConstraints(){
		return this.sdConstraints;
	}
	
	/**
     * @return the string id of the current case frame signature.
     */
	public String getId(){
		return this.id;
	}
	
	/**
     * This method is invoked from the constructor of this class to generate the id of
     * 	the newly created case frame signature.
     */
	private void generateId(String caseframeId){
		String s = "";
		String[] relations = caseframeId.split(",");
		for(int i = 0; i < relations.length; i++){
			boolean found = false;
			for(int j = 0; j < sdConstraints.size(); j++){
				if(sdConstraints.get(j).getRelation().equals(relations[i])){
					found = true;
					s += sdConstraints.get(j).getId();
				}
			}
			if(!found)
				s += "*";
			if(i < relations.length -1)
				s += ";";
			if(i == relations.length -1)
				s += "/" + this.resultingType;
		}
		this.id = s;
	}

}
