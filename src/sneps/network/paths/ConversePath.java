/**
 * @className ConversePath.java
 * 
 * @ClassDescription A converse path exists between two nodes A and B, 
 * 	if node A can be reached after following the converse of the path 
 * 	specified in the converse path starting at B. This class extends 
 *  Path.
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

public class ConversePath extends Path implements Serializable{
	
	/**
	 * A path that its converse is to be followed. 
	 * This path can be of any type.
	 */
	private Path path;
	
	/**
	 * The constructor of this class.
	 * 
	 * @param path
	 * 			the path defined in the converse path.
	 */
	public ConversePath(Path path){
		this.path = path;
	}
	
	/**
	 * 
	 * @return the path defined in the current converse
	 * 	path.
	 */
	public Path getPath(){
		return this.path;
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#follow(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> follow(Node node, PathTrace trace, Context context) {
		return this.path.followConverse(node, trace, context);
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#followConverse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public LinkedList<Object[]> followConverse(Node node, PathTrace trace, Context context) {
		return this.path.follow(node, trace, context);
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#clone(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public ConversePath clone() {
		return new ConversePath(this.path.clone());
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#equals(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public boolean equals(Object obj) {
		if(! obj.getClass().getSimpleName().equals("ConversePath"))
			return false;
		ConversePath conPath = (ConversePath) obj;
		if(! conPath.getPath().equals(this.path))
			return false;
		return true;
	}
	
	/**
	 * This method overrides the toString method inherited from the
	 * Object class.
	 */
	@Override
	public String toString(){
		return "Converse("+this.path.toString()+")";
	}

	/** (non-Javadoc)
     * @see sneps.Paths.Path#converse(sneps.Nodes.Node, sneps.network.PathTrace, SNeBR.Context)
     */
	@Override
	public Path converse() {
		return this.path;
	}
	
	public LinkedList<Relation> firstRelations() {
		return new LinkedList<Relation>();
		//TODO
			
		}

}
