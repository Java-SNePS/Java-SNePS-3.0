/**
 * @className FUnitPath.java
 * 
 * @ClassDescription A forward unit path exists between two nodes A 
 * 	and B, if there is a single arc going from A to B labeled with 
 * 	the relation specified in the forward unit path. This class extends
 * 	Path.
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.paths;

import java.util.LinkedList;

import sneps.setClasses.NodeSet;
import sneps.snebr.Context;
import sneps.network.classes.PathTrace;
import sneps.network.classes.Relation;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.Node;
import sneps.network.classes.term.Molecular;

public class FUnitPath extends Path {
	
	/**
	 * The relation that labels the arc of this forward unit path
	 */
	private Relation relation;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param relation
	 * 			the relation that labels the arc of the
	 * 			forward unit path.
	 */
	public FUnitPath(Relation relation){
		this.relation = relation;
	}
	
	/**
	 * 
	 * @return the relation of the current forward unit
	 * 	path
	 */
	public Relation getRelation(){
		return this.relation;
	}
	
	/** (non-Javadoc)
     * @see sneps.Paths.Path#follow(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> follow(Node node, PathTrace trace, Context context) 
	{	
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		System.out.println(node);
		if (node.getSyntacticSuperClass().equals("Molecular")){
			Molecular mNode = (Molecular) node.getTerm(); 
			 DownCableSet dSet = mNode.getDownCableSet();
			 DownCable dCable = dSet.getDownCable(this.relation.getName());
			 if (dCable != null){
				 NodeSet ns = dCable.getNodeSet();
				 for (int i = 0; i < ns.size(); i++){
					 Node n = ns.getNode(i);
					 PathTrace t = trace.clone();
					 t.compose(new FUnitPath(this.relation));
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
		return new BUnitPath(this.relation).follow(node, trace, context);
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#clone(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public FUnitPath clone() {
		return new FUnitPath(this.relation);
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#equals(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public boolean equals(Object obj) {
		if(!obj.getClass().getSimpleName().equals("FUnitPath"))
			return false;
		if(!((FUnitPath)obj).getRelation().equals(this.relation))
			return false;
		return true;
	}
	
	/**
	 * This method overrides the toString method inherited from the 
	 * Object class.
	 */
	@Override
	public String toString(){
		return this.relation.toString();
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#converse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public Path converse() {
		return new BUnitPath(this.relation);
	}
	
	public LinkedList<Relation> firstRelations() {
		LinkedList<Relation> r= new LinkedList<Relation>();
		r.add(relation);
		return r;
			
		}

}
