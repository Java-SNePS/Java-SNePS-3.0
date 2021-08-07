/**
 * @className EmptyPath.java
 * 
 * @ClassDescription An empty path is a sequence of zero arcs.
 * 	This class extends Path and has no instance variables. 
 * 
 * @author Nourhan Zakaria
 * @version 2.00 18/6/2014
 */
package sneps.network.paths;

import java.io.Serializable;
import java.util.LinkedList;

import sneps.snebr.Context;
import sneps.network.classes.PathTrace;
import sneps.network.classes.Relation;
import sneps.network.Node;

public class EmptyPath extends Path implements Serializable{

	/** (non-Javadoc)
     * @see sneps.Paths.Path#follow(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> follow(Node node, PathTrace trace, Context context) {
		
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		Object[] o = new Object[2];
		o[0] = node;
		o[1] = trace;
		result.add(o);
		
		return result;	
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#followConverse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> followConverse(Node node, PathTrace trace, Context context) {
	
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		Object[] o = new Object[2];
		o[0] = node;
		o[1] = trace;
		result.add(o);
		
		return result;
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#clone(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public EmptyPath clone() {
		return new EmptyPath();
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#equals(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public boolean equals(Object obj) {
		return obj.getClass().getSimpleName().equals("EmptyPath");
	}
	
	/**
	 * This method overrides the toString method inherited from the object class.
	 */
	@Override
	public String toString(){
		return "empty-path";
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#converse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public Path converse() {
		return this;
	}
	
	public LinkedList<Relation> firstRelations() {
		return new LinkedList<Relation>();
			
		}

}
