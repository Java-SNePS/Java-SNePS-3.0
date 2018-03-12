package sneps.network.classes;

public class CableTypeConstraint {

	/**
	 * The name of the semantic class representing the semantic type 
	 * specified by this cable type constraint.
	 */
	private String semanticType;
	
	/**
	 * The minimum number of nodes that should be having the semantic 
	 * 	type specified by this cable type constraint and having the arcs 
	 * 	labeled with the relation specified in the sub-domain constraint 
	 * 	pointing to them. 
	 * 
	 * If (min = null), then all the nodes, pointed to by the arcs labeled
	 * 	with the relation specified in the sub-domain constraint, should
	 * 	have the semantic type specified by this cable type constraint.
	 */
	private Integer min;
	
	/**
	 * The maximum number of nodes that should be having the semantic type 
	 * 	specified by this cable type constraint and having the arcs labeled 
	 * 	with the relation specified in the sub-domain constraint pointing to 
	 * 	them. 
	 * 
	 * If (max = null), then no maximum limit is specified.
	 */
	private Integer max;
	
	/**
	 * The id of the current cable type constraint.
	 */
	private String id;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param semantic
	 * 			the semantic type specified in the cable type constraint.
	 * @param minimum
	 * 			the minimum number of nodes specified in the cable type
	 * 			constraint. 
	 * @param maximum
	 * 			the maximum number of nodes specified in the cable type
	 * 			constraint.
	 */
	public CableTypeConstraint(String semantic, Integer minimum, Integer maximum){
		this.semanticType = semantic;
		this.min = minimum;
		this.max = maximum;
		this.id = semantic + "," + minimum + "," + maximum;
	}
	

	/**
	 * 
	 * @return the semantic type specified in the current cable type constraint.
	 */
	public String getSemanticType(){
		return this.semanticType;
	}
	
	/**
	 * 
	 * @return the lower limit specified in the current cable type constraint.
	 */
	public Integer getLowerLimit(){
		return this.min;
	}
	
	/**
	 * 
	 * @return the upper limit specified in the current cable type constraint.
	 */
	public Integer getUpperLimit(){
		return this.max;
	}
	
	/**
	 * 
	 * @return the ID of the current cable type constraint.
	 */
	public String getId(){
		return this.id;
	}
	

}
