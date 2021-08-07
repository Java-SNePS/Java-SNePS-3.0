/**
 * @className CFResBUnitPath.java
 * 
 * @ClassDescription A case frame restricted backward unit path is the 
 * 	inverse of the case frame restricted forward unit path, so the 
 * 	restricting case frame must be implemented by the target node instead of the 
 * 	starting node. Thus, this path exists between two nodes A and B, if there 
 * 	is an arc going from B to A, and A must have its down cable set implementing 
 * 	the restricting case frame, with the arc going from B to A labeled with the 
 * 	relation specified in the case frame restricted backward unit path.
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.paths;

import java.io.Serializable;
import java.util.LinkedList;

import sneps.snebr.Context;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.PathTrace;
import sneps.network.classes.Relation;
import sneps.network.cables.UpCable;
import sneps.network.cables.UpCableSet;
import sneps.network.Node;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Molecular;

public class CFResBUnitPath extends Path implements Serializable{

	/**
	 * The relation that labels the arc of this case frame restricted
	 * backward unit path.
	 */
	private Relation relation;
	
	/**
	 * The case frame that this case frame restricted backward unit 
	 * path is restricted to.
	 */
	private CaseFrame caseFrame;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param relation
	 * 			the relation that labels the arc of this path.
	 * @param cf
	 * 			the restricting case frame.
	 */
	public CFResBUnitPath(Relation relation, CaseFrame cf){
		this.relation = relation;
		this.caseFrame = cf;
	}
	
	/**
	 * 
	 * @return the relation of the current path.
	 */
	public Relation getRelation(){
		return this.relation;
	}
	
	/**
	 * 
	 * @return the case frame that the current path is restricted
	 * 	to.
	 */
	public CaseFrame getCaseFrame(){
		return this.caseFrame;
	}
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#follow(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> follow(Node node, PathTrace trace, Context context) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		UpCableSet upSet = node.getUpCableSet();
		UpCable upCable = upSet.getUpCable(this.relation.getName());
		if(upCable != null){
			NodeSet ns = upCable.getNodeSet();
			for(int i = 0; i < ns.size(); i++){
				Node n = ns.getNode(i);
				// check the CaseFrame of the node before adding it
				Molecular mNode = (Molecular) n.getTerm();
				if ((mNode.getDownCableSet().getCaseFrame().
						getId().equals(this.caseFrame.getId())))	{
					PathTrace t = trace.clone();
					t.compose(new CFResBUnitPath(this.relation, this.caseFrame));
					Object[] o = new Object[2];
	                o[0] = n;
	                o[1] = t;
	                result.add(o);
				}
			}
		}
		return result;
	}
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#followConverse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> followConverse(Node node, PathTrace trace, Context context) {
		return new CFResFUnitPath(this.relation, this.caseFrame).follow(node, trace, context);
	}
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#clone(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public CFResBUnitPath clone() {
		return new CFResBUnitPath(this.relation, this.caseFrame);
	}
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#equals(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public boolean equals(Object obj) {
		if(! obj.getClass().getSimpleName().equals("CFResBUnitPath"))
			return false;
		if(! ((CFResBUnitPath)obj).getRelation().equals(this.relation))
			return false;
		if(!((CFResBUnitPath)obj).getCaseFrame().getId().equals(this.caseFrame.getId()))
			return false;
		return true;
	}
	
	/**
	 * This method overrides the toString method inherited from the 
	 * Object class.
	 */
	@Override
	public String toString(){
		return "CaseFrame-restricted (" + this.relation.toString() + "-" + ")";
	}
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#converse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public Path converse() {
		return new CFResFUnitPath(this.relation, this.caseFrame);
	}
	
	public LinkedList<Relation> firstRelations() {
		LinkedList<Relation> r= new LinkedList<Relation>();
		r.add(relation);
		return r;
			
		}
	
}
